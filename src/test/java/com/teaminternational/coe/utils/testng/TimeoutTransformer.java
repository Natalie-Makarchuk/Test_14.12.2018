package com.teaminternational.coe.utils.testng;

import com.teaminternational.coe.utils.PropertiesContext;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Class implemented method transform to add timeout and retry Analyzer for test annotations
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see IAnnotationTransformer
 */
public class TimeoutTransformer implements IAnnotationTransformer {

    /**
     * Add timeout and rerun manager to each test annotation
     *
     * @param annotation The annotation that was read from your test class.
     * @param testClass If the annotation was found on a class, this parameter represents this class (null otherwise).
     * @param testConstructor If the annotation was found on a constructor, this parameter represents this constructor (null otherwise).
     * @param testMethod If the annotation was found on a method, this parameter represents this method (null otherwise).
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        long timeout = Integer.parseInt(PropertiesContext.getInstance().getProperty("test.timeout")) * 1000 ;
        //TODO: Check why it craches tests when timeout is set with error "Using soft asserts, but without @SoftAsserts annotation"
//        annotation.setInvocationTimeOut(timeout);
//        annotation.setTimeOut(timeout);
        annotation.setRetryAnalyzer(RerunManager.class);
    }
}
