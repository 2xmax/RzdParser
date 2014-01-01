package com.maximov.data.providers.http;

/**
 * Information about client type that will be send
 * as header in HTTP request. More info:
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
 * <p/>
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */
public enum UserAgent {
    DEFAULT, //no any data provided
    FIREFOX, //typical desktop client
    OPERA_MINI //typical mobile phone user
}
