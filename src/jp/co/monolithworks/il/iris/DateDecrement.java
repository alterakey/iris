package jp.co.monolithworks.il.iris;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class DateDecrement {
    
    public static String setDate(){
        String nowDate;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        nowDate = sdf.format(date);
        return nowDate;
    }

}
