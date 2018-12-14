package com.teaminternational.coe.utils.testng;

import com.teaminternational.coe.utils.PropertiesContext;
import org.testng.IAlterSuiteListener;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import java.util.List;


/**
 * Class implements methods alter of IAlterSuiteListener interface and defining parallel mode for test suite with thread count taken from configuration, also writes into lof about it.
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see IAlterSuiteListener
 */
public class ThreadCountTransformer implements IAlterSuiteListener {
    /**
     * Override suit parameters to inject variable thread count  and write log message about it
     *
     * @param suites The list of XmlSuites that are part of the current execution.
     */
    @Override
    public void alter(List<XmlSuite> suites) {
        //TODO: test framework with multithread. be sure that screenshots saved correctly for all threads
        XmlSuite suite = suites.get(0);
        int verbose = Integer.parseInt(PropertiesContext.getInstance().getProperty("verbose"));
        suite.setVerbose(verbose);
        Reporter.log("Setting verbose level for suit: " + suite.getVerbose(), 1, true);
        int threads_count = Integer.parseInt(PropertiesContext.getInstance().getProperty("threadcount"));
        if (threads_count>1) {
            suite.setParallel(XmlSuite.ParallelMode.TESTS);
            suite.setThreadCount(threads_count);
            Reporter.log("Setting thread count for suit: " + suite.getThreadCount(), 1, true);
        }
    }
}
