//manual-solution

package tests.task2.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.cucumber.java.ru.И;
import pagesManualSolutionCucumber.CartPage;
import pagesManualSolutionCucumber.CheckoutPage;
import pagesManualSolutionCucumber.LoginPage;
import pagesManualSolutionCucumber.ProductsPage;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;

public class Steps {

	private LoginPage loginPage = new LoginPage();
	private ProductsPage productsPage = new ProductsPage();
	private CartPage cartPage = new CartPage();
	private CheckoutPage checkoutPage = new CheckoutPage();

	// ========== ПРЕДЫСТОРИЯ ==========

	@Допустим("пользователь открывает страницу логина")
	public void openLoginPage() {
		open("/");
	}

	// ========== АВТОРИЗАЦИЯ ==========

	@Когда("пользователь вводит логин {string} и пароль {string}")
	public void enterCredentials(String username, String password) {
		loginPage.login(username, password);
	}

	@И("нажимает кнопку входа")
	public void clickLoginButton() {
		// Логин уже включает нажатие кнопки в методе login()
	}

	@Тогда("открывается страница товаров")
	public void productsPageIsOpened() {
		productsPage.checkPageIsLoaded();
	}

	@И("отображается заголовок {string}")
	public void titleIsDisplayed(String expectedTitle) {
		productsPage.checkPageIsLoaded();
	}

	@Тогда("отображается сообщение об ошибке {string}")
	public void errorMessageIsDisplayed(String expectedError) {
		loginPage.checkErrorMessage(expectedError);
	}

	@Когда("пользователь авторизован как {string} с паролем {string}")
	public void userIsLoggedIn(String username, String password) {
		loginPage.login(username, password);
		productsPage.checkPageIsLoaded();
	}

	// ========== ТОВАРЫ ==========

	@И("добавляет товар {string} в корзину")
	public void addProductToCart(String productName) {
		productsPage.addProductToCart(productName);
	}

	@Тогда("счетчик корзины показывает {string}")
	public void cartBadgeShows(String expectedCount) {
		productsPage.checkCartBadgeCount(expectedCount);
	}

	@Тогда("счетчик корзины отсутствует")
	public void cartBadgeIsAbsent() {
		productsPage.checkCartBadgeIsAbsent();
	}

	// ========== КОРЗИНА ==========

	@И("переходит в корзину")
	public void goToCart() {
		cartPage = productsPage.openCart();
	}

	@И("товар присутствует в корзине")
	public void itemIsInCart() {
		cartPage.checkItemInCart();
	}

	@И("нажимает кнопку {string}")
	public void clickButton(String buttonText) {
		switch (buttonText) {
			case "Checkout":
				checkoutPage = cartPage.goToCheckout();
				break;
			case "Continue":
				checkoutPage.clickContinue();
				break;
			case "Finish":
				checkoutPage.clickFinish();
				break;
			default:
				throw new IllegalArgumentException("Неизвестная кнопка: " + buttonText);
		}
	}

	@И("удаляет товар {string} из корзины")
	public void removeProductFromCart(String productName) {
		cartPage.removeProduct(productName);
	}

	@Тогда("корзина пуста")
	public void cartIsEmpty() {
		cartPage.checkCartIsEmpty();
	}

	// ========== ОФОРМЛЕНИЕ ЗАКАЗА ==========

	@И("заполняет форму оформления заказа:")
	public void fillCheckoutForm(DataTable dataTable) {
		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
		Map<String, String> data = rows.get(0);
		checkoutPage.fillCheckoutForm(
				data.get("firstName"),
				data.get("lastName"),
				data.get("postalCode")
		);
	}

	@И("заполняет форму оформления заказа пустыми значениями")
	public void fillCheckoutFormWithEmptyValues() {
		checkoutPage.fillCheckoutForm("", "", "");
	}

	@Тогда("открывается страница обзора заказа")
	public void overviewPageIsOpened() {
		checkoutPage.checkOverviewPageIsLoaded();
	}

	@Тогда("отображается сообщение {string}")
	public void messageIsDisplayed(String expectedMessage) {
		checkoutPage.checkOrderComplete();
	}
}