import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("net.kyori:indra-common:3.0.1")
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.7.0"
    id("net.kyori.indra.checkstyle") version "3.0.1"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}

group = "fr.aytronn"
version = System.getenv("PROJECT_VERSION") ?: (findProperty("projectVersion") ?: "")

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(19))
        }
    }

    kotlin {
        jvmToolchain {
            this.languageVersion.set(JavaLanguageVersion.of(19))
        }
    }
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    apply(plugin = "java-library")
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(19))
    }
    group = "fr.aytronn"
    version = System.getenv("PROJECT_VERSION") ?: (findProperty("projectVersion") ?: "")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "19"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
    apply(plugin = "net.kyori.indra.checkstyle")
}

tasks.named("publish") {
    dependsOn(":ModuloApi:publish")
}