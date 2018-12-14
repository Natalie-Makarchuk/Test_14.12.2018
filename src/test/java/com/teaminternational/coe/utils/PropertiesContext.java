package com.teaminternational.coe.utils;

import org.testng.Reporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * Properties Context Class. Used in whole projects to get config of all used systems from *.properties files in /resources folder. <br>
 * Loads list of properties from resources folder to be available in system in singleton manner. <br>
 * Available upgrades:<br>
 * Update to use overridden properties located in external folder;<br>
 * Add more ui properties files to divide parts of application.<br>
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 */
public class PropertiesContext {

    /**
     * Name of Properties file for whole environment.
     */
    private static final String ENV_PROPERTIES = "env";

    /**
     * Name of Properties file for database.
     */
    private static final String DB_PROPERTIES = "cassandra";

    /**
     * Name of Properties file for uiMap.
     */
    private static final String UI_PROPERTIES = "uiMap";
    /**
     * Static variable for singleton instance of PropertiesContext class
     */
    private static PropertiesContext instance = new PropertiesContext();
    /**
     * Used for loading properties from file with whole environment properties and saving it into the General Map
     */
    private Properties envMap = new Properties();
    /**
     * Used for loading properties from file with database properties and saving it into the General Map
     */
    private Properties dbMap = new Properties();
    /**
     * Used for loading properties from file with uiMap properties and saving it into the General Map
     */
    private Properties uiMap = new Properties();
    /**
     * Used for loading properties from file with userMap properties and saving it into the General Map
     */
    private Properties userMap = new Properties();
    /**
     * Used for loading properties from file with Gradle properties and saving it into the General Map
     */
    private Properties gradleMap = new Properties();
    /**
     * General Map for keeping all properties in this variable.
     */
    private Properties generalMap = new Properties();

    /**
     * Initialize PropertyContext. Call method init()
     *
     * @see #init()
     */
    private PropertiesContext() {
        init();
    }

    /**
     * Create a new PropertiesContext instance if it's not created before and store in private static variable
     *
     * @return created instance of PropertiesContext singleton
     */
    public static PropertiesContext getInstance() {
        if (instance == null) {
            instance = new PropertiesContext();
        }
        return instance;
    }

    /**
     * Load properties from *.properties files in /resources folder. Save all properties into one private variable generalMap
     *
     * @see #generalMap
     */
    private void init() {
        loadGradleProperties(gradleMap);
        loadPropertiesFromClasspath(envMap, ENV_PROPERTIES);
        loadPropertiesFromClasspath(uiMap, UI_PROPERTIES);
        loadPropertiesFromClasspath(dbMap, DB_PROPERTIES);
        generalMap.putAll(envMap);
        generalMap.putAll(uiMap);
        generalMap.putAll(gradleMap);
        generalMap.putAll(dbMap);
        //TODO: add usage of unused settings from properties or remove its

        String USER_PROPERTIES = "users";
        loadPropertiesFromClasspath(userMap, USER_PROPERTIES);
        generalMap.putAll(userMap);

        if (System.getProperty("envurl") != null) {
            generalMap.setProperty("envurl", System.getProperty("envurl"));
        }
        if (System.getProperty("browser") != null) {
            generalMap.setProperty("browser", System.getProperty("browser"));
        }
        if (System.getProperty("maxRetryCount") != null) {
            generalMap.setProperty("maxRetryCount", System.getProperty("maxRetryCount"));
        }
        if (System.getProperty("threadcount") != null) {
            generalMap.setProperty("threadcount", System.getProperty("threadcount"));
        }

        if (System.getProperty("enableZapi") != null) {
            generalMap.setProperty("enableZapi", System.getProperty("enableZapi"));
        }

        if (System.getProperty("networkFileDir") != null) {
            generalMap.setProperty("networkFileDir", System.getProperty("networkFileDir"));
        }
    }

    /**
     * Return loaded into field generalMap property by specified key.
     *
     * @param key The name of required property
     *
     * @return Value of loaded property
     *
     * @throws NullPointerException If key wasn't found in the generalMap field
     */
    public String getProperty(String key) {
        String result = (String) generalMap.get(key);
        if (result != null) {
            return result;
        } else {
            throw new NullPointerException("Property " + key + " was not found");
        }
    }

    /**
     * Set property specified by key into specified value in generalMap field.
     *
     * @param key The name of property
     * @param value The value that must be set
     *
     */
    public void setProperty(String key, String value) {
        generalMap.setProperty(key, value);
    }

    /**
     * Clear generalMap field from all values.
     *
     */
    public void clear() {
        generalMap.clear();
    }

    /**
     * Return full name of file with properties.
     *
     * @param fileName Name of file without extension
     *
     * @return full name of file
     */
    private String getFullFileName(String fileName) {
        return fileName + ".properties";
    }

    /**
     * Load properties of Gradle from  file gradle.properties
     *
     * @param props Variable where loaded properties stored
     *
     */
    private void loadGradleProperties(Properties props) {
        try {
            String fileName = "gradle.properties";
            String path = System.getProperty("user.dir");
            Reader resource = new FileReader(new File(path + "/" + fileName));
            if (resource != null) {
                props.load(resource);
            }
        } catch (IOException e) {
            Reporter.log("Missing or corrupt property file", 1, true);
        }
    }

    /**
     * Load properties of specified group from file _group_.properties in /resources folder.
     *
     * @param props Variable where loaded properties stored
     * @param fileName Name of file that must be loaded
     *
     */
    private void loadPropertiesFromClasspath(Properties props, String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream resourceAsStream = classLoader.getResourceAsStream(getFullFileName(fileName));


            if (resourceAsStream != null) {
                props.load(resourceAsStream);
            }
        } catch (IOException e) {
            Reporter.log("Missing or corrupt property file", 1, true);
        }
    }
}
