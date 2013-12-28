package com.maximov.data;

import java.util.ArrayList;
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
    private final String from;
    private final String to;
    private final Date when;
    private final List<String> seatTypes;

    public TrainFilter(String departureStation, String arrivalStation, Date when) {
        this.from = departureStation;
        this.to = arrivalStation;
        this.when = when;
        this.seatTypes = new ArrayList<String>();
    }

    public TrainFilter(String departureStation, String arrivalStation, Date when, String seatType) {
        this.from = departureStation;
        this.to = arrivalStation;
        this.when = when;
        this.seatTypes = new ArrayList<String>();
        this.seatTypes.add(seatType);
    }

    public TrainFilter(String departureStation, String arrivalStation, Date when, List<String> seatTypes) {
        this.from = departureStation;
        this.to = arrivalStation;
        this.when = when;

        if (seatTypes == null) {
            throw new IllegalArgumentException("seatTypes");
        }
        this.seatTypes = seatTypes;
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
}
