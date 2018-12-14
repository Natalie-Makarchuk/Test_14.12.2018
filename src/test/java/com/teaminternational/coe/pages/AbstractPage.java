package com.teaminternational.coe.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.teaminternational.coe.utils.CommonHelper;
import com.teaminternational.coe.utils.PropertiesContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Abstract class for implementing PageObject template.
 * Contain basic methods which can be helpful in any subclasses.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 */
public abstract class AbstractPage {

    /**
     * Constant Property context field woth project parameters.
     */
    protected static final PropertiesContext context = PropertiesContext.getInstance();
    /**
     * Selector field for select web element.
     */
    public String selector;
    /**
     * Field for common helper singleton.
     */
    public CommonHelper commonHelper;

    /**
     * Field for currently running WebDriver.
     */
    public WebDriver driver;

    /**
     * Field for WebDriverWait object with timeout defined for 3 seconds.
     */
    public WebDriverWait wait_3;

    /**
     * Field for WebDriverWait object with timeout defined for 10 seconds.
     */
    public WebDriverWait wait_10;

    /**
     * Field for WebDriverWait object with timeout defined for 60 seconds.
     */
    public WebDriverWait wait_60;
    /**
     * Constructor setup commonhelper, driver, wait_3, wait_10 and wait_60 fields
     */
    public AbstractPage() {
        this.commonHelper = CommonHelper.getInstance();
        this.driver = WebDriverRunner.getWebDriver();
        this.wait_3 = new WebDriverWait(driver, 3);
        this.wait_10 = new WebDriverWait(driver, 10);
        this.wait_60 = new WebDriverWait(driver, 60);
    }

    /**
     * Calls selenide refresh page method and wait 3 seconds
     */
    public void refreshPage() {
        Selenide.refresh();
        Selenide.executeJavaScript("window.exposeRequestCount = true;");
        CommonHelper.suspend(3000);
    }

    /**
     * Get selector field
     *
     * @return Selector
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Get selector field
     *
     * @return Selector
     */
    public String getAbsoluteSelector() {
        return selector;
    }

    /**
     * Get properties context singleton
     *
     * @return Properties Context singleton
     */
    public PropertiesContext getContext() {
        return context;
    }
}
