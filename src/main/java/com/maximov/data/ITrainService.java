package com.maximov.data;

import java.io.IOException;

/**
 * Maxim Maximov, 2013
 * 2xmax@mail.ru
 * MSc, 2nd year
 * St Petersburg State University
 * Physics Faculty
 * Department of Computational Physics
 */
public interface ITrainService {
    TrainSearchResult find(TrainFilter request) throws SearchException, IOException;
}
