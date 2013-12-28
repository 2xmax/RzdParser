package com.maximov.notification;

import com.maximov.data.TrainSearchResult;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */

public interface INotificationService {
    void notifySuccess(TrainSearchResult result);

    void notifyServiceIsAvailable();

    void notifyServiceIsDown();
}
