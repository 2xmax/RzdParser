package com.maximov.http;

import com.maximov.selenium.pageobjects.PageException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class StationService {
    private static IWebClient webClient;

    private static IWebClient getWebClient() {
        if (webClient == null) {
            webClient = new WebClient();
        }
        return webClient;
    }

    public static int getStationId(String name) throws PageException, IOException {
        IWebClient webClient = getWebClient();
        URL url = new URL("http://pass.rzd.ru/suggester?&lang=ru&lat=0&compactMode=y&stationNamePart=" + name );
        String str = webClient.downloadString(url, UserAgent.FIREFOX);
        JSONArray arr = new JSONArray(str);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("n").toLowerCase().equals(name.toLowerCase())) {
                return obj.getInt("c");
            }
        }
        throw new PageException("Station not found");
    }
}
