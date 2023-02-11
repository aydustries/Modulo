import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.tag
import java.util.TreeSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.palantir.docker") version "0.22.1"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

val shadowImplementation: Configuration by configurations.creating
configurations["implementation"].extendsFrom(shadowImplementation)

dependencies {
    implementation(project(":ModuloApi"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(19)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    assemble {
        dependsOn(shadowJar)
    }

    configurations {
        artifacts {
            runtimeElements(shadowJar)
            apiElements(shadowJar)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("ModuloCore")
        archiveVersion.set(version)

        from(collectDependencies)
    }
}

val collectDependencies: Task by tasks.creating {
    val outputFile = File(temporaryDir, "deps.txt")
    outputs.file(outputFile)

    doLast {
        val deps = HashSet<String>()

        val artifactProcessor: (Configuration) -> Unit = { config ->
            config.resolvedConfiguration.firstLevelModuleDependencies.forEach dependencies@{ it ->
                if (it.configuration != "runtime" && it.configuration != "runtimeElements") {
                    return@dependencies
                }

                val parentDepString = it.module.toString()
                if (parentDepString.startsWith("project ")) {
                    return@dependencies
                }

                deps.add(parentDepString)

                it.children.forEach deps@{ resolvedDep ->
                    val depString = resolvedDep.module.toString()
                    if (depString.startsWith("project ")) {
                        return@deps
                    }

                    deps.add(depString)

                }
            }
        }

        rootProject.subprojects.forEach { subproject ->
            if (subproject.name != "DatabaseAPI") {
                return@forEach
            }
            artifactProcessor(subproject.configurations["runtimeClasspath"])
        }

        outputFile.createNewFile()
        println("Writing dependencies to ${outputFile.absolutePath}")
        outputFile.bufferedWriter().use { writer ->
            TreeSet(deps).forEach {
                writer.write(it)
                writer.newLine()
            }
        }
    }
}

repositories {
    mavenCentral()
}

docker {
    name = "modulo:${version}"
    files("./build/libs/ModuloCore.jar", "./start.sh")
    tag("DockerHub", "aytronn/modulo:${version}")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fr.aytronn.modulocore.ModuloCore"
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}