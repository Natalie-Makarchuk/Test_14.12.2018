package com.teaminternational.coe.utils.report.zapi;

import com.atlassian.connect.play.java.AcHost;
import com.atlassian.connect.play.java.http.HttpMethod;
import com.atlassian.fugue.Option;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.TimeUtil;
import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.core.writer.JwtClaimsBuilder;
import com.atlassian.jwt.core.writer.NimbusJwtWriterFactory;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtSigningException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.httpclient.CanonicalHttpUriRequest;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.jwt.writer.JwtWriter;
import com.atlassian.jwt.writer.JwtWriterFactory;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.jwt.JwtConstants.HttpRequests.JWT_AUTH_HEADER_PREFIX;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class for generating JSON Web Token
 * Generated token used in Zephyr integration in class ZAPIManager
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 * @see ZAPIManager
 */
public class JwtGenerator {

    /**
     * Constant array of char delimiters
     */
    private static final char[] QUERY_DELIMITERS = new char[]{'&'};
    /**
     * Field for User name
     */
    Option<String> USER_NAME;
    /**
     * Field for JIRA shared secret key
     */
    String JIRA_SHARED_SECRET;
    /**
     * Field for Zephyr URL
     */
    String ZEPHYR_BASE_URL;
    /**
     * Object for atlassian connect
     */
    AcHost host;
    /**
     * Field for JIRA host Key
     */
    private String JIRA_HOST_KEY;
    /**
     * Keep number of seconds for expiration
     */
    private int jwtExpiryWindowSeconds;
    /**
     * Object to keep class for generating JWT
     */
    private JwtWriterFactory jwtWriterFactory;

    /**
     * Setter for object variables
     *
     * @param accessKey JIRA host key
     * @param secretKey JIRA shared secret key
     * @param userName User name
     * @param zephyrBaseUrl Zephyr URL
     */
    public JwtGenerator(String accessKey, String secretKey, String userName, String zephyrBaseUrl) {
        JIRA_HOST_KEY = accessKey;
        JIRA_SHARED_SECRET = secretKey;

        ZEPHYR_BASE_URL = zephyrBaseUrl;
        USER_NAME = Option.option(userName);

        host = new AcHost();
        host.setKey(JIRA_HOST_KEY);
        host.setSharedSecret(JIRA_SHARED_SECRET);
    }

    /**
     * Decodes URL using UTF-8 encoding.
     *
     * @param content string to decode
     *
     * @return decoded string
     *
     * @throws UnsupportedEncodingException in case of error
     */
    private static String urlDecode(String content) throws UnsupportedEncodingException {
        return null == content ? null : URLDecoder.decode(content, "UTF-8");
    }

    /**
     * Generate JSON Web Token wrapper
     *
     * @param requestMethod method of request
     * @param uri request URI
     * @param jwtExpiryWindowSeconds expiration of token in seconds
     *
     * @return string JSON Web Token
     *
     */
    public String generateJWT(String requestMethod, URI uri, int jwtExpiryWindowSeconds) {
        jwtWriterFactory = new NimbusJwtWriterFactory();
        this.jwtExpiryWindowSeconds = jwtExpiryWindowSeconds;
        //   final int jwtExpiryWindowSeconds = 60 * 3;
        Option<String> jwt = null;
        try {
            final URI uriWithoutProductContext = getUri(uri, ZEPHYR_BASE_URL);

            jwt = generate(HttpMethod.valueOf(requestMethod), uriWithoutProductContext, new HashMap<>(), host, USER_NAME);
        } catch (Exception e) {
        }

        return jwt.getOrNull();
    }

