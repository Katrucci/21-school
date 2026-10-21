package pagesManualSolutionCucumber;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CheckoutPage {
	private final SelenideElement pageTitle = $("[data-test='title']");
	private final SelenideElement firstNameInput = $("[data-test='firstName']");
	private final SelenideElement lastNameInput = $("[data-test='lastName']");
	private final SelenideElement zipInput = $("[data-test='postalCode']");
	private final SelenideElement continueButton = $("#continue");
	private final SelenideElement 	finishButton = $("#finish");

	public pagesManualSolutionCucumber.CheckoutPage checkPageIsLoaded() {
		pageTitle.shouldHave(text("Checkout: Your Information"));
		return this;
	}

	public pagesManualSolutionCucumber.CheckoutPage fillCheckoutForm(String firstName, String lastName, String zip) {
		firstNameInput.setValue(firstName);
		lastNameInput.setValue(lastName);
		zipInput.setValue(zip);
		return this;
	}

	public pagesManualSolutionCucumber.CheckoutPage clickContinue() {
		continueButton.shouldBe(visible)
				.click();
		return this;
	}

	public pagesManualSolutionCucumber.CheckoutPage checkOverviewPageIsLoaded() {
		pageTitle.shouldHave(text("Checkout: Overview"));
		return this;
	}

	public pagesManualSolutionCucumber.CheckoutPage clickFinish() {
		finishButton
				.shouldBe(visible)
				.click();
		return this;
	}

	public pagesManualSolutionCucumber.CheckoutPage checkOrderComplete() {
		pageTitle
				.shouldBe(visible)
				.shouldHave(text("Checkout: Complete!"));
		return this;
	}
}