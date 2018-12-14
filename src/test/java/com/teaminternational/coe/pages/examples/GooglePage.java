package com.teaminternational.coe.pages.examples;

import com.codeborne.selenide.SelenideElement;
import com.teaminternational.coe.pages.HomePage;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;

/**
 * Example of GooglePage class. Has only one additional method except basics - emulates search some texts using SelenideElement methods.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 * @see HomePage
 */
public class GooglePage extends HomePage {
    /**
     * Web element for search input
     */
    private SelenideElement searchField = $(byName("q"));


    /**
     * Input search text into search input end press enter using selenide
     *
     * @param text string for searching
     */
    public void searchFor(String text) {
        searchField.val(text).pressEnter();
    }
}