    /**
     * Generate URI using provided uri and baseURL
     *
     * @param uri request URI
     * @param baseUrlString Base URL
     *
     * @return Full URI
     *
     * @throws URISyntaxException if any error.
     */
    private URI getUri(URI uri, String baseUrlString) throws URISyntaxException {
        final String path = uri.getPath();
        final URI baseUrl = new URI(baseUrlString);
        final String productContext = baseUrl.getPath();
        final String pathWithoutProductContext = path.substring(productContext.length());
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                pathWithoutProductContext, uri.getQuery(), uri.getFragment());
    }

    /**
     * Generate JSON Web Token
     *
     * @param httpMethod HTTP method
     * @param url URL of request
     * @param parameters parameters for request
     * @param acHost Atlassian connect host object
     * @param userId User ID
     *
     * @return String object which contains JWT
     *
     * @throws JwtIssuerLacksSharedSecretException if any error.
     * @throws JwtUnknownIssuerException if any error.
     */
    public Option<String> generate(HttpMethod httpMethod, URI url, Map<String, List<String>> parameters, AcHost acHost,
                                   Option<String> userId)
            throws JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException {

        checkArgument(null != parameters, "Parameters Map argument cannot be null");
        checkNotNull(acHost);

        Map<String, String[]> paramsAsArrays = Maps.transformValues(parameters, input -> checkNotNull(input).toArray(new String[input.size()]));
        return Option.some(JWT_AUTH_HEADER_PREFIX + encodeJwt(httpMethod, url, paramsAsArrays, userId.getOrNull(),
                acHost));
    }

    /**
     * Generate second part of JSON Web Token
     *
     * @param httpMethod HTTP method
     * @param targetPath URL of request
     * @param params parameters for request
     * @param userKeyValue User ID
     * @param acHost Atlassian connect host object
     *
     * @return String object which contains second part of JWT
     *
     * @throws JwtIssuerLacksSharedSecretException if any error.
     * @throws JwtUnknownIssuerException if any error.
     */
    private String encodeJwt(HttpMethod httpMethod, URI targetPath, Map<String, String[]> params, String userKeyValue,
                             AcHost acHost) throws JwtUnknownIssuerException, JwtIssuerLacksSharedSecretException {
        checkArgument(null != httpMethod, "HttpMethod argument cannot be null");
        checkArgument(null != targetPath, "URI argument cannot be null");

        JwtJsonBuilder jsonBuilder = new JsonSmartJwtJsonBuilder()
                .issuedAt(TimeUtil.currentTimeSeconds())
                .expirationTime(TimeUtil.currentTimePlusNSeconds(jwtExpiryWindowSeconds))
                .issuer(host.getKey());

        if (null != userKeyValue) {
            jsonBuilder = jsonBuilder.subject(userKeyValue);
        }

        Map<String, String[]> completeParams = params;

        try {
            if (!StringUtils.isEmpty(targetPath.getQuery())) {
                completeParams = new HashMap<>(params);
                completeParams.putAll(constructParameterMap(targetPath));
            }

            CanonicalHttpUriRequest canonicalHttpUriRequest = new CanonicalHttpUriRequest(httpMethod.toString(),
                    targetPath.getPath(), "", completeParams);

            JwtClaimsBuilder.appendHttpRequestClaims(jsonBuilder, canonicalHttpUriRequest);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String build = jsonBuilder.build();
        return issueJwt(build);
    }

    /**
     * Convert prepared JSON string to second part of JWT
     *
     * @param jsonPayload JSON prepared string
     *
     * @return String object which contains second part of JWT
     *
     * @throws JwtSigningException if any error.
     * @throws JwtIssuerLacksSharedSecretException if any error.
     * @throws JwtUnknownIssuerException if any error.
     */
    private String issueJwt(String jsonPayload) throws JwtSigningException, JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException {
        return getJwtWriter().jsonToJwt(jsonPayload);
    }

    /**
     * Get JwtWriter object generated by jwtWriterFactory field
     *
     * @return JwtWriter object
     *
     * @throws JwtIssuerLacksSharedSecretException if any error.
     * @throws JwtUnknownIssuerException if any error.
     */
    private JwtWriter getJwtWriter() throws JwtUnknownIssuerException, JwtIssuerLacksSharedSecretException {
        return jwtWriterFactory.macSigningWriter(SigningAlgorithm.HS256, JIRA_SHARED_SECRET);
    }

    /**
     * Parse URI parameters and put into MAP object
     *
     * @param uri Query URI
     * @return MAP object with query parameters
     *
     * @throws UnsupportedEncodingException if any error.
     */
    private Map<String, String[]> constructParameterMap(URI uri) throws UnsupportedEncodingException {
        final String query = uri.getQuery();
        if (query == null) {
            return Collections.emptyMap();
        }
        Map<String, String[]> queryParams = new HashMap<>();
        CharArrayBuffer buffer = new CharArrayBuffer(query.length());
        buffer.append(query);
        ParserCursor cursor = new ParserCursor(0, buffer.length());

        while (!cursor.atEnd()) {
            NameValuePair nameValuePair = BasicHeaderValueParser.DEFAULT.parseNameValuePair(buffer, cursor, QUERY_DELIMITERS);
            if (!StringUtils.isEmpty(nameValuePair.getName())) {
                String decodedName = urlDecode(nameValuePair.getName());
                String decodedValue = urlDecode(nameValuePair.getValue());
                String[] oldValues = queryParams.get(decodedName);
                String[] newValues = null == oldValues ? new String[1] : Arrays.copyOf(oldValues, oldValues.length + 1);
                newValues[newValues.length - 1] = decodedValue;
                queryParams.put(decodedName, newValues);
            }
        }
        return queryParams;
    }

}
