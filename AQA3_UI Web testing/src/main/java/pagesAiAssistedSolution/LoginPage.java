package pagesAiAssistedSolution;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object для страницы логина.
 * Использует библиотеку Selenide и паттерн Fluent Interface (возврат this)
 * для возможности цепочки вызовов в тестах.
 */
public class LoginPage {

	// Локаторы элементов страницы
	private final SelenideElement usernameInput = $(byId("user-name"));
	private final SelenideElement passwordInput = $(byId("password"));
	private final SelenideElement loginButton = $(byClassName("submit-button"));

	// Локатор сообщения об ошибке.
	// На сайте SauceDemo оно имеет класс "error-message-container".
	// Если на вашем проекте селектор другой, измените его здесь.
	private final SelenideElement errorMessage = $(".error-message-container");

	/**
	 * Выполняет успешный вход в систему.
	 *
	 * @param username имя пользователя
	 * @param password пароль
	 * @return экземпляр класса следующей страницы (например, InventoryPage).
	 *         Тип возвращаемого значения следует заменить на ваш реальный Page Object товаров.
	 */
	public <T> T loginSuccessfully(String username, String password) {
		usernameInput.setValue(username);
		passwordInput.setValue(password);
		loginButton.click();

		// Пример возврата: return page(InventoryPage.class);
		// Замените Void на нужный тип или используйте wildcard, если страница не описана отдельным PO:
		@SuppressWarnings("unchecked")
		T result = (T) new Object();
		return result;
	}

	/**
	 * Пытается выполнить вход с неверными данными для проверки обработки ошибок.
	 *
	 * @param username имя пользователя
	 * @param password пароль
	 * @return текущий экземпляр LoginPage для цепочки проверок
	 */
	public LoginPage loginWithInvalidData(String username, String password) {
		usernameInput.setValue(username);
		passwordInput.setValue(password);
		loginButton.click();
		return this;
	}

	/**
	 * Проверяет, что сообщение об ошибке отображается и содержит ожидаемый текст.
	 *
	 * @param expectedText ожидаемый текст ошибки (например, "Epic sadface: Username and password do not match any user in this service")
	 * @return текущий экземпляр LoginPage
	 */
	public LoginPage shouldSeeErrorMessage(String expectedText) {
		errorMessage.shouldBe(Condition.visible).shouldHave(Condition.text(expectedText));
		return this;
	}

	/**
	 * Проверка того, что страница открыта и готова к работе.
	 * Обычно проверяется наличие ключевого элемента.
	 */
	public void validatePage() {
		usernameInput.shouldBe(Condition.visible);
	}
}