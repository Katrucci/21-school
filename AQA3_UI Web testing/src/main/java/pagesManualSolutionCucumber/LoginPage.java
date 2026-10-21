package pagesManualSolutionCucumber;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
	// Описываем элементы
	private final SelenideElement usernameField = $("#user-name");
	private final SelenideElement passwordField = $("#password");
	private final SelenideElement loginButton = $("#login-button");
	private final SelenideElement errorMessage = $("[data-test='error']");

	// Метод для логина
	public void login(String username, String password) {
		usernameField.setValue(username);
		passwordField.setValue(password);
		loginButton.click();
	}

	public LoginPage checkErrorMessage(String expectedError) {
		errorMessage
				.shouldBe(visible, Duration.ofSeconds(5))
				.shouldHave(text(expectedError));
		return this;
	}
}
