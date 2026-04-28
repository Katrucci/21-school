plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // Основной стартер для REST API
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Стандартный стартер для тестирования
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Selenium Java - основная библиотека для управления браузером
    testImplementation("org.seleniumhq.selenium:selenium-java:4.41.0")

    // TestNG - фреймворк для запуска тестов (вместо стандартного JUnit)
    testImplementation("org.testng:testng:7.9.0")

    // Он сам подберет нужный драйвер под твою версию Chrome
    testImplementation("io.github.bonigarcia:webdrivermanager:5.7.0")

    implementation("org.postgresql:postgresql")

    // Spring Data JPA для работы с базой данных
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring Security для работы с фильтрами и авторизацией
    //это стандартная страница входа Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Шаблонизатор для HTML-страниц
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("org.springframework.boot:spring-boot-starter-validation")

}



tasks.withType<Test> {
    useTestNG()

    // Полезно для вывода логов теста в консоль
    testLogging {
        events("passed", "skipped", "failed")
    }
}

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }



