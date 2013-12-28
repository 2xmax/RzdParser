package com.maximov.data;

import java.util.Map;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public final class Train {
    private final String trainId;
    private final Map<String, Integer> seats;

    public Train(String trainId, Map<String, Integer> seatsByClass) {
        this.trainId = trainId;
        this.seats = seatsByClass;
    }

    public int getSeatsByClass(String className) {
        if (seats.containsKey(className)) {
            return seats.get(className);
        }
        return 0;
    }

    public Map<String, Integer> getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(trainId + " ");
        for (Map.Entry<String, Integer> entry : seats.entrySet()) {
            sb.append(entry.getKey() + ":" + entry.getValue() + " ");
        }
        return sb.toString();
    }
}
