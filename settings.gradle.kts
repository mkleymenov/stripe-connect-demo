pluginManagement {
    repositories {
        // Repositories for the Spring Boot Gradle plugin
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/snapshot")
        gradlePluginPortal()
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "stripe-connect-demo"
include("server")
