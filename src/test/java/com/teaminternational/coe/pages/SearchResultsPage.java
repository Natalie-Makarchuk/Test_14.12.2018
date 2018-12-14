package com.teaminternational.coe.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.teaminternational.coe.utils.PropertiesContext;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Search Results Page class that emulates collections of elements with results of search on the page.<br>
 * A subclass of HomePage with additional methods for testing Search Results.
 * You can define set of locators in uiMap.properties file, for example:<br>
 * searchresults.results=".selector1"<br>
 * searchresults2.results=".selector2"<br>
 * And you can initiate different autocomplete pages using parameter in constructor, for example:<br>
 * SearchResultsPage results = new SearchResultsPage();//you don't need to specify default parameter "searchresults"<br>
 * SearchResultsPage results = new SearchResultsPage("searchresults2");//specify "autocomplete2" to use "searchresults2.results"<br>
 * Now you can use our methods:<br>
 * results.getResult(0);
 * results.checkResultsSizeIsAtLeast(10);
 * results.checkResultHasTest(0,"Selenide");
 * results.getResults();
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 * @see HomePage
 */
public class SearchResultsPage extends HomePage {
    /**
     * Collection of web elements with search results
     */
    private ElementsCollection results;

    /**
     * Constructor call initialization method with default locatorsSet
     */
    public SearchResultsPage() {
        this.initSearchResultsPage("searchresults");
    }

    /**
     * Constructor call initialization method with defined in parameter locatorsSet
     *
     * @param locatorsSet name of collection of properties from properties context for autocomplete class
     */
    public SearchResultsPage(String locatorsSet) {
        this.initSearchResultsPage(locatorsSet);
    }

    /**
     * Initialize two fields of class using locators from properties context
     *
     * @param locatorsSet name of collection of properties from properties context for autocomplete class
     */
    private void initSearchResultsPage(String locatorsSet) {
        this.results = $$(PropertiesContext.getInstance().getProperty(locatorsSet+".results"));
    }

    /**
     * Get certain result (index) from collection of results
     *
     * @param index integer index
     * @return webElement from results collection with index index
     */
    public SelenideElement getResult(int index) {
        return results.get(index);
    }

    /**
     * Assert that results field has certain amount of results (expectedSize)
     *
     * @param expectedSize expected amount of results
     */
    public void checkResultsSizeIsAtLeast(int expectedSize) {
        results.shouldHave(sizeGreaterThan(expectedSize));
    }

    /**
     * Assert that certain element of results has certain text
     *
     * @param index index of elents in results field that must be checked
     * @param expectedText text that must be contained in verified element
     */
    public void checkResultHasTest(int index, String expectedText) {
        results.get(index).shouldHave(text(expectedText));
    }

    /**
     * Get results field
     *
     * @return results collection
     */
    public ElementsCollection getResults() {
        return results;
    }
}
