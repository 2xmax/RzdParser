package com.maximov.selenium.pageobjects;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class PageException extends Exception {
    public PageException(String msg, Exception cause) {
        super(msg, cause);
    }

    public PageException(String msg) {
        super(msg);
    }
}
