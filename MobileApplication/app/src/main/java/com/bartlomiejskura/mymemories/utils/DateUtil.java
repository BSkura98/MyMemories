package com.bartlomiejskura.mymemories.utils;

public class DateUtil {
    public static String formatDateTime(String dateTime){
        return dateTime.substring(8, 10) + "-" + dateTime.substring(5, 7) + "-" + dateTime.substring(0, 4)+" "+dateTime.substring(11, 16);
    }

    public static String formatDate(String dateTime){
        return dateTime.substring(8, 10) + "-" + dateTime.substring(5, 7) + "-" + dateTime.substring(0, 4);
    }

    public static String formatTime(String dateTime){
        return dateTime.substring(11, 16);
    }
}
