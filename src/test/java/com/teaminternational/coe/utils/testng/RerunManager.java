package com.teaminternational.coe.utils.testng;

import com.teaminternational.coe.utils.PropertiesContext;
import org.openqa.selenium.WebDriverException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Class implements Retry Analyzer method for methods with test annotation.
 * Write information about timeout end of test and number of retries, and stop execution of test in case of max retry count reached.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see IRetryAnalyzer
 */
public class RerunManager implements IRetryAnalyzer {

    /**
     * Current retry number
     */
    private int retryCount = 0;
    /**
     * Maximum allowed retries
     */
    private int maxRetryCount = Integer.parseInt(PropertiesContext.getInstance().getProperty("maxRetryCount"));

    /**
     * Implements ability to retry failed tests certain amount of time defined in config files
     *
     * @param result The result of the test method that just ran.
     * @return Returns true if the test method has to be retried, false otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {
        if (maxRetryCount != 0) {
            if (!result.isSuccess() && result.getThrowable() instanceof WebDriverException | result.getThrowable() instanceof AssertionError) {
                if (retryCount < maxRetryCount) {
					//TODO: check why only Report.log() call visible in Report output of Testng Report
                    Reporter.log("Retrying test " + result.getTestName(), 1, true);
                    retryCount++;
                    result.setStatus(ITestResult.SUCCESS);
                    String message = Thread.currentThread().getName() + ": Error in " + result.getName() + " Retrying "
                            + (maxRetryCount + 1 - retryCount) + " more times";
                    Reporter.log("" + message, 1, true);
                    return true;
                } else {
                    result.setStatus(ITestResult.FAILURE);
                }
            }
        }
        return false;
    }
}
