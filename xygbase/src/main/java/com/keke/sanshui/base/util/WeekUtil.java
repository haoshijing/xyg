package com.keke.sanshui.base.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class WeekUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final long WEEK_MILL =  7 *3600*24*1000;
    public static int getCurrentWeek(){
        return getCurrentWeek("");
    }


    public static int getCurrentWeek(String dateStr){
        Long firstTimeStamp = 1515340800000L;
        long endTimestamp = System.currentTimeMillis();
        if(StringUtils.isNotEmpty(dateStr)){
            try {
                Date date = format.parse(dateStr);
                Long diffWeek = (date.getTime() - firstTimeStamp) / WEEK_MILL;
                return  diffWeek.intValue() + 1;
            }catch (Exception e){
                return 0;
            }
        }else{
            Long diffWeek =  (endTimestamp - firstTimeStamp) /WEEK_MILL;
            return diffWeek.intValue() + 1;
        }
    }

    public static long getWeekStartTimestamp(int week){
        DateTime dateTime = new DateTime(2018,1,8,0,0,0);
        return dateTime.plusWeeks(week-1).getMillis();
    }
    public static long getWeekEndTimestamp(int week){
        DateTime dateTime = new DateTime(2017,1,14,23,59,59);
        return dateTime.plusWeeks(week-1).getMillis();
    }
    public static long getWeekStartTimestamp(){
        return getWeekStartTimestamp(getCurrentWeek());
    }

    public static long getWeekEndTimestamp(){
        return   getWeekEndTimestamp(getCurrentWeek());
    }

    public static void main(String[] args) {
        System.out.println(getWeekStartTimestamp());
    }

}
