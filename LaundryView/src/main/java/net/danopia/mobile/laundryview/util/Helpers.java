package net.danopia.mobile.laundryview.util;

import java.util.StringTokenizer;

/**
 * Created by daniel on 8/29/13.
 */
public class Helpers {
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    protected static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    protected static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String titleCase(String s) {
        StringBuilder rv = new StringBuilder(s.length());
        StringTokenizer strtok = new StringTokenizer(s);

        while (strtok.hasMoreTokens()) {
            String word = strtok.nextToken();
            String firstLetter = word.substring(0,1);
            String restOfWord = word.substring(1);

            if (rv.length() > 0) rv.append(" ");
            rv.append(firstLetter.toUpperCase());
            rv.append(restOfWord.toLowerCase());
        }

        return rv.toString();
    }
}
