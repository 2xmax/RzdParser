package com.maximov.selenium.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public final class HomePage extends PageBase {
    public HomePage(WebDriver driver) {
        super(driver);
    }

    public ResultsPage fillSearchForm(String from, String to, Date when) throws PageException {
        final String rootUrl = "http://rzd.ru";
        driver.navigate().to(rootUrl);

        WebElement fromEl = driver.findElement(By.id("name0"));
        fromEl.sendKeys(from);

        WebElement toEl = driver.findElement(By.id("name1"));
        toEl.sendKeys(to);

        WebElement dateEl = driver.findElement(By.id("date0"));
        String s = formatDate(when);
        dateEl.clear();
        dateEl.sendKeys(s);

        int nAttempts = 20;
        while (nAttempts > 0) {
            WebElement submitEl = driver.findElement(By.id("Submit"));
            submitEl.click();
            if (!driver.getCurrentUrl().equals(rootUrl)) {
                return new ResultsPage(driver);
            }
            nAttempts--;
            wait(500);
        }
        throw new PageException("Home page is stuck");
    }

    private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }
}
