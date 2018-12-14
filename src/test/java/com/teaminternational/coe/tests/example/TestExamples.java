package com.teaminternational.coe.tests.example;

import com.codeborne.selenide.*;
import com.codeborne.selenide.testng.annotations.Report;
import com.teaminternational.coe.pages.AutocompletePage;
import com.teaminternational.coe.pages.examples.GooglePage;
import com.teaminternational.coe.pages.SearchResultsPage;
import com.teaminternational.coe.tests.BaseCustomTest;
import com.teaminternational.coe.utils.PropertiesContext;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeNotEqual;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.id;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.name;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.textCaseSensitive;
import static com.codeborne.selenide.Condition.type;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selectors.byPartialLinkText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byTitle;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selectors.withText;


import static com.codeborne.selenide.Selenide.*;
import static org.testng.Assert.assertEquals;
import com.teaminternational.coe.utils.webdriver.ByJquery;
import com.teaminternational.coe.utils.webdriver.ByJavaScript;

/**
 * Class with Example of tests.
 * Cover page with testing most frequently used elements and examples of all selenide methods:
 * search result pages, checkboxes, radioboxes, selects, text inputs, visibility tests,
 * tests of attributes and text content, upload/download files, javascript working and javascript errors,
 * drag and drop, autocomplete elements, etc.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
@Report
public class TestExamples extends BaseCustomTest {

    /**
     * Base URL for test examples
     */
    protected final String frameworktest_baseurl = PropertiesContext.getInstance().getProperty("frameworktest_baseurl");

    /**
     * Example of test method.
     * Demonstrates usage of GooglePage PageObject and SearchResults Page Object and his methods
     */
    @Test
    public void userCanSearch() {
        open(envurl);
        System.out.println("Open URL:"+envurl);
        new GooglePage().searchFor("selenide");
        SearchResultsPage results = new SearchResultsPage();
        results.getResult(0).shouldHave(text("azaza"));
        results.getResult(0).shouldHave(text("bebebe"));
        results.getResults().shouldHave(sizeGreaterThan(1));
        results.getResult(0).shouldHave(text("Selenide: concise UI tests in Java"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of GooglePage PageObject and SearchResults Page Object and his methods
     */
    @Test
    public void userCanSearch2() {
        open(envurl);
        new GooglePage().searchFor("selenide");

        SearchResultsPage results = new SearchResultsPage();
        results.checkResultsSizeIsAtLeast(1);
        results.checkResultHasTest(0, "Selenide: concise UI tests in Java");
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide method for working with checkboxes
     */
    @Test
    public void checkboxTest() {
        //open("https://the-internet.herokuapp.com/checkboxes");
        open(frameworktest_baseurl);

        //only standard HTML checkboxes
        $("input[type=checkbox]").click();
        $("input[type=checkbox]").shouldBe(checked);

        //or
        $("input[type=checkbox]").setSelected(true);
        $("input[type=checkbox]").shouldBe(checked);


        $("input[type=checkbox]", 1).setSelected(false);
        $("input[type=checkbox]", 1).shouldNotBe(checked);

    }


    /**
     * Example of test method.
     * Demonstrates usage of selenide method for working with radio boxes
     */
    @Test
    public void radiobox() {
        //open("http://www.ebay.com/sch/ebayadvsearch");
        open(frameworktest_baseurl);

        //only standard HTML radioboxes
        $("[name=_fsradio2]").selectRadio("&LH_LocatedIn=1"); //value of radiobox item

        // we should definitely add element.getSelectedRadio one day :)
        getSelectedRadio(byName("_fsradio2")).shouldHave(value("LocatedIn"));

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide method for working with dropdown boxes (select)
     */
    @Test
    public void dropdown() {
        //open("https://the-internet.herokuapp.com/dropdown");
        open(frameworktest_baseurl);

        //only standard HTML selects
        $("#dropdown").getSelectedOption().shouldBe(disabled);

        $("#dropdown").selectOption("Option 2"); //exact text match
        $("#dropdown").selectOptionContainingText("ption 1"); //partial match
        $("#dropdown").selectOption(2); //starting from 0, third option
        $("#dropdown").selectOptionByValue("1"); //value

        $("#dropdown").getSelectedOption().shouldHave(text("Option 1"), value("1"));

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide method for working with input text
     */
    @Test
    public void input() {
        //open("https://the-internet.herokuapp.com/login");
        open(frameworktest_baseurl+"/#!/signin");
        //fill in with JavaScript
        Configuration.fastSetValue = true;
        $("#email").setValue("abc");
        $("#email").shouldHave(value("abc"));

        //default. Traditional sendKeys of Selenium
        Configuration.fastSetValue = false;
        $("#email").shouldHave(value("abc"));
        $("#email").setValue("abc");


    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods Should , ShouldBe , ShouldHave, ShouldNot, ShouldNotHave
     *
     * @see com.codeborne.selenide.commands.Should
     * @see com.codeborne.selenide.commands.ShouldBe
     * @see com.codeborne.selenide.commands.ShouldHave
     * @see com.codeborne.selenide.commands.ShouldNot
     * @see com.codeborne.selenide.commands.ShouldNotHave
     */
    @Test
    public void simpleShoulds() {
        open(frameworktest_baseurl);

        //every should* is an assert which
        // 1) waits its condition for at most Configuration.timeout ms
        // 2) if condition is not met after <timeout.ms>
        //    a) takes screenshot
        //    b) saves sourceCode
        //    c) outputs debug information about element and meaningful error message


        // all these should-s are the same, variants are for language semantics
        $("h1").shouldBe(visible);
        $("h1").shouldHave(text("Test"));
        $("h1").should(appear);


        // negative variants of should-s
        $("h8").shouldBe(hidden);
        $("h1").shouldNotHave(text("Automation"));
        $("h8").shouldNot(exist);


    }


    /**
     * Example of test method.
     * Demonstrates usage of selenide methods Should , ShouldBe , ShouldHave, ShouldNot, ShouldNotHave
     *
     * @see com.codeborne.selenide.commands.Should
     * @see com.codeborne.selenide.commands.ShouldBe
     * @see com.codeborne.selenide.commands.ShouldHave
     * @see com.codeborne.selenide.commands.ShouldNot
     * @see com.codeborne.selenide.commands.ShouldNotHave
     */
    @Test
    public void moreShoulds() {
        open(frameworktest_baseurl);

        //chainable
        $("h1").shouldBe(visible)
                .shouldHave(text("Test"))
                .should(appear);

        // >1 conditions
        $("h1").shouldHave(text("Test"), cssClass("docs-logotype"));

        // is, be, not combined
        $("h1").should(be(visible), have(text("Test")), not(be(hidden)));

        // or, and (why do you need them in tests?)
        $("h1").shouldBe(or("to be or not to be visible", visible, hidden));
        $("h1").shouldBe(and("fish & chops", visible, not(disabled)));
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods should related to element visibility
     */
    @Test
    public void visibilityConditions() {
        open(frameworktest_baseurl);
        //Conditions.*

        // the most popular!
        $("h1").shouldBe(visible); //and shouldNotBe(visible) too
        $("h1").should(appear); //it is the same

        // exists in DOM could be visible or hidden
        // why do you need it in UI tests?
        $("h1").should(exist);

        // hidden or does not exist!!
        $("h8").shouldBe(hidden);
        $("h8").should(disappear); //the same
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide assertion methods with different text conditions
     */
    @Test
    public void textConditions() {
        open(frameworktest_baseurl);

        $("h1").shouldHave(text("TES")); //case insensitive, partial match
        $("h1").shouldHave(textCaseSensitive("Tes")); //case sensitive, partial match
        $("h1").shouldHave(exactText("coe TEST Automation")); //case insensitive, exact match
        $("h1").shouldHave(exactTextCaseSensitive("COE Test Automatoin")); //case sensitive, exact match
        $("h1").should(matchText("[A..Z]est")); //regexp
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide assertion methods with different conditions
     */
    @Test
    public void otherConditions() {
        open(frameworktest_baseurl);

        //popular
        $("body").shouldHave(attribute("layout", "row")); //with value
        $("h1").shouldNotHave(attribute("href")); //any value
        $("h1").shouldHave(cssClass("docs-logotype"));

        // for inputs
        $("input[ng-model='user.company']").shouldBe(disabled);
        $("input[ng-model='user.firstName']").shouldBe(enabled);
        $("input[ng-model='user.firstName']").shouldBe(empty);

        $("input[ng-model='user.company']").shouldHave(value("Goo")); //partial match
        $("input[ng-model='user.company']").shouldHave(exactValue("Google"));

        //others
        $("[type=button]").shouldHave(type("button"));
        $("[type=button]").shouldNotHave(id("123"));
        $("[name=documentation-version]").shouldHave(name("documentation-version"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods with basic selectors
     */
    @Test
    public void basicSelectors() {
        open(frameworktest_baseurl);

        //Collections are Iterable SelenideElements
        ElementsCollection collection = $$("div");

        $$("abc"); // doesn't search anything!

        // use the same Selectors as in $
        $$(byText("abc"));

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods for working with collections
     */
    @Test
    public void operationsOnCollections() {
        open(frameworktest_baseurl);
        //filtering
        $$("div").filterBy(text("123")); //only with text 123
        $$("div").excludeWith(text("123")); // all except with text 123
        //choosing an element
        $$("div").first();
        $$("div").last();
        $$("div").get(2); // get third element, index from 0

        // find first match (works faster now)
        $$("div").findBy(text("123")); // finds the first element

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide returning elements for variables of  ElementsCollection and SelenideElement classes
     * @see ElementsCollection
     * @see SelenideElement
     */
    @Test
    public void operationsReturns() {
        open(frameworktest_baseurl);
        ElementsCollection collection = $$("div").filterBy(text("123")); // even when filters to a single element!
        ElementsCollection collection2 = $$("div").filterBy(text("xyz")); // empty collection!
        SelenideElement element = $$("div").get(2);
        SelenideElement element2 = $$("div").find(text("xyz")); // null?
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide assertion methods of different types
     */
    @Test
    public void assertions() {
        open(frameworktest_baseurl);

        //Size assertion
        $$("div").shouldBe(CollectionCondition.empty);
        $$("div").shouldHave(size(5));
        $$("div").shouldHaveSize(5); //the same

        $$("div").shouldHave(sizeGreaterThan(1));
        $$("div").shouldHave(sizeGreaterThanOrEqual(1));
        $$("div").shouldHave(sizeLessThan(100));
        $$("div").shouldHave(sizeLessThanOrEqual(100));
        $$("div").shouldHave(sizeNotEqual(1984));


        // text assertions are case-sensitive
        $$("div").shouldHave(texts("", "")); //partial matches of every single text
        $$("div").shouldHave(exactTexts("", ""));

        // as of Selenide 4.3
        // no methods for asserting collection has some of elements
        // or for asserting ignoring the order of elements
        // Alternative solution: iterating over collection
        // (it is on the to be done features list)

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide method download file
     *
     * @throws FileNotFoundException in case of error
     */
    @Test
    public void downloadFile() throws FileNotFoundException {
        open(frameworktest_baseurl);

        //open("http://the-internet.herokuapp.com/download");

        //FileNotFoundException is thrown if server returns 40x on download
        // works for remote WebDriver as well!
        File file = $(Selectors.byText("some-file.txt")).download();

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods for uploading file
     */
    @Test
    public void uploadFile() {
        open(frameworktest_baseurl);
        //open("http://the-internet.herokuapp.com/upload");

        // works for remote driver as well!
        File file = new File("src/test/resources/readme.txt");
        $("#file-upload").uploadFile(file);

        // classpath - typically resources folder in maven/gradle source structure
        $("#file-upload").uploadFromClasspath("readme.txt");

        // don't forget to submit ;-)
        $("#file-submit").click();

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods for working with javascript
     */
    @Test
    public void javascriptUsage() {

        open(frameworktest_baseurl);
        //open("https://the-internet.herokuapp.com/javascript_error");
        //Selenide.*

        // without arguments
        executeJavaScript("alert('selenide')");
        Selenide.confirm(); //close alert dialog
        //with arguments
        executeJavaScript("alert(arguments[0]+arguments[1])", "abc", 12);
        Selenide.confirm();
        //with return value
        long fortytwo = executeJavaScript("return arguments[0]*arguments[1];", 6, 7);
        assertEquals(42, fortytwo);

    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods for working with javascript errors
     */
    @Test//(expected = JavaScriptErrorsFound.class)
    public void javascriptErrors() {
        open(frameworktest_baseurl);
        //open("https://the-internet.herokuapp.com/javascript_error");
        List<String> jsErrors = getJavascriptErrors();
        System.out.println(jsErrors);

        // Asserts, no JS Errors
        // Exception, if there are errors
        assertNoJavascriptErrors();
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide basic action methods with elements on the page
     */
    @Test
    public void basicActions() {
        open(frameworktest_baseurl);
        $("h1").click();
        $("h1").contextClick(); //right click
        $("h1").doubleClick();
        $("h1").hover(); //mouse over
    }

    /**
     * Example of test method.
     * Demonstrates usage of selenide methods for drag and drop functionality
     */
    @Test
    public void dragAndDrop() {
        open(frameworktest_baseurl);
        // Doesn't work with HTML5 draggable at this time (Selenium Bug)
        $("#column-a").dragAndDropTo($("#column-b"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of some more selenide action methods: scrollTo, moveByOffset, clickAndHold, release, perform, keyDown, keyUp, sendKeys
     */
    @Test
    public void otherActions() {
        open(frameworktest_baseurl);

        $("h1").scrollTo(); //scrolling to element

        //Selenide.actions() -> Selenium Actions
        actions().moveByOffset(10, 500)
                .clickAndHold()
                .moveByOffset(-10, -500)
                .release()
                .perform();

        //keyboard actions are also there
        actions().keyDown(Keys.ALT)
                .sendKeys("P")
                .keyUp(Keys.ALT);

    }

    /**
     * Example of test method.
     * Demonstrates usage of different selenide selector types
     */
    @Test
    public void seleniumSelectors() {
        open(frameworktest_baseurl);

        //Elements are SelenideElement and WebElement (Selenium class)
        SelenideElement element = $("div");

        $("xyz");  // no error it doesn't search for anything!

        //Selectors.by**

        // Simple Selectors (from Selenium)
        $(".md-content .md-ink-ripple").click(); //=byCss
        $(byXpath("//button")).shouldBe(visible); //-By Xpath
        $x("//button").shouldBe(visible);// the same as previous line
        $$x("//button").get(0).shouldBe(visible);;//the same as previous line
        $(byId("abc"));
        $(byLinkText("Button"));
        $(byPartialLinkText("utton"));
        $(byName("name"));
        $(byClassName("md-ink-ripple"));
        //$(byTagName() -> is not available, use $("tagname");

    }

    /**
     * Example of test method.
     * Demonstrates usage of different selenide selector types
     */
    @Test
    public void selenideSelectors() {
        open(frameworktest_baseurl);

        //Added Selectors from Selenide
        $(byText("Basic Usage")).click();  // EXACT Texts
        $(withText("asic Usage")).click(); //finds partial occurance
        $(byAttribute("md-svg-src", "img/icons/android.svg")).click();
        $(by("md-svg-src", "img/icons/android.svg")).click(); //the same as above
        $("div", 2); // index starts with 0, the third <div> will be selected
        $(byTitle("my_title"));
        $(byValue("my_value"));
    }


    /**
     * Example of test method.
     * Demonstrates examples of redundant selenide selectors
     */
    @Test
    public void redundantSelectors() {
        open(frameworktest_baseurl);

        // This selectors can all be replaced with $("")
        // no errors, $ doesn't search for anything
        $(byName("abc"));
        $("[name=abc]"); // is the same
        $(byTitle("cde"));
        $("[title=cde]"); // is the same
        $(byValue("fgh"));
        $("[value=fgh]"); // is the same
        $(byId("ijk"));
        $("#ijk"); // is the same
        $(byClassName("xyz"));
        $(".xyz"); // is the same
    }


    /**
     * Example of test method.
     * Demonstrates examples of chained selenide selectors
     */
    @Test
    public void chainedSelectors() {
        open(frameworktest_baseurl);

        // Selectors can be naturally chained
        $("div.container").$("button.buy"); // still doesn't search!
        // find is synonym for $
        $(".container").find(".subarea").find(byText("abc")); //and it still doesn't search, despite of its name.

        // parent and closest(tag) helps navigation in DOM
        $(byText("Bottom Sheet")).parent().closest("ul").shouldBe(visible);

    }


    /**
     * Example of test method.
     * Demonstrates examples of selenide methods for waiting something on the page
     */
    @Test
    public void waiting() {
        open(frameworktest_baseurl);
        // if you want wait, but really wait
        $(".waiting").waitUntil(visible, 30000); //waits 30 seconds
        $(".waiting").waitUntil(text("Ready"), 15000);

        // wait while condition is met
        $(".waiting").waitWhile(hidden, 15000);

    }

    /**
     * Example of test method.
     * Demonstrates how to wait 10 seconds and assert fail
     */
    @Test//(expected = AssertionError.class)
    public void timeout() {
        open(frameworktest_baseurl);
        // if fails then
        // the same in should (Screenshots, Readable Message, SourceCode)
        $(".abc").waitUntil(visible, 10000); //waits 10 seconds, then AssertionError
    }


    /**
     * Example of test method.
     * Demonstrates usage of Autocomplete PageObject and his methods
     */
    @Test
    public void autocompleteShowsUsStates() {
        open(frameworktest_baseurl);
        AutocompletePage autocompletePage = new AutocompletePage();
        autocompletePage.fill("V");
        autocompletePage.autocompleteBox.shouldHave(text("Virginia"))
                .shouldHave(text("Vermont"))
                .shouldNotHave(text("Alaska"));
        autocompletePage.selectFromAutocompleteBox("Virginia");
        autocompletePage.stateInput.shouldHave(value("Virginia"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of Autocomplete PageObject and his methods
     */
    @Test
    public void autocompleteCalifornia() {
        open(frameworktest_baseurl);
        AutocompletePage autocompletePage = new AutocompletePage();
        autocompletePage.fill("Calif");
        autocompletePage.selectFromAutocompleteBox("California");
        autocompletePage.stateInput.shouldHave(value("California"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of out ByJquery implementation. Successful test.
     */
    @Test
    public void useByJQuerySuccess() {
        open(frameworktest_baseurl);
        $(new ByJquery(".navbar-brand.v-link-active")).shouldHave(text("Sample App")).shouldHave(text("Test Automatoin"));
    }
    /**
     * Example of test method.
     * Demonstrates usage of out ByJquery implementation. Failed test.
     */
    @Test
    public void useByJQueryFail() {
        open(frameworktest_baseurl);
        $(new ByJquery(".navbar-brand.v-link-not-exists")).shouldHave(text("Sample App"));
        $(new ByJquery(".navbar-brand.v-link-active")).shouldHave(text("azazazazaz"));
    }
    /**
     * Example of test method.
     * Demonstrates usage of out ByJquery implementation. Successful test.
     */
    @Test
    public void useByJavaScriptSuccess() {
        open(frameworktest_baseurl);
        $(new ByJavaScript("return document.getElementById(\"brand-title\")")).shouldHave(text("Sample App"));
    }
    /**
     * Example of test method.
     * Demonstrates usage of out ByJquery implementation. Failed test.
     */
    @Test
    public void useByJavaScriptFail() {
        open(frameworktest_baseurl);
        $(new ByJavaScript("return document.getElementById(\"brand-not-exists\")")).shouldHave(text("Sample App"));
        $(new ByJavaScript("return document.getElementById(\"brand-title\")")).shouldHave(text("azazazaza"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of confirm()
     */
    @Test
    public void confirmConfirm() {
        open(frameworktest_baseurl+"/#/products");
        $("tr:nth-child(1) td:nth-child(4) button:nth-child(1)").click();
        confirm();
        $("tr:nth-child(1) td:nth-child(1)").shouldNotHave(text("Table"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of dismiss()
     */
    @Test
    public void dismissConfirm() {
        open(frameworktest_baseurl+"/#/products");
        $("tr:nth-child(1) td:nth-child(4) button:nth-child(1)").click();
        dismiss();
        $("tr:nth-child(1) td:nth-child(1)").shouldHave(text("Table"));
    }
    /**
     * Example of test method.
     * Demonstrates usage of dismiss()
     */
    @Test
    public void propmptPrompt() {
        open(frameworktest_baseurl);
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("document.getElementById('ijk').value = prompt(\"Your birthday year?\", 2000);");
        prompt();
        $(byId("ijk")).shouldHave(value("2000"));
    }
    /**
     * Example of test method.
     * Demonstrates usage of dismiss()
     * Also demonstrates usage of ZAPI for connecting test with JIRA Test.
     * TmsLink must be ID of test in JIRA. Should look like "10000" or "10001", etc. Can be found in
     *
     * @see TmsLink
     */
    @Test
    @DisplayName("Check dismiss() of Selenide is working")
    @TmsLink("10002")
    public void dismissPromptFail() {
        open(frameworktest_baseurl);
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("document.getElementById('ijk').value = prompt(\"Your birthday year?\", 2000);");
        dismiss();
        $(byId("ijk")).shouldHave(value("2000"));
    }

    /**
     * Example of test method.
     * Demonstrates usage of back(), forward() and getFocusedElement()
     */
    @Test
    public void someOtherSelenideFucntions() {
        open(frameworktest_baseurl);
        $(byId("sign-in")).click();
        back();
        $(byId("sign-in")).shouldHave(text("Sign In"));
        forward();
        $("h1.text-center").shouldHave(text("Sign In"));
        $(byId("email")).click();
        $(getFocusedElement()).shouldHave(cssClass("form-control"));
    }

}
