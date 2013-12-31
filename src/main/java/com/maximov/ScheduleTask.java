package com.maximov;


import com.maximov.data.ITrainService;
import com.maximov.data.TrainFilter;
import com.maximov.data.TrainSearchResult;
import com.maximov.notification.INotificationService;
import com.maximov.notification.NotificationService;
import com.maximov.selenium.pageobjects.PageException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public final class ScheduleTask extends TimerTask {
    private final INotificationService notificationService;
    private final ITrainService trainService;
    private final TrainFilter filter;

    public ScheduleTask(TrainFilter filter) throws IOException {
        this.trainService = new com.maximov.http.TrainService();
        this.notificationService = new NotificationService();
        this.filter = filter;
    }

    public static void main(String[] args) throws IOException, ParseException {
        Options opts = Options.parse(args);
        if (opts.isValid) {
            TimerTask instance = new ScheduleTask(opts.filter);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(instance, 0, opts.timeout);
        }
    }

    @Override
    public void run() {
        try {
            TrainSearchResult trainSearchResult = trainService.find(filter);
            if (!trainSearchResult.hasError()) {
                if (trainSearchResult.getItems().size() > 0) {
                    notificationService.notifySuccess(trainSearchResult);
                }
            } else {
                notificationService.notifyServiceIsDown();
            }
            notificationService.notifyServiceIsAvailable();
        } catch (PageException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

