package tests.task1WorkingWithSelenide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pagesManualSolution.CartPageNoCucumber;
import pagesManualSolution.CheckoutPageNoCucumber;

@DisplayName("Тесты оформления заказа")
public class CartTest extends TestBase {

	@BeforeEach
	void setUp() {
		loginBeforeTest();
		productsPage.addProductToCart("Sauce Labs Backpack");
	}

	@Test
	@DisplayName("Успешное оформление заказа полным циклом")
	void successfulCheckoutTest() {

		CartPageNoCucumber cartPage = productsPage.openCart();
		cartPage.checkItemInCart();

		CheckoutPageNoCucumber checkoutPage = cartPage.goToCheckout();
		checkoutPage.fillCheckoutForm("Ivan", "Ivanov", "12345");
		checkoutPage.clickContinue();

		checkoutPage.checkOverviewPageIsLoaded();
		checkoutPage.clickFinish();
		checkoutPage.checkOrderComplete();
	}

	@Test
	@DisplayName("Оформление заказа без заполнения полей")
	void checkoutWithoutFieldsTest() {

		CartPageNoCucumber cartPage = productsPage.openCart();
		CheckoutPageNoCucumber checkoutPage = cartPage.goToCheckout();

		checkoutPage.fillCheckoutForm("", "", "");
		checkoutPage.clickContinue();

		assertErrorMessageContains("Error: First Name is required");
	}

	@Test
	@DisplayName("Удаление товара из корзины")
	void removeProductFromCartTest() {

		CartPageNoCucumber cartPage = productsPage.openCart();
		cartPage.checkItemInCart();

		cartPage.removeProduct("Sauce Labs Backpack");

		cartPage.checkCartIsEmpty();
	}
}