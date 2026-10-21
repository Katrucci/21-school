package pagesManualSolution;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CartPageNoCucumber {
	private final SelenideElement pageTitle = $("[data-test='title']");
	private final SelenideElement checkoutButton = $("#checkout");
	private final SelenideElement cartItem = $(".cart_item");
	private final SelenideElement cartBadge = $(".shopping_cart_badge");

	public CartPageNoCucumber checkPageIsLoaded() {
		pageTitle
				.shouldBe(visible)
				.shouldHave(text("Your Cart"));
		return this;
	}

	public CartPageNoCucumber checkItemInCart() {
		cartItem
				.shouldBe(visible);
		return this;
	}

	public CheckoutPageNoCucumber goToCheckout() {
		checkoutButton
				.shouldBe(visible)
				.click();
		return new CheckoutPageNoCucumber();
	}

	private SelenideElement getRemoveButton(String productName) {
		String dataTestId = "remove-" + productName.toLowerCase().replace(" ", "-");
		return $("[data-test='" + dataTestId + "']");
	}

	public CartPageNoCucumber removeProduct(String productName) {
		getRemoveButton(productName)
				.shouldBe(visible)
				.click();
		return this;
	}

	public CartPageNoCucumber checkCartIsEmpty() {
		cartItem.shouldNotBe(visible);
		cartBadge.shouldNotBe(visible);
		return this;
	}
}