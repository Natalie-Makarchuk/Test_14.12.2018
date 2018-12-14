package com.teaminternational.coe.pages.examples;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import com.teaminternational.coe.pages.HomePage;

/**
 * Example of Page class that uses some SelenideElement methods.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
public class SelenideExamplePageObject extends HomePage {

    /**
     * Web element with id = text
     */
    SelenideElement description = $("#text");
    /**
     * Web element with id = button
     */
    SelenideElement submitBtn = $("#button");
    /**
     * Web element with id = label
     */
    SelenideElement productName = $("#label");
    /**
     * Web element with id = link
     */
    SelenideElement followMe = $("#link");
    /**
     * Web element with id = textfield
     */
    SelenideElement passwordField = $("#textfield");

    /**
     * Use some of selenide method on all 5 web element fields of class SelenideExamplePageObject
     * Example od usage selenide methods.
     */
    public void useElements() {
        description.getText();
        submitBtn.click();
        productName.shouldBe(visible);
        followMe.click();
        passwordField.setValue("password");
    }
}
