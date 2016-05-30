package io.base.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gaochao on 2015/10/22.
 */
public class TimeUtils {
    private TimeUtils(){}

    public interface OnNotifyDateListener{
        void onNotifyDate(int year, int month, int dayOfMonth);
    }

    /**
     * 选择日期，并显示选择结果
     * */
    /*public static void selectDate(final CiweiDate date, Context context, final OnNotifyDateListener listener){
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                ++month;
                date.year = year;
                date.month = month;
                date.day = day;
                if(listener != null){
                    listener.onNotifyDate(year, month, day);
                }
            }
        }, date.year, date.month - 1, date.day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }*/

    //格式化生日
    public static String formatBirthday(int year, int month, int day){
        return String.format("%04d.%02d.%02d", year, month, day);
    }

    //格式化生日
    /*public static String formatBirthday(CiweiDate date){
        return formatBirthday(date.year, date.month, date.day);
    }*/

    //String到CiweiDate的转换
    /*public static CiweiDate getBirthdayDate(String birthday){
        String[] splits = null;
        if(birthday.indexOf(".") > -1) {
            splits = birthday.split("\\.");
        }else{
            splits = birthday.split("-");
        }
        return new CiweiDate(Integer.valueOf(splits[0]), Integer.valueOf(splits[1]), Integer.valueOf(splits[2]));
    }*/

    /**
     * long到CiweiDate的转换
     * @param birthday long表示的日期，单位毫秒
     * */
    /*public static CiweiDate getBirthdayDate(long birthday){
        Date date = new Date(birthday * 1000);
        return new CiweiDate(date.getYear(), date.getMonth(), date.getDay());
    }*/

    /**
     * 按照如下格式格式化时间
     *
     * @param time 以毫秒表示的时间
     * */
    public static String formatTime(long time){
        long currentTime = System.currentTimeMillis();

        long diff = currentTime - time;
        if(diff < MINUTE) {//1分钟内
            return "刚刚";
        }

        if(diff < HOUR){//1小时内
            return diff/MINUTE + "分钟前";
        }

        if(diff < DAY) {//一天内
            return diff/HOUR + "小时前";
        }

        if(diff < MONTH){
            return diff/DAY + "天前";
        }

        if(diff < YEAR){
            return diff/MONTH + "月前";
        }

        return diff/YEAR + "年前";
    }
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long MONTH = 31 * DAY;
    private static final long YEAR = 12 * MONTH;

    /**
     * @return if date1 <= date2, true; or false
     * */
    public static boolean lessThan(String date1, String date2){
        String[] splits1 = date1.split(getSeparator(date1));
        String[] splits2 = date2.split(getSeparator(date2));
        int date1Year = Integer.valueOf(splits1[0]), date2Year = Integer.valueOf(splits2[0]);
        if(date1Year < date2Year){
            return true;
        }

        if(date1Year == date2Year){
            int date1Month = Integer.valueOf(splits1[1]), date2Month = Integer.valueOf(splits2[1]);
            if(date1Month <= date2Month){
                return true;
            }
        }

        return false;
    }

    public static String plusYear(String date, int yearOffset){
        String[] splits = date.split(getSeparator(date));
        return String.valueOf(Integer.valueOf(splits[0]) + yearOffset) + "." + splits[1];
    }

    /**
     * year.month -> (year + yearoffset).month
     * @return if null, 表示现在，至今;
     * */
    public static String plusYearAndSetMonth(String date, int yearOffset, String month){
        String[] splits = date.split(getSeparator(date));
        int year = Integer.valueOf(splits[0]) + yearOffset;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if(year > currentYear){
            return SPECIAL_YEAR;
        }else{
            return year + "." + month;
        }
    }

    private static String getSeparator(String str){
        return str.indexOf('.') != -1 ? SEPARATOR_DOT : "-";
    }

    public static String getUtilNow(String separator){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + separator + calendar.get(Calendar.MONTH);
    }
    public static String getUtilNow(){
        return getUtilNow(".");
    }

    private static final String SEPARATOR_DOT = "\\.";

    public static String getUtilNowLiteral(){
        return SPECIAL_YEAR;
    }
    private static final String SPECIAL_YEAR = "至今";

    /*
    * @param createtime 传入的时间格式必须类似于2012-8-21 17:53:20这样的格式
    * **/
    public static String getInterval(String createtime) {
        String interval = null;

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date d1 = (Date) sd.parse(createtime, pos);

        //用现在距离1970年的时间间隔new Date().getTime()减去以前的时间距离1970年的时间间隔d1.getTime()得出的就是以前的时间与现在时间的时间间隔
        long time = new Date().getTime() - d1.getTime();// 得出的时间间隔是毫秒

        if(time/1000 < 10 && time/1000 >= 0) {
            //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
            interval ="刚刚";

        }else if(time/1000 < 60 && time/1000 > 0) {
            //如果时间间隔小于60秒则显示多少秒前
            int se = (int) ((time%60000)/1000);
            interval = se + "秒前";

        } else if(time/60000 < 60 && time/60000 > 0) {
            //如果时间间隔小于60分钟则显示多少分钟前
            int m = (int) ((time%3600000)/60000);//得出的时间间隔的单位是分钟
            interval = m + "分钟前";

        } else if(time/3600000 < 24 && time/3600000 >= 0) {
            //如果时间间隔小于24小时则显示多少小时前
            int h = (int) (time/3600000);//得出的时间间隔的单位是小时
            interval = h + "小时前";

        } else {
            //大于24小时，则显示正常的时间，但是不显示秒
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//            ParsePosition pos2 = new ParsePosition(0);
//            Date d2 = (Date) sdf.parse(createtime, pos2);
//
//            interval = sdf.format(d2);
            int day = (int) (time/3600000)/24;//得出的时间间隔的单位是小时
            interval = day + "天前";
        }
        return interval;
    }

}
