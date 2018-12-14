package com.teaminternational.coe.pages;

import com.codeborne.selenide.SelenideElement;
import com.teaminternational.coe.utils.PropertiesContext;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

/**
 * Autocomplete Page class that emulates entering data and select value from list in autocomplete field.<br>
 * You can define set of locators in uiMap.properties file, for example:<br>
 * autocomplete.stateInput=".selector1"<br>
 * autocomplete.autocompleteBox=".selector2"<br>
 * autocomplete2.stateInput=".selector3"<br>
 * autocomplete2.autocompleteBox=".selector4"<br>
 * And you can initiate different autocomplete pages using parameter in constructor, for example:<br>
 * AutocompletePage autocomplete = new AutocompletePage();//you don't need to specify default parameter "autocomplete"<br>
 * AutocompletePage autocomplete = new AutocompletePage("autocomplete2");//specify "autocomplete2" to use "autocomplete.stateInput" and "autocomplete.autocompleteBox"<br>
 * Now you can use our methods:<br>
 * autocomplete.fill("Calif");
 * autocomplete.selectFromAutocompleteBox("California");
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
public class AutocompletePage extends AbstractPage {

    /**
     * Web element for State input
     */
    public SelenideElement stateInput;
    /**
     * Web element for autocomplete suggestions
     */
    public SelenideElement autocompleteBox;
    /**
     * String selector to find Web element for autocomplete suggestions
     */
    public String autocompleteBoxSelector;

    /**
     * Constructor call initialization method with default locatorsSet
     */
    public AutocompletePage() {
        this.initAutocomplete("autocomplete");
    }

    /**
     * Constructor call initialization method with defined in parameter locatorsSet
     *
     * @param locatorsSet name of collection of properties from properties context for autocomplete class
     */
    public AutocompletePage(String locatorsSet) {
        this.initAutocomplete(locatorsSet);
    }

    /**
     * Initialize two fields of class using locators from properties context
     *
     * @param locatorsSet name of collection of properties from properties context for autocomplete class
     */
    private void initAutocomplete(String locatorsSet) {
        this.stateInput = $(PropertiesContext.getInstance().getProperty(locatorsSet+".stateInput"));
        this.autocompleteBoxSelector = PropertiesContext.getInstance().getProperty(locatorsSet+".autocompleteBox");
    }

    /**
     * Method put 'text' parameter into state input
     *
     * @param text proposed text string
     */
    public void fill(String text) {
        stateInput.click();
        autocompleteBox = $(autocompleteBoxSelector);
        stateInput.setValue(text);
    }

    /**
     * Selects the state from the suggestions box
     *
     * @param state full text of which you want to select from list
     */
    public void selectFromAutocompleteBox(String state) {
        autocompleteBox = $(autocompleteBoxSelector);
        autocompleteBox.find(byText(state)).click();
    }
}
