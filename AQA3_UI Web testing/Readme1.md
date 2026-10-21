# Запуск тестов через Gradle

Все тесты и наборы тестов (test suite) запускаются из корневой директории проекта

## Очистка сборки перед запуском
./gradlew --stop
./gradlew clean

## Запуск всех тестов
`для запуска 21 версии jdk`<br>

$env:JAVA_HOME = "C:\Program Files\Java\corretto-21.0.9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

`для запуска 21 версии jdk команды Linux `<br>
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
chmod +x gradlew     `Даем права на выполнение`
<br>
==============================================
### Запуск JUnit тестов
./gradlew test --continue
./gradlew test
<br>
==============================================
### Меняем браузер
//без точки и слэша!!
gradle test -Pbrowser=firefox
<br>
==============================================
### Только CartTest
./gradlew test --tests "tests.task1WorkingWithSelenide.CartTest"

### Только LoginTest
./gradlew test --tests "tests.task1WorkingWithSelenide.LoginTest"

### Только ProductsTest 
/
./gradlew test --tests "tests.task1WorkingWithSelenide.ProductsTest"
<br>
-------------------------------------------------------------
### Запуск Cucumber тестов
/
./gradlew test --tests "tests.task2.CucumberRunner"
<br>
======================================
<br>
### ALLURE
./gradlew clean test --tests "tests.task2.CucumberRunner" allureReport
./gradlew clean test --tests "tests.task1WorkingWithSelenide.*" allureReport
./gradlew clean test allureReport  //запустить все тесты.

./gradlew allureReport

### Запуск тестов с дебаг-логами Gradle
./gradlew clean testngTest --info --stacktrace



<br>

# UI Web Testing (AQA) 

Проект по автоматизации UI-тестирования веб-приложения [SauceDemo](https://www.saucedemo.com/) с использованием стека: **Selenide**, **Cucumber**, **JUnit 5** и **Allure**.
В рамках проекта реализован паттерн Page Object, BDD-подход с использованием синтаксиса Gherkin и интеграция с Allure для построения наглядных отчетов. Также проведено сравнение ручного написания кода и генерации с помощью ИИ.

##  Технологии

| Категория | Инструмент |
| --- | --- |
| Язык | Java 21 |
| Сборка | Gradle |
| Тестовый фреймворк | JUnit 5 |
| UI-автоматизация | Selenide |
| BDD | Cucumber (Gherkin) |
| Отчетность | Allure |

##  Структура проекта

```text
src/
 ├── main/java/
 │   ├── pagesManualSolution/             # Page Objects для JUnit тестов (Task 1)
 │   │   ├── LoginPageNoCucumber.java
 │   │   ├── ProductsPageNoCucumber.java
 │   │   ├── CartPageNoCucumber.java
 │   │   └── CheckoutPageNoCucumber.java
 │   │
 │   ├── pagesManualSolutionCucumber/     # Page Objects для Cucumber тестов (Task 3)
 │   │   ├── LoginPage.java
 │   │   ├── ProductsPage.java
 │   │   ├── CartPage.java
 │   │   └── CheckoutPage.java
 │   │
 │   └── pagesAiAssistedSolution/         # Page Objects, сгенерированные ИИ
 │       └── LoginPage.java
 │
 └── test/
     ├── java/
     │   ├── tests/task1WorkingWithSelenide/  # JUnit 5 тесты (Task 1 & 2)
     │   │   ├── TestBase.java
     │   │   ├── LoginTest.java
     │   │   ├── ProductsTest.java
     │   │   └── CartTest.java
     │   │
     │   └── tests/task2/                 # Cucumber тесты (Task 3)
     │       ├── CucumberRunner.java
     │       └── steps/
     │           ├── CucumberHooks.java
     │           └── Steps.java
     │
     └── resources/
         └── features/                    # Feature-файлы (Gherkin)
             └── saucedemo.feature
```             

## Покрытые сценарии (Gherkin)

В рамках Cucumber-тестов реализованы следующие сценарии:
- **Авторизация:** Успешный вход, вход с неверным паролем, вход с пустыми полями.
- **Товары:** Добавление одного и нескольких товаров в корзину, проверка счетчика (badge).
- **Корзина и оформление заказа:** Успешное оформление полного цикла, удаление товара, негативный сценарий с пустыми полями при оформлении.

##  Ключевые термины и паттерны

**Selenide:**
- `$()` / `$$()` — поиск элементов.
- `shouldBe()`, `shouldHave()` — встроенные проверки с автоматическим ожиданием (избавляют от `Thread.sleep()`).
- `setValue()`, `click()` — действия над элементами.
- `Configuration` — глобальная настройка браузера и таймаутов.

**Page Object Pattern:**
- Инкапсуляция локаторов и методов взаимодействия в отдельных классах.
- Fluent Interface (возврат `this`) для цепочки вызовов в тестах.

**Cucumber (Gherkin):**
- Ключевые слова: `Допустим` (Given), `Когда` (When), `Тогда` (Then), `И` (And).
- `@Допустим`, `@Когда`, `@Тогда` — аннотации для связки шагов из feature-файла с методами в Java (`Steps.java`).
- `DataTable` — передача табличных данных в шаги (используется для заполнения формы Checkout).

**Allure:**
- Интеграция через `allure-gradle` плагин.
- Автоматический сбор скриншотов, page source и логов при падении тестов.
