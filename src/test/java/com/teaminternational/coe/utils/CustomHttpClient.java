package com.teaminternational.coe.utils;

import com.teaminternational.coe.entities.users.User;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Simple custom http client class used by ZAPIManager for submitting requests to the Zephyr for JIRA Cloud API
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see com.teaminternational.coe.utils.report.zapi.ZAPIManager
 */
public class CustomHttpClient {

    /**
     * Creates and made basic configuration of object of CloseableHttpClient class for making requests to Zephyr API.
     *
     * @return object of CloseableHttpClient class
     */
    public synchronized CloseableHttpClient getHttpClient() {
        CloseableHttpClient client = null;
        try {
            SSLContextBuilder sslBuilder = new SSLContextBuilder();
            sslBuilder.loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslBuilder.build());

            client = HttpClientBuilder.create()
                    .setSSLSocketFactory(sslsf)
                    .disableCookieManagement()
                    .disableAutomaticRetries()
                    .disableRedirectHandling()
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    /**
     * Send POST request to the specified URL and JSON body, return response as CloseableHttpResponse object.
     *
     * @param hostURL URL to which request will be send
     * @param body text of body of request, can be the list of post parameters
     *
     * @return object of CloseableHttpResponse class or null in case of error
     */
    public synchronized CloseableHttpResponse submitPOSTrequest(String hostURL, String body) {

        try {
            HttpPost request = new HttpPost(hostURL);
            CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
            request.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
            StringEntity se = new StringEntity(body);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            request.setEntity(se);
            return closeableHttpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send POST request to the specified URL and JSON body with authorization and keeping session cookie, return response as CloseableHttpResponse object.
     *
     * @param sessionCookie session id which was stored in cookie
     * @param authToken access token for authorisation on service
     * @param hostURL URL to which request will be send
     * @param body text of body of request, can be the list of post parameters
     *
     * @return object of CloseableHttpResponse class or null in case of error
     */
    public synchronized CloseableHttpResponse submitPOSTrequest(String sessionCookie, String authToken, String hostURL, String body) {

        try {
            HttpPost request = new HttpPost(hostURL);
            CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
            request.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader("Cookie", "session=" + sessionCookie);

            StringEntity se = new StringEntity(body);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            request.setEntity(se);
            return closeableHttpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send GET request to the specified URL, return response as CloseableHttpResponse object.
     *
     * @param hostURL URL to which request will be send
     *
     * @return object of CloseableHttpResponse class or null in case of error
     */
    public synchronized CloseableHttpResponse submitGETrequest(String hostURL) {

        try {
            HttpGet request = new HttpGet(hostURL);
            CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
            request.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
            return closeableHttpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send PUTT request to the specified URL and JSON body, return response as CloseableHttpResponse object.
     *
     * @param hostURL URL to which request will be send
     * @param body text of body of request, can be the list of put parameters
     *
     * @return object of CloseableHttpResponse class or null in case of error
     */
    public synchronized CloseableHttpResponse submitPutRequest(String hostURL, String body) {

        try {
            HttpPut request = new HttpPut(hostURL);
            CloseableHttpClient closeableHttpClient = new CustomHttpClient().getHttpClient();
            request.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
            StringEntity se = new StringEntity(body);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            request.setEntity(se);
            return closeableHttpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
