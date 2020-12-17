package com.maximov.data.providers.http;

import com.maximov.data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
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

    private final String SESSION_URL = "https://pass.rzd.ru/timetable/public/ru?STRUCTURE_ID=735&layer_id=5371&dir=0&tfl=3&checkSeats=1&st0=%s&code0=%d&dt0=%s&st1=%s&code1=%d&dt1=%s";
    private final String TICKETS_URL = SESSION_URL + "&rid=%d";
    private final String STATION_URL = "https://pass.rzd.ru/suggester?&lang=ru&lat=0&compactMode=y&stationNamePart=%s";
    private final Log log = LogFactory.getLog(TrainService.class);
    private final Map<String, Integer> stationCache = new HashMap<String, Integer>();
    private IWebClient webClient;

    private IWebClient getWebClient() {
        if (webClient == null) {
            webClient = new WebClient();
        }
        return webClient;
    }

    @Override
    public TrainSearchResult find(TrainFilter filter) throws SearchException, IOException {
        JSONObject obj = null;
        int maxRetry = 10;
        long waitTimeout = 1000;
        for (int i = 0; i < maxRetry; i++) {
            try {
                RequestContext params = getRequestContext(filter);
                if (obj != null && "OK".equals(obj.getString("result"))) {
                    return parse(obj, filter);
                } else {
                    Thread.sleep(waitTimeout);
                    obj = getJson(filter, params);
                }
            } catch (JSONException je) {
                try {
                    // captcha workaround
                    // todo: use backoff
                    webClient = null;
                    Thread.sleep(60000);
                } catch (Exception ex) {
                }
                je.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new TrainSearchResult(true);
    }

    private JSONObject getJson(TrainFilter request, RequestContext params) throws IOException, SearchException {
        URL url = new URL(String.format(TICKETS_URL, formatString(request.getFrom()), getStationId(request.getFrom()), formatDate(request.getWhen()), formatString(request.getTo()), getStationId(request.getTo()), formatDate(request.getWhen()), params.getrId()));
        String searchResult = getWebClient().downloadString(url, UserAgent.DEFAULT);
        JSONObject obj = new JSONObject(searchResult);
        return obj;
    }

    private TrainSearchResult parse(JSONObject obj, TrainFilter filter) {
        List<Train> trains = new ArrayList<Train>();
        JSONArray tr = obj.getJSONArray("tp");
        for (int i = 0; i < tr.length(); i++) {
            JSONObject item = tr.getJSONObject(i);
            JSONArray list = item.getJSONArray("list");
            for (int j = 0; j < list.length(); j++) {
                JSONObject trainItem = list.getJSONObject(j);
                Map<String, Integer> allSeats = parseSeatsByClass(trainItem.getJSONArray("cars"));
                String trainCode = trainItem.getString("number");
                if (!filter.isFilteredByTrainCode() || trainCode.toLowerCase().contains(filter.getTrainCode().toLowerCase())) {
                    Map<String, Integer> filteredSeats = filterMap(allSeats, filter.getSeatTypes());
                    if (filteredSeats.size() > 0) {
                        trains.add(new Train(trainCode, filteredSeats));
                    }
                }
            }
            log.info(String.format("[%s]: %d non-filtered, %d filtered trains", filter.toString(), list.length(), trains.size()));
        }
        return new TrainSearchResult(trains);
    }

    private Map<String, Integer> parseSeatsByClass(JSONArray arr) {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            ret.put(obj.getString("typeLoc"), obj.getInt("tariff"));
        }
        return ret;
    }

    private int getStationId(String name) throws SearchException, IOException {
        if (stationCache.containsKey(name)) {
            return stationCache.get(name);
        }
        URL url = new URL(String.format(STATION_URL, name.replace(" ", " ")));
        String str = getWebClient().downloadString(url, UserAgent.DEFAULT);
        JSONArray arr = new JSONArray(str);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("n").toLowerCase().equals(name.toLowerCase())) {
                Integer id = obj.getInt("c");
                stationCache.put(name, id);
                return id;
            }
        }
        throw new SearchException("Station not found");
    }

    private RequestContext getRequestContext(TrainFilter request) throws SearchException, IOException {
        URL url = new URL(String.format(SESSION_URL, formatString(request.getFrom()), getStationId(request.getFrom()), formatDate(request.getWhen()), formatString(request.getTo()), getStationId(request.getTo()), formatDate(request.getWhen())));
        String str = getWebClient().downloadString(url, UserAgent.FIREFOX);
        JSONObject json = new JSONObject(str);
        return new RequestContext(json.getLong("rid"));
    }

    private String formatString(String str) {
        return str.replace(" ", "%20");
    }

    private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }

    private Map<String, Integer> filterMap(Map<String, Integer> target, List<String> filter) {
        if (filter.size() == 0) {
            return target;
        }
        Map<String, Integer> filtered = new HashMap<String, Integer>();
        for (String item : filter) {
            if (target.containsKey(item) && target.get(item) > 0) {
                filtered.put(item, target.get(item));
            }
        }
        return filtered;
    }

    private class RequestContext {
        private final long rId;

        // rID is sort of session ID, required parameter to pass
        public RequestContext(long rId) {
            this.rId = rId;
        }

        public long getrId() {
            return rId;
        }
    }
}
