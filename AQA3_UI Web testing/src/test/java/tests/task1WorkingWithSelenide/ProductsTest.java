package tests.task1WorkingWithSelenide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тесты страницы товаров")
public class ProductsTest extends TestBase {

	@BeforeEach
	void setUp() {
		loginBeforeTest();
	}

	@Test
	@DisplayName("Проверка добавления товара в корзину")
	void addProductToCartTest() {
		// Arrange
		productsPage.checkPageIsLoaded();

		// Act & Assert
		productsPage
				.addProductToCart("Sauce Labs Backpack")
				.checkCartBadgeCount("1");
	}

	@Test
	@DisplayName("Проверка добавления нескольких товаров в корзину")
	void addThreeProductToCartTest() {
		// Arrange
		productsPage.checkPageIsLoaded();

		// Act & Assert
		productsPage
				.addProductToCart("Sauce Labs Backpack")
				.addProductToCart("Sauce Labs Fleece Jacket")
				.addProductToCart("Test.allTheThings() T-Shirt (Red)")
				.checkCartBadgeCount("3");
	}
}