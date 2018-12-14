package com.teaminternational.coe.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.openqa.selenium.WebDriverException;
import org.testng.Reporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Custom JSONParser helper class.
 * Used to get ObjectNode from JSON string, parse json files
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
public class JSONparser {
    /**
     * Parse JSON string loaded from file into ObjectNode.
     * Write string into log, and write message into log in case of parsing error
     *
     * @param fileName name of the file stored in the special folder and containing JSON string
     *
     * @return parsed ObjectNode
     */
    public synchronized static ObjectNode getJSON(String fileName) {
        Reporter.log("Getting JSON from file: " + fileName, 1, true);
        String path = System.getProperty("user.dir");
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedReader br =
                     new BufferedReader(new FileReader(path + "/src/test/resources/json/" + fileName))) {
            return (ObjectNode) mapper.readTree(br);
        } catch (Exception e) {
            try (BufferedReader br =
                         new BufferedReader(new FileReader(path + "/src/test/resources/json/" + fileName))) {
                return (ObjectNode) mapper.readTree(br).get(0);
            } catch (Exception ignored) {
                Reporter.log("Failed to load JSON from file", 1, true);
                throw new WebDriverException("Failed to load JSON from file");
            }
        }
    }


    /**
     * Parse JSON string into ObjectNode.
     * Write string into log, and write message into log in case of parsing error
     *
     * @param string not parsed JSON string
     *
     * @return parsed ObjectNode
     */
    public synchronized static ObjectNode getJSONString(String string) {
        Reporter.log("Getting JSON from string: " + string, 10, true);
        ObjectNode objectNode = null;
        ObjectMapper mapper = new ObjectMapper();
        BufferedReader br =
                new BufferedReader((new StringReader(string)));
        try {
            objectNode = (ObjectNode) mapper.readTree(br);
        } catch (Exception e) {
            try {
                objectNode = (ObjectNode) mapper.readTree(string).get(0);//if JSON happens to be an array
            } catch (Exception ex) {
                Reporter.log("Failed to load JSON from string\n", 1, true);
                Reporter.log(ex.toString(), 1, true);

            }
        }
        return objectNode;
    }

    /**
     * Get ObjectNode from response on http-request
     *
     * @param response object of CloseableHttpResponse class taken as result of http request
     *
     * @return parsed ObjectNode or null in case of parsing error
     */
    public synchronized static ObjectNode getJSONfromResponse(CloseableHttpResponse response) {
        try {
            return JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
