package pagesManualSolutionCucumber;

import com.codeborne.selenide.SelenideElement;
import pagesManualSolutionCucumber.CheckoutPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CartPage {
	private final SelenideElement pageTitle = $("[data-test='title']");
	private final SelenideElement checkoutButton = $("#checkout");
	private final SelenideElement cartItem = $(".cart_item");
	private final SelenideElement cartBadge = $(".shopping_cart_badge");

	public pagesManualSolutionCucumber.CartPage checkPageIsLoaded() {
		pageTitle
				.shouldBe(visible)
				.shouldHave(text("Your Cart"));
		return this;
	}

	public pagesManualSolutionCucumber.CartPage checkItemInCart() {
		cartItem
				.shouldBe(visible);
		return this;
	}

	public CheckoutPage goToCheckout() {
		checkoutButton
				.shouldBe(visible)
				.click();
		return new CheckoutPage();
	}

	private SelenideElement getRemoveButton(String productName) {
		String dataTestId = "remove-" + productName.toLowerCase().replace(" ", "-");
		return $("[data-test='" + dataTestId + "']");
	}

	public pagesManualSolutionCucumber.CartPage removeProduct(String productName) {
		getRemoveButton(productName)
				.shouldBe(visible)
				.click();
		return this;
	}

	public pagesManualSolutionCucumber.CartPage checkCartIsEmpty() {
		cartItem.shouldNotBe(visible);
		cartBadge.shouldNotBe(visible);
		return this;
	}
}
