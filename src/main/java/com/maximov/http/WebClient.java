package com.maximov.http;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

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

public class WebClient implements IWebClient {
    /**
     * request timeout before the exception raised.
     */
    private final int MAX_TIMEOUT_MS = 60000;
    private CookieManager manager;

    /**
     * Downloads the specified resource as a String.
     *
     * @param url       the url of the resource
     * @param userAgent information about client type
     * @return response from the resource
     * @throws java.io.IOException
     */
    @Override
    public InputStream openRead(URL url, UserAgent userAgent) throws IOException {
        if (manager == null) {
            manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(true);
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setReadTimeout(MAX_TIMEOUT_MS);
        switch (userAgent) {
            case FIREFOX:
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2;"
                        + " WOW64; rv:19.0) Gecko/20100101 Firefox/19.0");
                break;
            case OPERA_MINI:
                conn.setRequestProperty("User-Agent", "Opera/9.50 (J2ME/MIDP;"
                        + " Opera Mini/4.0.10031/298; U; en)");
                break;
            default:
                //no custom user client data
                break;
        }
        String encoding = conn.getContentEncoding();

        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            return new GZIPInputStream(conn.getInputStream());
        } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
            return new InflaterInputStream(conn.getInputStream(),
                    new Inflater(true));
        }
        return conn.getInputStream();
    }

    @Override
    public String downloadString(URL url, UserAgent userAgent) throws IOException {
        InputStream inputStream = openRead(url, userAgent);
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        return writer.toString();
    }
}
