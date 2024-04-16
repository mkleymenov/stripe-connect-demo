plugins {
    application
    // Spring Gradle plugin
    id("org.springframework.boot") version "3.3.0-SNAPSHOT"
}

apply {
    // Spring Dependency Management plugin
    plugin("io.spring.dependency-management")
}

repositories {
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // H2 database
    runtimeOnly("com.h2database:h2")

    // Stripe SDK
    implementation("com.stripe:stripe-java:25.2.0")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.9.3")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("com.dataart.itkonekt.App")
}
