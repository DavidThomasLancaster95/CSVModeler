package Calculation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeCalculation {

    static public int lineOfTime930(){
        return 27901;
    }
    static public int lineOfTime1030(){
        return 24301;
    }

    public static String ConvertTimeIntToString(int timeInt) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        date.setHours(17);
        date.setMinutes(15);
        date.setSeconds(0);
        cal.setTime(date);
        cal.add(Calendar.SECOND, -timeInt + 1);

        return formatter.format(cal.getTime());
    }
}
