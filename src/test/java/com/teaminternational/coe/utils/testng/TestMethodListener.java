package com.teaminternational.coe.utils.testng;

import io.qameta.allure.TmsLink;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Class implements methods beforeInvocation and afterInvocation of tests listener for testng.
 * In those implementations link to test added into results of test and this used in other listener TestListener to write this link to JIRA
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see IInvokedMethodListener
 * @see TestListener
 */
public class TestMethodListener implements IInvokedMethodListener {

    /**
     * Override beforeInvocation listener. No any implementation yet.
     *
     * @param method  method that has been invoked by TestNG
     * @param testResult  the result of a test.
     */
    @Override
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
    }

    /**
     * Override afterInvocation listener. Add Link to test into testResult field for using in other classes
     * Method that will be posted to JIRA should have annotation TmsLink and Long value in it's body
     *
     * @param method  method that has been invoked by TestNG
     * @param testResult  the result of a test.
     * @see TmsLink
     */
    @Override
    public void afterInvocation(final IInvokedMethod method, final ITestResult testResult) {
        TmsLink testId = method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(TmsLink.class);
        if (testId != null ) {
            Object val = testId.value();
            try {
                if (String.valueOf(val).equals(String.valueOf(Long.parseLong(String.valueOf(val))))) {
                    testResult.setAttribute("TmsLink", val);
                }
            } catch (Exception e) {
                Reporter.log("Specified value \""+val+"\" is not Long value in TmsLink annotation for method \"" + method.getTestMethod().getMethodName()+"\"", 1, true);
            }
        }
    }
}
