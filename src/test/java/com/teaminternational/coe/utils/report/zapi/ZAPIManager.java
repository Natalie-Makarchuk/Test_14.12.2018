package com.teaminternational.coe.utils.report.zapi;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.teaminternational.coe.utils.CommonHelper;
import com.teaminternational.coe.utils.CustomHttpClient;
import com.teaminternational.coe.utils.JSONparser;
import com.teaminternational.coe.utils.PropertiesContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.testng.Reporter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Class for managing API calls to Zephyr of JIRA Cloud
 * Have wrapper for Zephyr methods for creating execution, cycle, step, delete execution, cycle, update execution, cycle, step,
 * prepare JSON for any of it's methods, get server information.
 *
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
public class ZAPIManager {


    /**
     * Project ID in JIRA for ZAPI. Can be found in JIRA Dashboard in the project management "Project Settings" / "Details" in URL parameter pid. Should look like "10000" or "10001", etc.  See screenshots in <a href="/">"Overview"</a>.
     */
    private static String projectID = PropertiesContext.getInstance().getProperty("projectID");
    /**
     * Version ID in JIRA for ZAPI. Can be found in JIRA Dashboard in the project management "Project Settings" / "Versions" / click on the created version. Version ID will be in URL after "/versions/". Should look like "10000" or "10001", etc. See screenshots in <a href="/">"Overview"</a>.
     */
    private static String versionID = PropertiesContext.getInstance().getProperty("versionId");
    /**
     * Username for authorisation in ZAPI.  Can be found in JIRA Dashboard in JIRA Settings / Apps / ZAPI.  See screenshots in <a href="/">"Overview"</a>.
     */
    private static String username = PropertiesContext.getInstance().getProperty("userName");
    /**
     * Access key for authorisation in ZAPI. Can be found in JIRA Dashboard in JIRA Settings / Apps / ZAPI.  See screenshots in <a href="/">"Overview"</a>.
     */
    private static String zapiAccessKey = PropertiesContext.getInstance().getProperty("zapiAccessKey");
    /**
     * Secret key for authorisation in ZAPI. Can be found in JIRA Dashboard in JIRA Settings / Apps / ZAPI.  See screenshots in <a href="/">"Overview"</a>.
     */
    private static String secretKey = PropertiesContext.getInstance().getProperty("secretKey");
    /**
     * Base URL for ZAPI calls. It can be found in ZAPI documentation. At the moment it's https://prod-api.zephyr4jiracloud.com/connect
     */
    private static String zephyrBaseUrl = PropertiesContext.getInstance().getProperty("zephyrBaseURL");


    /**
     * Body of execution of program from command line.
     * Creates new ZAPIManager instance, get server info from server, creates execution for test with dummy issue id and updates execution with dummy comment and issue id number.
     * Dummy method just for example.
     *
     * @param args command line arguments
     * @throws Exception in case of errors
     */
    public static void main(String... args) throws Exception {
        ZAPIManager manager = new ZAPIManager();
        manager.getServerInfo();
        String executionID = manager.createExecution(prepareJsonForExecution(33967, false));
        manager.updateExecution(prepareJsonForExecutionUpdate(executionID, "Comment just for fun", 33967, false), executionID);
    }

