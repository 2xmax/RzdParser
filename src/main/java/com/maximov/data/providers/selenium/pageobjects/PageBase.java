package com.maximov.data.providers.selenium.pageobjects;

import com.maximov.data.SearchException;
import org.openqa.selenium.WebDriver;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public abstract class PageBase {
    protected final WebDriver driver;

    protected PageBase(WebDriver driver) {
        this.driver = driver;
    }

    protected void wait(int timeout) throws SearchException {
        synchronized (driver) {
            try {
                driver.wait(timeout);
            } catch (InterruptedException e) {
                throw new SearchException("wait failed", e);
            }
        }
    }
}
