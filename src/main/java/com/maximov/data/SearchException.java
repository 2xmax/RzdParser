package com.maximov.data;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class SearchException extends Exception {
    public SearchException(String msg, Exception cause) {
        super(msg, cause);
    }

    public SearchException(String msg) {
        super(msg);
    }
}
