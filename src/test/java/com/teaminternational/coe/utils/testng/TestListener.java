package com.teaminternational.coe.utils.testng;

import com.teaminternational.coe.utils.PropertiesContext;
import com.teaminternational.coe.utils.report.zapi.ZAPIManager;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class implements method onTestSuccess and onTestFailure of TestListenerAdapter class.
 * In those implementations added creating and updating of executions in JIRA through Zephyr API, if it's enabled
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see TestListenerAdapter
 */
public class TestListener extends TestListenerAdapter {
    /**
     * Results Map. Used in methods for merging with
     */
    private static Map<String, Boolean> results = new ConcurrentHashMap<>();


    /**
     * Implements onTestSuccess listener . Creates successful execution at JIRA and saves link to report there
     *
     * @param tr ITestResult containing information about the run test
     */
    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        if (PropertiesContext.getInstance().getProperty("enableZapi").equalsIgnoreCase("true")) {
            if (tr.getAttribute("TmsLink") != null) {
                for (String caseId : ((String) tr.getAttribute("TmsLink")).split(",")) {
                    try {
                        results.merge(caseId, true, (a, b) -> a & b);
                        Long issueID = Long.parseLong(caseId);
                        String id = new ZAPIManager().createExecution(ZAPIManager.prepareJsonForExecution(issueID, true));
                        new ZAPIManager().updateExecution(ZAPIManager.prepareJsonForExecutionUpdate(id, "Test passed", issueID, results.get(caseId)), id);
                    } catch (UnsupportedEncodingException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Implements onTestFailure listener . Creates failed execution at JIRA and saves link to report there
     *
     * @param result ITestResult containing information about the run test
     */
    @Override
    public void onTestFailure(ITestResult result) {
        super.onTestFailure(result);
        if (PropertiesContext.getInstance().getProperty("enableZapi").equalsIgnoreCase("true")) {
            if (result.getAttribute("TmsLink") != null) {
                for (String caseId : ((String) result.getAttribute("TmsLink")).split(",")) {
                    try {
                        results.merge(caseId, false, (a, b) -> a & b);
                        Long issueID = Long.parseLong(caseId);
                        String id = new ZAPIManager().createExecution(ZAPIManager.prepareJsonForExecution(issueID, false));
                        new ZAPIManager().updateExecution(ZAPIManager.prepareJsonForExecutionUpdate(id, "Test failed: "+result.getThrowable().getMessage() + " " + result.getThrowable().getCause(), issueID, results.get(caseId)), id);
                        String attachmentID = new ZAPIManager().attachScreenshot(issueID, id);
                    } catch (UnsupportedEncodingException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
