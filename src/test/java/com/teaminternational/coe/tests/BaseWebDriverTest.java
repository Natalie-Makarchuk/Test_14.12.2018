package com.teaminternational.coe.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.testng.SoftAsserts;
import com.codeborne.selenide.testng.TextReport;
import com.teaminternational.coe.entities.users.UsersPool;
import com.teaminternational.coe.pages.HomePage;
import com.teaminternational.coe.utils.CommonHelper;
import com.teaminternational.coe.utils.PropertiesContext;
import com.teaminternational.coe.utils.report.zapi.ZAPIManager;
import com.teaminternational.coe.utils.testng.TestListener;
import com.teaminternational.coe.utils.testng.TestMethodListener;
import com.teaminternational.coe.utils.testng.TimeoutTransformer;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.asserts.Assertion;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.Exception;

import static com.codeborne.selenide.Selenide.close;


/**
 * Basic test class.
 * Use it as parent for all your testing classes.
 * Contains setup and tear down methods for test, initiation, loading of basic configuration.
 * Define basic Allure reporting before and after each method execution.
 * Enable integration with Zephyr for JIRA Cloud API
 * Add 2 TestNG based listeners and 2 Selenide listeners to the basic test class
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see TestMethodListener
 * @see TestListener
 * @see SoftAsserts
 * @see TextReport
 */
@Listeners({TestMethodListener.class, TestListener.class, TimeoutTransformer.class, SoftAsserts.class})
public class BaseWebDriverTest {

    /**
     * URL of tested application
     */
    protected final String envurl = PropertiesContext.getInstance().getProperty("envurl");

    /**
     * Field for loading managing project parameters
     */
    public PropertiesContext context;
    /**
     * Field for CommonHelper instance
     */
    public CommonHelper common;
    /**
     * Field for HomePage object
     */
    public HomePage homePage;
    /**
     * For User's pool
     */
    protected UsersPool pool = UsersPool.getInstance();


    /**
     * Method executed before test run.
     * Load base configuration and save into PropertiesContext singleton.
     * Setup Selenide Configuration.
     * Write log about starting test run.
     *
     * @param ctx test context with information about test run
     *
     */
    @BeforeSuite
    public void beforeSuite(final ITestContext ctx) {
        if (PropertiesContext.getInstance().getProperty("enableZapi").equalsIgnoreCase("true")) {

            try {
                if (PropertiesContext.getInstance().getProperty("cycleId").equals("-1")) {
                    PropertiesContext.getInstance().setProperty("cycleId", new ZAPIManager().createCycle(ZAPIManager.prepareJsonForCycle("Smoke Cycle " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")))));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //TODO: Check why suite executed with verbose="0" and nothing stored by Reporter.log in Reporter output
        try {
            Reporter.log("Running on: " + java.net.InetAddress.getLocalHost().getHostName(), 1, true);
        } catch (UnknownHostException e) {
            Reporter.log("Failed to get hostname", 1);
        }

        Configuration.timeout = Integer.parseInt(PropertiesContext.getInstance().getProperty("selenide.timeout"));
        Configuration.browser = PropertiesContext.getInstance().getProperty("browser");
        Configuration.reportsFolder = PropertiesContext.getInstance().getProperty("reportlocation");
        Configuration.assertionMode = Configuration.AssertionMode.SOFT;
        Configuration.baseUrl = PropertiesContext.getInstance().getProperty("envurl");
        Configuration.savePageSource = false;

        if (PropertiesContext.getInstance().getProperty("browser").contains("grid")) {
            Configuration.remote = PropertiesContext.getInstance().getProperty("gridHubUrl");
        }
    }

    /**
     * Method executed before test method execution.
     * initialize logger, properties context, common helper and homepage. Write log in case of error of initialization.
     *
     * @param testContext test context with information about test run
     *
     */
    @BeforeMethod()
    public void beforeMethod(final ITestContext testContext) {
        context = PropertiesContext.getInstance();
        try {
            common = CommonHelper.getInstance(); //Init commonHelper
            homePage = new HomePage();
        } catch (WebDriverException e) {
            e.printStackTrace();
            Reporter.log("Failed to initialize test", 1, true);
            Reporter.log(e.getMessage(), 1, true);
        }
    }

    /**
     * Method executed after test method execution.
     * Assert test method execution.
     * Verify on Javascript errors in webdriver logs, in closing webdriver and in saving report logs.
     *
     * @param testContext test context with information about test run
     * @param result Test method result
     */
    @AfterMethod()
    public void afterMethod(final ITestContext testContext, ITestResult result) {
        //pool.releaseUser(currentlyLoggedInUser);
        boolean isServerError = false;
        try {
            for (String entry : Selenide.getWebDriverLogs(LogType.BROWSER, Level.SEVERE)) {
                Reporter.log("***** Severe JS error " + entry + " *****", 1, true);
                isServerError = isServerError | entry.contains("Internal Server Error");
            }
            for (String entry : Selenide.getWebDriverLogs(LogType.BROWSER, Level.WARNING)) {
                Reporter.log("***** Warning JS error " + entry + " *****", 1, true);
                isServerError = isServerError | entry.contains("Internal Server Error");
            }

        } catch (NullPointerException ignore) {
            Reporter.log("Failed to get browser logs", 1, true);
        }

        try {
            close();
        } catch (Exception ignored) {
            Reporter.log("Failed to quit driver", 1, true);
            Reporter.log(ignored.getMessage(), 1, true);
        }
        try {

            Reporter.log("****************************************************", 1, true);
            Reporter.log("Completed test method " + result.getName() + " at " + LocalDateTime.now(), 1, true);
            Reporter.log("****************************************************", 1, true);
            Reporter.log("Time taken by method " + result.getName() + ": " + (result.getEndMillis() - result.getStartMillis()) / 1000 + "sec", 1, true);
            Reporter.log("****************************************************", 1, true);
        } catch (Exception e) {
            System.out.println("Failed to get info from results");
        }
        new Assertion().assertFalse(isServerError, "500 Error detected during test execution");

    }

    /**
     * Method executed after test run execution.
     * Tries to close webdriver and write report log in case of error.
     *
     * @param ctx test context with information about test run
     */
    @AfterSuite
    public void afterSuite(final ITestContext ctx) {
        try {
            close();
        } catch (Exception e) {
            Reporter.log("Failed to quit webdriver", 1, true);
        }
    }
}
