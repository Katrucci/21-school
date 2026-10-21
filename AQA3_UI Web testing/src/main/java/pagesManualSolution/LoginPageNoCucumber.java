package pagesManualSolution;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;

public class LoginPageNoCucumber {
	// Описываем элементы
	private final SelenideElement usernameField = $("#user-name");
	private final SelenideElement passwordField = $("#password");
	private final SelenideElement loginButton = $("#login-button");

	// Метод для логина
	public void login(String username, String password) {
		usernameField.setValue(username);
		passwordField.setValue(password);
		loginButton.click();
	}
}
