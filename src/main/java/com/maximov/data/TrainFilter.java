package com.maximov.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class TrainFilter {
    private final String trainCode;
    private final String from;
    private final String to;
    private final Date when;
    private final List<String> seatTypes;

    public TrainFilter(String departureStation, String arrivalStation, Date when, List<String> seatTypes, String trainCode) {
        this.from = departureStation;
        this.to = arrivalStation;
        this.when = when;

        if (seatTypes == null) {
            throw new IllegalArgumentException("seatTypes");
        }
        this.seatTypes = seatTypes;
        this.trainCode = trainCode;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Date getWhen() {
        return when;
    }

    public List<String> getSeatTypes() {
        return seatTypes;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public boolean isFilteredByTrainCode() {
        return trainCode != null && !trainCode.equals("");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (trainCode != null && !trainCode.equals("")) {
            sb.append(trainCode + " ");
        }
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        sb.append(df.format(when) + " ");
        sb.append(from + " ");
        sb.append(to);
        return sb.toString();
    }
}
