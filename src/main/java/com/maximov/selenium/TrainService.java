package com.maximov.selenium;

import com.maximov.data.ITrainService;
import com.maximov.data.Train;
import com.maximov.data.TrainFilter;
import com.maximov.data.TrainSearchResult;
import com.maximov.selenium.pageobjects.HomePage;
import com.maximov.selenium.pageobjects.ResultsPage;
import com.maximov.selenium.pageobjects.results.ResultsFormItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public class TrainService implements ITrainService {
    private final Log log = LogFactory.getLog(TrainService.class);
    private WebDriver webDriver;

    @Override
    public TrainSearchResult find(TrainFilter request) throws IOException {
        log.debug("search request");
        WebDriver driver = getWebDriver();
        HomePage home = new HomePage(driver);
        try {
            ResultsPage results = home.fillSearchForm(request.getFrom(), request.getTo(), request.getWhen());
            List<ResultsFormItem> result = results.parse();
            List<Train> trains = new ArrayList<Train>();
            for (ResultsFormItem r : result) {
                Map<String, Integer> filteredSeats = filterMap(r.getAvailableSeats(), request.getSeatTypes());
                if (filteredSeats.size() > 0) {
                    trains.add(new Train(r.getTrainId(), filteredSeats));
                }
            }
            log.info(result.size() + " non-filtered, " + trains.size() + " filtered train results has been found");
            return new TrainSearchResult(trains);
        } catch (Exception ex) {
            return new TrainSearchResult(true);
        }
    }

    private WebDriver getWebDriver() throws IOException {
        if (webDriver == null) {
            webDriver = WebDriverFactory.create();
        }
        return webDriver;
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
}
