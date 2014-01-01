package com.maximov;

import com.maximov.data.TrainFilter;
import com.maximov.data.providers.selenium.pageobjects.results.SeatClasses;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class Options {
    public boolean isValid;
    public long timeout = 1000 * 60;
    public List<TrainFilter> filters;

    private static List<String> readLines(String file) throws IOException {
        List<String> ret = new ArrayList<String>();
        InputStream in = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        while (line != null) {
            ret.add(line);
            line = br.readLine();
        }
        in.close();
        return ret;
    }

    public static Options parse(String[] args) throws ParseException {
        Options options = new Options();
        String from = null;
        String to = null;
        Date at = null;
        String by = null;
        String config = null;
        List<String> classes = new ArrayList<String>();
        classes.add(SeatClasses.Platzkart);
        options.filters = new ArrayList<TrainFilter>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-from")) {
                from = args[i + 1];
            }
            if (args[i].equals("-to")) {
                to = args[i + 1];
            }
            if (args[i].equals("-at")) {
                at = new SimpleDateFormat("dd.MM.yyyy").parse(args[i + 1]);
            }
            if (args[i].equals("-by")) {
                by = args[i + 1];
            }
            if (args[i].equals("-config")) {
                config = args[i + 1];
            }
        }

        if (config != null) {
            try {
                List<String> lines = readLines(config);
                int nFilters = lines.size() / 4;
                for (int i = 0; i < nFilters; i++) {
                    from = lines.get(i * 4);
                    to = lines.get(i * 4 + 1);
                    at = new SimpleDateFormat("dd.MM.yyyy").parse(lines.get(i * 4 + 2));
                    by = lines.get(i * 4 + 3);
                    options.filters.add(new TrainFilter(from, to, at, classes, by));
                }
            } catch (IOException e) {
                System.out.println("Unable to read file " + config);
            }
        } else {
            if (from != null && to != null && at != null) {
                options.filters.add(new TrainFilter(from, to, at, classes, by));
            }
        }

        if (options.filters.size() == 0) {
            printUsage();
            options.isValid = false;
        } else {
            options.isValid = true;
        }

        return options;
    }

    public static void printUsage() {
        System.out.println("Wrong argument provided.");
        System.out.println("Usage examples:");
        System.out.println("\t1.-from САНКТ-ПЕТЕРБУРГ -to \"NEW YORK CITY\" -at 30.12.2013");
        System.out.println("\t2.-config request.config");
    }


}
