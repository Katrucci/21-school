plugins {
    id("java")
    id("io.qameta.allure") version "2.11.2"
}

group = "AQA3_UI"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.3")

    // Logging
    testImplementation("org.slf4j:slf4j-simple:2.0.16")

    // Selenide
    // Source: https://mvnrepository.com/artifact/com.codeborne/selenide
    implementation("com.codeborne:selenide:7.14.0")
    testImplementation("net.java.dev.jna:jna:5.14.0")

    // Cucumber
    testImplementation("io.cucumber:cucumber-java:7.18.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.18.1")
    testImplementation("io.cucumber:cucumber-junit:7.18.1")



    // Allure
    testImplementation("io.qameta.allure:allure-cucumber7-jvm:2.27.0")
    implementation("io.qameta.allure:allure-junit5:2.27.0")
}


tasks.test {
    useJUnitPlatform()
      testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath


        systemProperty("browser", project.findProperty("browser") ?: "chrome")
        systemProperty("headless", project.findProperty("headless") ?: "false")
        systemProperty("browserSize", project.findProperty("browserSize") ?: "1600x900")
        systemProperty("baseUrl", project.findProperty("baseUrl") ?: "https://www.saucedemo.com")
    systemProperty("timeout", project.findProperty("timeout") ?: "10000")

    jvmArgs("-Dbrowser=${project.findProperty("browser") ?: "chrome"}")
    jvmArgs("-Dheadless=${project.findProperty("headless") ?: "false"}")
    jvmArgs("-DbrowserSize=${project.findProperty("browserSize") ?: "1600x900"}")
    jvmArgs("-Dtimeout=${project.findProperty("timeout") ?: "10000"}")

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }

    allure {
        version.set("2.27.0")
    }

    }



