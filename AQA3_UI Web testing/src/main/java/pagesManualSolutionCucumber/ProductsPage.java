package pagesManualSolutionCucumber;

import com.codeborne.selenide.SelenideElement;
import pagesManualSolution.CartPageNoCucumber;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProductsPage {

	// Локаторы
	private final SelenideElement pageTitle = $("[data-test='title']");
	private final SelenideElement cartBadge = $(".shopping_cart_badge");
	private final SelenideElement shoppingCartLink = $(".shopping_cart_link");

	// Проверка загрузки страницы
	public pagesManualSolutionCucumber.ProductsPage checkPageIsLoaded() {
		pageTitle.shouldBe(visible).shouldHave(text("Products"));
		return this;
	}

	// Добавление товара в корзину
	public pagesManualSolutionCucumber.ProductsPage addProductToCart(String productName) {
		String dataTestId = "add-to-cart-" + productName.toLowerCase().replace(" ", "-");
		$("[data-test='" + dataTestId + "']").click();
		return this;
	}

	// Проверка счётчика корзины
	public pagesManualSolutionCucumber.ProductsPage checkCartBadgeCount(String expectedCount) {
		cartBadge.shouldBe(visible).shouldHave(text(expectedCount));
		return this;
	}

	public ProductsPage checkCartBadgeIsAbsent() {
		cartBadge.shouldNotBe(visible);
		return this;
	}
	public CartPage openCart() {
		shoppingCartLink
				.shouldBe(visible)
				.click();
		return new CartPage();
	}

}