    /**
     * Creates ObjectNode, define all field required for execution, converts it to string and returns. Also writes it to log.
     *
     * @param cycleID ID of cycle
     * @param issueId ID of issue
     * @param pass status of test, true - passed
     *
     * @return JSON string for execution
     */
    public static String prepareJsonForExecution(String cycleID, long issueId, boolean pass) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.putObject("status");
        ((ObjectNode) node.get("status")).put("id", pass ? 1 : 2);
        node.put("issueId", issueId);
        node.put("cycleId", cycleID);
        node.put("versionId", versionID);
        node.put("assignee", username);
        node.put("projectId", projectID);
        node.put("assigneeType", "assignee");
        Reporter.log(node.toString(), 1, true);
        return node.toString();
    }

    /**
     * Creates ObjectNode, defines all field required for execution update, converts it to string and returns. Also writes it to log.
     *
     * @param executionID ID of execution
     * @param comment Commentary about update
     * @param issueId ID of issue
     * @param pass status of test, true - passed
     *
     * @return JSON string for execution update
     */
    public static String prepareJsonForExecutionUpdate(String executionID, String comment, long issueId, boolean pass) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.putObject("status");
        ((ObjectNode) node.get("status")).put("id", pass ? 1 : 2);
        node.put("id", executionID);
        node.put("issueId", issueId);
        node.put("comment", comment.length() > 749 ? comment.substring(0, 749) : comment);
        node.put("cycleId", PropertiesContext.getInstance().getProperty("cycleId"));
        node.put("versionId", versionID);
        node.put("projectId", projectID);
        Reporter.log(node.toString(), 1, true);
        return node.toString();
    }

    /**
     * Creates ObjectNode, defines all field required for step update, converts it to string and returns. Also writes it to log.
     *
     * @param issueId ID of issue
     * @param stepId ID of step
     * @param executionID ID of execution
     * @param pass status of test, true - passed
     *
     * @return JSON string for step update
     */
    public static String prepareJsonForStepUpdate(long issueId, String stepId, String executionID, boolean pass) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.putObject("status");
        ((ObjectNode) node.get("status")).put("id", pass ? 1 : 2);
        node.put("issueId", issueId);
        node.put("stepId", stepId);
        node.put("executionId", executionID);
        Reporter.log(node.toString(), 1, true);
        return node.toString();
    }

    /**
     * Calls prepareJsonForExecution with parameter cycleId taken from PropertiesContext singleton and returns returned value
     *
     * @param issueId ID of issue
     * @param pass status of test, true - passed
     *
     * @return JSON string for execution
     */
    public static String prepareJsonForExecution(long issueId, boolean pass) {
        return prepareJsonForExecution(PropertiesContext.getInstance().getProperty("cycleId"), issueId, pass);
    }

    /**
     * Creates ObjectNode, define all field required for cycle creation, converts it to string and returns. Also writes it to log.
     *
     * @param cycleName Name of cycle
     *
     * @return JSON string for cycle creation
     */
    public static String prepareJsonForCycle(String cycleName) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("name", cycleName);
        node.put("environment", PropertiesContext.getInstance().getProperty("envurl"));
        node.put("description", "Cycle created for smoke run");
        node.put("startDate", System.currentTimeMillis());
        node.put("projectId", projectID);
        node.put("versionId", versionID);
        Reporter.log(node.toString(), 1, true);
        return node.toString();
    }

    /**
     * Get JSON Web Token for defined requestType and URL for 360 seconds
     *
     * @param requestType Type of request
     * @param url URL for token generation
     *
     * @return generated JWT
     */
    private String getJWTToken(String requestType, String url) {
        Reporter.log("Generating JWT for url: " + url, 1, true);
        JwtGenerator jwtGenerator = new JwtGenerator(zapiAccessKey, secretKey, username, zephyrBaseUrl);

        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        int expirationInSec = 360;
        return jwtGenerator.generateJWT(requestType, uri, expirationInSec);
    }

    /**
     * Get Server (Zephyr for JIRA Cloud) Information. Writes info to log.
     *
     */
    public void getServerInfo() {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/serverinfo";
        String gwtToken = getJWTToken("GET", getServerInfoURL);

        HttpGet request = new HttpGet(getServerInfoURL);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info GET returned status: " + response.getStatusLine().toString(), 1, true);
            Reporter.log("Server info GET returned status: " + JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8")), 1, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send requests to ZAPI for removing cycle. Writes information to log.
     *
     * @param cycleId ID of cycle
     */
    public void deleteCycle(String cycleId) {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/cycle/" + cycleId + "?versionId=" + versionID + "&projectId="+projectID;
        String gwtToken = getJWTToken("DELETE", getServerInfoURL);

        HttpDelete request = new HttpDelete(getServerInfoURL);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info DELETE returned status: " + response.getStatusLine().toString(), 1, true);
            Reporter.log("Server info DELETE returned status: " + JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8")), 1, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send requests to ZAPI for creating cycle. Writes information to log.
     *
     * @param body string entity of request
     *
     * @return created CycleID or empty string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    public String createCycle(String body) throws UnsupportedEncodingException {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/cycle";
        String gwtToken = getJWTToken("POST", getServerInfoURL);

        HttpPost request = new HttpPost(getServerInfoURL);
        StringEntity se = new StringEntity(body);
        request.setEntity(se);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info POST returned status: " + response.getStatusLine().toString(), 1, true);
            ObjectNode npo = JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            Reporter.log("Server info POST returned status: " + npo, 1, true);
            return npo.get("cycleIndex").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Send requests to ZAPI for creating execution. Writes information to log.
     *
     * @param body string entity of request
     *
     * @return Execution ID or empty string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    public String createExecution(String body) throws UnsupportedEncodingException {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/execution";
        String gwtToken = getJWTToken("POST", getServerInfoURL);

        HttpPost request = new HttpPost(getServerInfoURL);
        StringEntity se = new StringEntity(body);
        request.setEntity(se);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info POST returned status: " + response.getStatusLine().toString(), 1, true);
            ObjectNode npo = JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            Reporter.log("Server info POST returned status: " + npo, 1, true);
            return npo.get("execution").get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Send requests to ZAPI for updating execution. Writes information to log.
     *
     * @param body string entity of request
     * @param executionID ID of execution
     *
     * @return Execution ID or empty string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    public String updateExecution(String body, String executionID) throws UnsupportedEncodingException {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/execution/" + executionID;
        String gwtToken = getJWTToken("PUT", getServerInfoURL);

        HttpPut request = new HttpPut(getServerInfoURL);
        StringEntity se = new StringEntity(body);
        request.setEntity(se);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info PUT returned status: " + response.getStatusLine().toString(), 1, true);
            ObjectNode npo = JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            Reporter.log("Server info PUT returned status: " + npo, 1, true);
            return npo.get("execution").get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Send requests to ZAPI for updating step. Writes information to log.
     *
     * @param body string entity of request
     * @param stepResultId ID of step result
     *
     * @return Step ID or empty string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    public String updateStep(String body, String stepResultId) throws UnsupportedEncodingException {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/stepresult/" + stepResultId;
        String gwtToken = getJWTToken("PUT", getServerInfoURL);

        HttpPut request = new HttpPut(getServerInfoURL);
        StringEntity se = new StringEntity(body);
        request.setEntity(se);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info PUT returned status: " + response.getStatusLine().toString(), 1, true);
            ObjectNode npo = JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            Reporter.log("Server info PUT returned status: " + npo, 1, true);
            return npo.get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Send requests to ZAPI for deleting execution. Writes information to log.
     *
     * @param executionId ID of execution
     *
     */
    public void deleteExecution(String executionId) {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/execution/" + executionId;
        String gwtToken = getJWTToken("DELETE", getServerInfoURL);

        HttpDelete request = new HttpDelete(getServerInfoURL);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info DELETE returned status: " + response.getStatusLine().toString(), 1, true);
            Reporter.log("Server info DELETE returned status: " + JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8")), 1, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send requests to ZAPI for creating attachment. Writes information to log.
     *
     * @param issueId ID of issue
     * @param entityID ID of execution
     * @param name name of file field for uploading
     * @param filename filename of uploaded file
     * @param file image in byte's sequence
     *
     * @return Attachment ID or empty string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    public String createAttachment(long issueId, String entityID, String name, String filename, byte[] file) throws UnsupportedEncodingException {
        String getServerInfoURL = zephyrBaseUrl + "/public/rest/api/1.0/attachment?issueId="+issueId
                +"&versionId="+versionID+"&entityName=execution&cycleId="+PropertiesContext.getInstance().getProperty("cycleId")
                +"&entityId="+entityID+"&comment=Execution%20failed&projectId="+projectID;
        String gwtToken = getJWTToken("POST", getServerInfoURL);

        HttpPost request = new HttpPost(getServerInfoURL);
        CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
        request.setHeader(HttpHeaders.AUTHORIZATION, gwtToken);
        request.setHeader("zapiAccessKey", zapiAccessKey);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody(name, file, ContentType.DEFAULT_BINARY, filename);
        HttpEntity me = builder.build();
        request.setEntity(me);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(request);
            Reporter.log("Server info POST returned status: " + response.getStatusLine().toString(), 1, true);
            ObjectNode npo = JSONparser.getJSONString(IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            Reporter.log("Server info POST returned status: " + npo, 1, true);
            return npo.get("execution").get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Send requests to ZAPI for creating attachment with Screenshot.
     *
     * @param issueId ID of issue
     * @param entityID ID of execution
     *
     * @return Attachment ID or empty string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    public String attachScreenshot(long issueId, String entityID) throws UnsupportedEncodingException {
        CommonHelper commonHelper = CommonHelper.getInstance();
        return createAttachment(issueId, entityID, "Screenshot", "Screenshot_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy__kk_mm"))+".png", commonHelper.getScreenshotBytes());
    }

}
