package tests.task1WorkingWithSelenide;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@DisplayName("Тесты авторизации")
public class LoginTest extends TestBase {

	@Test
	@DisplayName("Успешная авторизация с валидными данными")
	void loginSuccess() {
		open(LOGIN_PATH);
		performLogin("standard_user", "secret_sauce");
		$("[data-test='title']")
				.shouldBe(visible)
				.shouldHave(text("Products"));
	}

	@Test
	@DisplayName("Негативный тест: Авторизация с неверным паролем")
	void loginWrongPassword() {
		open(LOGIN_PATH);
		performLogin("standard_user", "pass");
		assertErrorMessageContains("Epic sadface: Username and password do not match");
	}

	@Test
	@DisplayName("Негативный тест: Авторизация с пустыми полями")
	void loginEmptyFields() {
		open(LOGIN_PATH);
		performLogin("", "");
		assertErrorMessageContains("Epic sadface: Username is required");
	}
}