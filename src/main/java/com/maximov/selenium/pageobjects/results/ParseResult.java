package com.maximov.selenium.pageobjects.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class ParseResult {
    private final boolean isInProgress;
    private final List<ResultsFormItem> trains;

    public ParseResult(List<ResultsFormItem> trains) {
        this.trains = trains;
        this.isInProgress = false;
    }

    public ParseResult(Boolean isInProgress) {
        this.trains = new ArrayList<ResultsFormItem>();
        this.isInProgress = isInProgress;
    }

    public List<ResultsFormItem> getTrains() {
        return trains;
    }

    public boolean isInProgress() {
        return isInProgress;
    }
}
