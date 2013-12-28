package com.maximov.selenium.pageobjects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

    protected void wait(int timeout) throws PageException {
        synchronized (driver) {
            try {
                driver.wait(timeout);
            } catch (InterruptedException e) {
                throw new PageException("wait failed", e);
            }
        }
    }
}
