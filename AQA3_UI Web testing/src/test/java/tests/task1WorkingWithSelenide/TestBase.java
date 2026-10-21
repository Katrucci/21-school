package tests.task1WorkingWithSelenide;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import pagesManualSolution.CartPageNoCucumber;
import pagesManualSolution.LoginPageNoCucumber;
import pagesManualSolution.ProductsPageNoCucumber;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class TestBase {

	protected static final String LOGIN_PATH = "/";
	protected LoginPageNoCucumber loginPage = new LoginPageNoCucumber();
	protected ProductsPageNoCucumber productsPage = new ProductsPageNoCucumber();


	@BeforeAll
	static void setup() {
// Считываем параметры, переданные из Gradle, или используем дефолтные значения
		Configuration.baseUrl = System.getProperty("baseUrl", "https://www.saucedemo.com");
		Configuration.browser = System.getProperty("browser", "chrome");
		Configuration.browserSize = System.getProperty("browserSize", "1600x900");
		Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
		Configuration.timeout = 5000;
	}

	@AfterAll
	static void tearDownAll() {
		closeWebDriver();
	}

	@AfterEach
	void tearDown() {
		clearBrowserCookies();
		clearBrowserLocalStorage();
		executeJavaScript("window.sessionStorage.clear();");
	}
	protected void loginBeforeTest() {
		open(LOGIN_PATH);
		performLogin("standard_user", "secret_sauce");
		productsPage.checkPageIsLoaded();
	}



	protected void performLogin(String username, String password) {
		loginPage.login(username, password);
	}

	protected void assertErrorMessageContains(String expectedText) {
		$("[data-test='error']")
				.shouldBe(visible, Duration.ofSeconds(5))
				.shouldHave(text(expectedText));
	}
}