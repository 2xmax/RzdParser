package com.maximov.http;

import com.maximov.data.ITrainService;
import com.maximov.data.Train;
import com.maximov.data.TrainFilter;
import com.maximov.data.TrainSearchResult;
import com.maximov.selenium.pageobjects.PageException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class TrainService implements ITrainService {
    private final String TICKETS_URL = "http://pass.rzd.ru/timetable/public/ru?STRUCTURE_ID=735&layer_id=5371&dir=0&tfl=3&checkSeats=1&st0=%s&code0=%d&dt0=%s&st1=%s&code1=%d&dt1=%s&rid=%d&SESSION_ID=%d";
    private final String SESSION_URL = "http://pass.rzd.ru/timetable/public/ru?STRUCTURE_ID=735&layer_id=5371&dir=0&tfl=3&checkSeats=1&st0=%s&code0=%d&dt0=%s&st1=%s&code1=%d&dt1=%s";
    private final String STATION_URL = "http://pass.rzd.ru/suggester?&lang=ru&lat=0&compactMode=y&stationNamePart=%s";
    private final Map<String, Integer> stationCache = new HashMap<String, Integer>();
    private IWebClient webClient;

    private IWebClient getWebClient() {
        if (webClient == null) {
            webClient = new WebClient();
        }
        return webClient;
    }

    @Override
    public TrainSearchResult find(TrainFilter request) throws PageException, IOException {

        RequestContext params = getRequestContext(request);
        JSONObject obj = null;
        int maxRetry = 10;
        long waitTimeout = 1000;
        for (int i = 0; i < maxRetry; i++) {
            try {
                if (obj != null && "OK".equals(obj.getString("result"))) {
                    return parse(obj);
                } else {
                    Thread.sleep(waitTimeout);
                    obj = getJson(request, params);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new TrainSearchResult(true);
    }

    private JSONObject getJson(TrainFilter request, RequestContext params) throws IOException, PageException {
        URL url = new URL(String.format(TICKETS_URL, request.getFrom(), getStationId(request.getFrom()), formatDate(request.getWhen()), request.getTo(), getStationId(request.getTo()), formatDate(request.getWhen()), params.getrId(), params.getSessionId()));
        String searchResult = getWebClient().downloadString(url, UserAgent.DEFAULT);
        JSONObject obj = new JSONObject(searchResult);
        return obj;
    }

    private TrainSearchResult parse(JSONObject obj) {
        List<Train> trains = new ArrayList<Train>();
        JSONArray tr = obj.getJSONArray("tp");
        for (int i = 0; i < tr.length(); i++) {
            JSONObject item = tr.getJSONObject(i);
            JSONArray list = item.getJSONArray("list");
            for (int j = 0; j < list.length(); j++) {
                Train train = parseTrain(list.getJSONObject(j));
                trains.add(train);
            }
        }
        return new TrainSearchResult(trains);
    }

    private Train parseTrain(JSONObject trainItem) {
        Map<String, Integer> seats = parseSeatsByClass(trainItem.getJSONArray("cars"));
        Train ret = new Train(trainItem.getString("number"), seats);
        return ret;
    }

    private Map<String, Integer> parseSeatsByClass(JSONArray arr) {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            ret.put(obj.getString("typeLoc"), obj.getInt("tariff"));
        }
        return ret;
    }

    private int getStationId(String name) throws PageException, IOException {
        if (stationCache.containsKey(name)) {
            return stationCache.get(name);
        }
        URL url = new URL(String.format(STATION_URL, name));
        String str = getWebClient().downloadString(url, UserAgent.FIREFOX);
        JSONArray arr = new JSONArray(str);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("n").toLowerCase().equals(name.toLowerCase())) {
                Integer id = obj.getInt("c");
                stationCache.put(name, id);
                return id;
            }
        }
        throw new PageException("Station not found");
    }

    private RequestContext getRequestContext(TrainFilter request) throws PageException, IOException {
        URL url = new URL(String.format(SESSION_URL, request.getFrom(), getStationId(request.getFrom()), formatDate(request.getWhen()), request.getTo(), getStationId(request.getTo()), formatDate(request.getWhen())));
        String str = getWebClient().downloadString(url, UserAgent.FIREFOX);
        JSONObject json = new JSONObject(str);
        RequestContext ret = new RequestContext(json.getInt("SESSION_ID"), json.getInt("rid"));
        return ret;
    }

    private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }

    private class RequestContext {
        private final int sessionId;
        private final int rId;

        public RequestContext(int sessionId, int rId) {
            this.sessionId = sessionId;
            this.rId = rId;
        }

        public int getSessionId() {
            return sessionId;
        }

        public int getrId() {
            return rId;
        }
    }
}
