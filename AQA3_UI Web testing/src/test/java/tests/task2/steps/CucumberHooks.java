package tests.task2.steps;

import com.codeborne.selenide.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class CucumberHooks {
	@Before
	public void setup() {
// Считываем параметры, переданные из Gradle, или используем дефолтные значения
		Configuration.baseUrl = System.getProperty("baseUrl", "https://www.saucedemo.com");
		Configuration.browser = System.getProperty("browser", "chrome");
		Configuration.browserSize = System.getProperty("browserSize", "1600x900");
		Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
		Configuration.timeout = 10000;
	}

	@After
	public void tearDown() {
		clearBrowserCookies();
		clearBrowserLocalStorage();
		executeJavaScript("window.sessionStorage.clear();");
		closeWebDriver();
	}
}