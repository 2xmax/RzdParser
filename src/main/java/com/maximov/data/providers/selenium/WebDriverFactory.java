package com.maximov.data.providers.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public final class WebDriverFactory {
    public static WebDriver create() throws IOException {
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("webdriver.properties"));

        String type = properties.getProperty("webdriver.type");
        String binary = properties.getProperty("webdriver.binary");
        Map<String, Object> prefs = new HashMap<String, Object>();
        for (Object key : properties.keySet()) {
            String strKey = (String) key;
            if (strKey.startsWith("webdriver.options.")) {
                prefs.put(strKey.replace("webdriver.options.", ""), Integer.parseInt(properties.getProperty(strKey)));
            }
        }

        if (type.toLowerCase().equals("firefox")) {
            FirefoxProfile profile = new FirefoxProfile();

            for (String key : prefs.keySet()) {
                if (prefs.get(key) instanceof Integer) {
                    profile.setPreference(key, (Integer) prefs.get(key));
                } else {
                    profile.setPreference(key, prefs.get(key).toString());
                }
            }

            if (binary != null) {
                return new FirefoxDriver(
                        new FirefoxBinary(new File(binary)),
                        profile);
            } else {
                return new FirefoxDriver(profile);
            }
        } else if (type.toLowerCase().equals("chrome")) {
            ChromeOptions profile = new ChromeOptions();

            if (binary != null) {
                profile.setBinary(new File(binary));
                System.setProperty("webdriver.chrome.driver", binary);
            }

            ChromeDriver driver = new ChromeDriver(profile);
            return driver;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
