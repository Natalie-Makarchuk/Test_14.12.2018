package com.teaminternational.coe.utils.webdriver;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.testng.IAnnotationTransformer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Class extends selenium class By and implements method that allow retrieve WebElements by javascript function
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see By
 */
public class ByJavaScript extends By implements Serializable {
    /**
     * Variable for setting of JavaScript that must be executed
     */
    private final String script;

    /**
     * Verify that script is not null and define  private script variable
     *
     * @param script string with javascript code
     */
    public ByJavaScript(String script) {
        checkNotNull(script, "Cannot find elements with a null JavaScript expression.");
        this.script = script;
    }

    /**
     * check if context is a JavascriptExecutor or WrapsDriver then return WebDriver
     *
     * @param context A context to use to find the element.
     * @return JavascriptExecutor object
     * @throws IllegalStateException if context is not JavascriptExecutor or WrapsDriver
     * @see JavascriptExecutor
     */
    private static JavascriptExecutor getJavascriptExecutorFromSearchContext(SearchContext context) {
        if (context instanceof JavascriptExecutor) {
            // context is most likely the whole WebDriver
            return (JavascriptExecutor) context;
        }
        if (context instanceof WrapsDriver) {
            // context is most likely some WebElement
            WebDriver driver = ((WrapsDriver) context).getWrappedDriver();
            checkState(driver instanceof JavascriptExecutor, "This WebDriver doesn't support JavaScript.");
            return (JavascriptExecutor) driver;
        }
        throw new IllegalStateException("We can't invoke JavaScript from this context.");
    }

    /**
     * Verify response object and trying to get from it list of webElements
     *
     * @param response result of javascript execution
     * @return list of WebElement objects
     * @throws IllegalArgumentException if javascript returned not webElement
     * @see WebElement
     */
    @SuppressWarnings("unchecked")  // cast thoroughly checked
    private static List<WebElement> getElementListFromJsResponse(Object response) {
        if (response == null) {
            // no element found
            return Lists.newArrayList();
        }
        if (response instanceof WebElement) {
            // a single element found
            return Lists.newArrayList((WebElement) response);
        }
        if (response instanceof List) {
            // found multiple things, check whether every one of them is a WebElement
            checkArgument(
                    Iterables.all((List<?>) response, Predicates.instanceOf(WebElement.class)),
                    "The JavaScript query returned something that isn't a WebElement.");
            return (List<WebElement>) response;  // cast is checked as far as we can tell
        }
        if (response != null) {
            try {
                Map<Object, Object> result = (Map<Object, Object>) response;
                return result.values().stream().filter(i -> i instanceof WebElement).map(i -> (WebElement) i).collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Failed to get WebElements from map");
            }
        }
        throw new IllegalArgumentException("The JavaScript query returned something that isn't a WebElement. " + response.getClass().getName());
    }

    /**
     * Remove from list of WebElements all elements that are not a child of specified element
     *
     * @param elements list of WebElements
     * @param ancestor WebElement that must be parent of elements from first parameter.
     */
    private static void filterOutElementsWithoutCommonAncestor(List<WebElement> elements, WebElement ancestor) {
        for (Iterator<WebElement> iter = elements.iterator(); iter.hasNext(); ) {
            WebElement elem = iter.next();

            // iterate over ancestors
            while (!elem.equals(ancestor) && !elem.getTagName().equals("html")) {
                elem = elem.findElement(By.xpath("./.."));
            }

            if (!elem.equals(ancestor)) {
                iter.remove();
            }
        }
    }

    /**
     * Implements findElements method to allow find elements by jQuery selectors
     *
     * @param context A context to use to find the element.
     * @return list of found WebElements on the page
     */
    @Override
    public List<WebElement> findElements(SearchContext context) {
        JavascriptExecutor js = getJavascriptExecutorFromSearchContext(context);

        // call the JS, inspect and validate response
        Object response = js.executeScript(script);
        List<WebElement> elements = getElementListFromJsResponse(response);

        // filter out the elements that aren't descendants of the context node
        if (context instanceof WebElement) {
            filterOutElementsWithoutCommonAncestor(elements, (WebElement) context);
        }

        return elements;
    }

    /**
     * Implements toString method for string presentation of javascript for using in logger, etc.
     *
     * @return string with query
     */
    @Override
    public String toString() {
        return "By.javaScript: \"" + script + "\"";
    }
}
