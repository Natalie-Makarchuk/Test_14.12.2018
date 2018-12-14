package com.teaminternational.coe.utils;


import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static com.codeborne.selenide.Selenide.$;


/**
 * Class implements many useful functions for framework.
 * Get ipsum string, get random id, get random integer id, get random long, get random string and random string with spaces, upload file into input of file type on the page.
 * All methods are useful for writing tests.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
public class CommonHelper {

    /**
     * Static variable for singleton instance of CommonHelper class
     */
    private static volatile CommonHelper instance;
    /**
     * Constant for XPath selector that should find file input field
     */
    private static final String FILE_INPUT_XPATH = "//input[@type='file']";
    /**
     * Varable to keep logger object
     */
    private Logger logger;

    /**
     * Initialize logger field of CommonHelper class
     *
     * @see Logger
     */
    private CommonHelper() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Create a new CommonHelper instance if it's not created before and store in private static variable
     *
     * @return create instance of CommonHelper singleton
     */
    public static synchronized CommonHelper getInstance() {
        if (instance != null)
            synchronized (CommonHelper.class) {
                if (instance == null)
                    instance = new CommonHelper();
            }
        return instance;
    }


    /**
     * Get Random 8 alpha numeric symbols and lorem ipsim string after
     * good for long text fields
     *
     * @param length Required length of returned string
     * @return "Lorem ipsum ..." text with random 8 random symbols at the beginning
     */
    public static String getIpsumString(int length) {
        String ipsum = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,";
        return (getRandomID(8) + " " + ipsum).substring(0, length);
    }

    /**
     * Return alphanumeric id with length
     *
     * @param length Required length of returned string
     * @return string with specified length and random set of symbols
     */
    public static String getRandomID(int length) {
        return new BigInteger(130, new SecureRandom()).toString(32).substring(0, length);
    }

    /**
     * Return sequence of integers with length
     *
     * @param length Required length of returned int
     * @return random integer with defined length
     */
    public static Integer getRandomIntID(int length) {
        char[] CHARSET_AZ_09 = "1234567890".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }
        return Integer.parseInt(new String(result));
    }

    /**
     * Generate random integer between defined minimum and maximum
     *
     * @param low Minimal value of returned int
     * @param high Maximal value of returned int
     * @return random integer
     */
    public static Integer getRandomIntBetween(int low, int high) {
        return new Random().nextInt(high - low) + low;
    }

    /**
     * Generate random long between defined minimum and maximum
     *
     * @param low Minimal value of returned Long
     * @param high Maximal value of returned Long
     * @return random long
     */
    public static Long getRandomLongBetween(Long low, Long high) {
        return ThreadLocalRandom.current().nextLong(low, high);
    }

    /**
     * Generate string of length with first char in uppercase
     *
     * @param length Required length of returned string
     * @return random string with defined length and first uppercase character
     */
    public static String randomString(int length) {
        char[] CHARSET_AZ_09 = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }
        result[0] = Character.toUpperCase(result[0]);
        return new String(result);
    }

    /**
     * Generate string of length with spaces and first char in uppercase
     *
     * @param length Required length of returned string
     * @return random string with spaces with defined length and first uppercase character
     */
    public static String randomStringWithSpaces(int length) {
        char[] CHARSET_AZ_09 = "abcdefghijklmnopqrstuvwxyz ".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }
        result[0] = Character.toUpperCase(result[0]);
        return new String(result);
    }

    /**
     * Delay thread execution for defined amount of milliseconds
     * In general use of this is not welcomed, use mustWaitForLoadToComplete
     *
     * @param millis How mani milliseconds need to sleep
     */
    public static void suspend(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute Selenide command to upload file into fileinput on the page
     *
     * @param filePath Path to file that will be uploaded
     * @param uploadFromClassPath Specify that should be used uploadFromClasspath method or uploadFile
     */
    public void uploadFile(String filePath, boolean uploadFromClassPath) {
        File file = new File(filePath);
        if (uploadFromClassPath) {
            $(By.xpath(FILE_INPUT_XPATH)).uploadFromClasspath(filePath);
        } else {
            $(By.xpath(FILE_INPUT_XPATH)).uploadFile(file);
        }

    }

    /**
     * Get Image Screenshot as sequence of bytes on underlying instance of Selenium WebDriver
     *
     * @return Object in which is stored information about the screenshot
     */
    public static byte[] getScreenshotBytes() {
        return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Get the source of the last loaded page as sequence of bytes on underlying instance of Selenium WebDriver
     *
     * @return Byte's array in which is stored information about the last loaded page
     */
    public static byte[] getPageSourceBytes() {
        return WebDriverRunner.getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }


    /**
     * Writes custom additional step to the Allure report of current testcase.
     * Useful for debugging or when you just want to log into report some simple step in testcase that doesn't call any PageObject methods.
     *
     * @param name Name of step.
     */
    public static void addAllureStep(String name) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        final String uuid = UUID.randomUUID().toString();
        final StepResult result = new StepResult().withName(name);
        lifecycle.startStep(uuid, result);
        lifecycle.stopStep(uuid);
    }
}

