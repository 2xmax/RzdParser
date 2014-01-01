package com.maximov.data.providers.selenium.pageobjects.results;

import com.maximov.data.SearchException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
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

public class Parser {
    public ParseResult parse(String html) throws SearchException {
        List<ResultsFormItem> ret = new LinkedList<ResultsFormItem>();
        Document htmlDoc = Jsoup.parse(html);

        Elements trainRows = htmlDoc.select("tr.trlist__trlist-row");

        if (trainRows.size() > 0) {
            for (Element trainRow : trainRows) {
                ret.add(parseTrain(trainRow));
            }
            return new ParseResult(ret);
        }

        Elements loading = htmlDoc.select("div#ajaxTrainTable");
        if (loading.size() > 0) {
            return new ParseResult(true);
        }

        return new ParseResult(true);
    }

    private ResultsFormItem parseTrain(Element trainRow) throws SearchException {
        Elements tds = trainRow.getElementsByTag("td");
        String trainId = parseTrainId(tds.get(2));
        Date departs = parseDateTimeCol(tds.get(3));
        Date arrives = parseDateTimeCol(tds.get(7));
        Map<String, Integer> seats = getAvailableSeats(tds.get(8));
        return new ResultsFormItem(trainId, departs, arrives, seats);
    }

    private String parseTrainId(Element trainColumn) {
        Element titleSpan = trainColumn.getElementsByClass("train-num-0").first();
        return titleSpan.text().replace("№", "").trim();
    }

    private Map<String, Integer> getAvailableSeats(Element seatsColumn) {
        Elements titleSpan = seatsColumn.getElementsByTag("tr");
        Map<String, Integer> ret = new HashMap<String, Integer>();
        for (Element seat : titleSpan) {
            String desc = seat.text().trim();
            String type = desc.substring(0, desc.indexOf(' ')).trim();
            desc = desc.replace(type, "").trim();
            int nFree = Integer.parseInt(desc.substring(0, desc.indexOf(' ')));
            if (!ret.containsKey(type)) {
                ret.put(type, nFree);
            }
        }
        return ret;
    }

    private Date parseDateTimeCol(Element dateCol) throws SearchException {
        String time = dateCol.getElementsByClass("trlist__cell-pointdata__time").first().text().trim();
        String date = dateCol.getElementsByClass("trlist__cell-pointdata__date-sub").first().text().replace("|", "").trim();
        //is default city time?
        Boolean isDefaultTime = dateCol.getElementsByClass("trlist__cell-pointdata__time-local").first().text().trim().equals("Время московское");
        if (!isDefaultTime) {
            throw new UnsupportedOperationException();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        try {
            return sdf.parse(date + " " + time);
        } catch (ParseException e) {
            throw new SearchException("Invalid date", e);
        }
    }
}
