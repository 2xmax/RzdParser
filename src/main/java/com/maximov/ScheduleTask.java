package com.maximov;


import com.maximov.data.ITrainService;
import com.maximov.data.SearchException;
import com.maximov.data.TrainFilter;
import com.maximov.data.TrainSearchResult;
import com.maximov.notification.INotificationService;
import com.maximov.notification.NotificationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public final class ScheduleTask extends TimerTask {
    private final INotificationService notificationService;
    private final ITrainService trainService;
    private final List<TrainFilter> filters;
    private final Log log = LogFactory.getLog(ScheduleTask.class);

    public ScheduleTask(List<TrainFilter> filters) throws IOException {
        this.trainService = new com.maximov.data.providers.http.TrainService();
        this.notificationService = new NotificationService();
        this.filters = filters;
    }

    public static void main(String[] args) throws IOException, ParseException {
        Options opts = Options.parse(args);
        if (opts.isValid) {
            TimerTask instance = new ScheduleTask(opts.filters);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(instance, 0, opts.timeout);
        }
    }

    @Override
    public void run() {
        log.info("===========================================");
        for (TrainFilter filter : filters) {
            try {
                TrainSearchResult trainSearchResult = trainService.find(filter);
                if (!trainSearchResult.hasError()) {
                    if (trainSearchResult.getItems().size() > 0) {
                        notificationService.notifySuccess(filter, trainSearchResult);
                    }
                } else {
                    //notificationService.notifyServiceIsDown();
                }
                //notificationService.notifyServiceIsAvailable();

            } catch (SearchException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
