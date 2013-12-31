package com.maximov.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Provides common access for sending and receiving data from a web server.
 * <p/>
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public interface IWebClient {
    /**
     * Opens input stream for the data from resource specified by the url
     *
     * @param url       the url of the resource
     * @param userAgent information about client type
     * @return response from the resource
     * @throws java.io.IOException
     */
    InputStream openRead(URL url, UserAgent userAgent) throws IOException;

    String downloadString(URL url, UserAgent userAgent) throws IOException;
}
