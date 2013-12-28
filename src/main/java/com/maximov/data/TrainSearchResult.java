package com.maximov.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class TrainSearchResult {
    private final Boolean isError;
    private final List<Train> items;

    public TrainSearchResult(boolean isError) {
        this.isError = isError;
        this.items = new LinkedList<Train>();
    }

    public TrainSearchResult(List<Train> items) {
        this.items = items;
        this.isError = false;
    }

    public boolean hasError() {
        return isError;
    }

    public List<Train> getItems() {
        return items;
    }
}
