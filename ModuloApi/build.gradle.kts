import org.gradle.internal.impldep.org.bouncycastle.cms.RecipientId.password
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("java")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("org.javacord:javacord:3.7.0")
    api("com.vdurmont:emoji-java:5.1.1")
    api("org.yaml:snakeyaml:1.27")
    api("com.google.code.gson:gson:2.8.9")
    api("io.netty:netty-all:4.1.86.Final")

    api("org.apache.logging.log4j:log4j-slf4j-impl:2.9.0")
    api("com.google.guava:guava:31.1-jre")

    api("org.mongodb:mongodb-driver-sync:4.6.0")
    api("org.mongodb:bson:4.6.0")
    api("org.mongodb:mongodb-driver-core:4.6.0")

    api("fastutil:fastutil:5.0.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.named("cleanTest") { group = "verification" }


tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(19)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "aydustries"
            url = uri("https://nexus.aytronn.com/repository/aydustries/")
            credentials {
                username = project.findProperty("nexus_username") as String? ?: System.getenv("NEXUS_USERNAME") ?: ""
                password = project.findProperty("nexus_password") as String? ?: System.getenv("NEXUS_PASSWORD") ?: ""
            }
        }
    }
    publications {
        register<MavenPublication>("mavenJava") {
            version = System.getenv("PROJECT_VERSION") ?: (findProperty("projectVersion").toString() ?: "")
            groupId = "fr.aytronn"
            artifactId = "modulo-api"
            pom {
                name.set("Modulo")
                description.set("You can see it as a minecraft server with plugins. Modulo will load the modules you added in the modules folder.")
                url.set("https://github.com/aydustries/Modulo")
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://github.com/aydustries/Modulo/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("aytronn")
                        email.set("aytronn18@gmail.com")
                    }
                }
            }
            from(components["java"])
        }
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
