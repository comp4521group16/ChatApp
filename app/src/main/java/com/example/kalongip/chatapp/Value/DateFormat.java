package com.example.kalongip.chatapp.Value;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class to get the Date and Time in a correct format to be shown
 */
public class DateFormat {
    public static String getTime (Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ");
        return sdf.format(date);
    }
    public static String getDate (Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        return sdf.format(date);
    }
}
