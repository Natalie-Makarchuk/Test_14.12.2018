package com.teaminternational.coe.utils.webdriver;

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class extends selenium class By and implements method that allow retrieve WebElements by jQuery selector.
 * Method checks if jQuery added on the page and if jQuery wasn't found it adds jQuery to the page
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see By
 */
public class ByJquery extends By implements Serializable {
    /**
     * Variable for setting of jQuery selector
     */
    private final String query;

    /**
     * Verify that selector is not null and define selector to the private variable
     *
     * @param query string jquery selector
     */
    public ByJquery(String query) {
        checkNotNull(query, "Cannot find elements with a null JQuery expression.");
        this.query = query;
    }

    /**
     * check if context is a WebDriver or WrapsDriver then return WebDriver
     *
     * @param context A context to use to find the element.
     * @return WebDriver object
     * @throws IllegalStateException if context is not WebDriver or WrapsDriver
     */
    private static WebDriver getWebDriverFromSearchContext(SearchContext context) {
        if (context instanceof WebDriver) {
            return (WebDriver) context;
        }
        if (context instanceof WrapsDriver) {
            return ((WrapsDriver) context).getWrappedDriver();
        }
        throw new IllegalStateException("Can't access a WebDriver instance from the current search context.");
    }

    /**
     * Check if jquery already included on the page.
     * TODO: Some JavaScript test for a JQuery object.
     *
     * @param driver WebDriver used for tests
     * @return boolean value , true id jquery already included
     */
    private static boolean isJQueryInThisPage(WebDriver driver) {
        // TODO Some JavaScript test for a JQuery object.
        return true;
    }

    /**
     * Inject jQuery to the page using JavascriptExecutor class
     *
     * @param driver WebDriver used for tests
     * @see JavascriptExecutor
     */
    private static void injectJQuery(WebDriver driver) {
        final JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("dollar = null; if(typeof jQuery == \"undefined\") {" +
                "if(typeof $ != \"undefined\") {dollar = $;}" +
                " var headID = document.getElementsByTagName(\"head\")[0];" +
                "var newScript = document.createElement('script');" +
                "newScript.type = 'text/javascript';" +
                "newScript.src = 'https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js';" +
                "headID.appendChild(newScript);}");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        System.out.println("... Injecting jQuery ...");
        Function<WebDriver, Boolean> jQueryAvailable = new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver input) {
                return (Boolean) js.executeScript("return (typeof jQuery != \"undefined\")");
            }
        };
        try {
            wait.until(jQueryAvailable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //restore saved '$' if any were saved
        js.executeScript("if(dollar != null) {$ = dollar}");
    }

    /**
     * Implements findElements method to allow find elements by jQuery selectors
     *
     * @param context A context to use to find the element.
     * @return list of found WebElements on the page
     */
    @Override
    public List<WebElement> findElements(SearchContext context) {
        WebDriver driver = getWebDriverFromSearchContext(context);

        if (!isJQueryInThisPage(driver)) {
            injectJQuery(driver);
        }

        return new ByJavaScript("return $(\"" + query + "\")").findElements(context);
    }

    /**
     * Implements toString method for string presentation of query for using in logger, etc.
     *
     * @return string with query
     */
    @Override
    public String toString() {
        return "By.jQuery: $(\"" + query + "\")";
    }
}

