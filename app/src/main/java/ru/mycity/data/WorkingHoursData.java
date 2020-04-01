package ru.mycity.data;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class WorkingHoursData
{
    final static long TIME_OUT = android.text.format.DateUtils.MINUTE_IN_MILLIS / 2;
    //private final static Matcher matcher = Pattern.compile("([01]?[0-9]|2[0-3]):([0-5][0-9])\\u0020*\\-\\u0020*([01]?[0-9]|2[0-3]):([0-5][0-9])").matcher("");

    //private final static Matcher matcher = Pattern.compile("([01]?[0-9]|2[0-3]):([0-5][0-9])[^0-9]+([01]?[0-9]|2[0-3]):([0-5][0-9])").matcher("");
    private final static Matcher matcher = Pattern.compile("([01]?[0-9]|2[0-3]):([0-5][0-9])[^0-9]+([01]?[0-9]|2[0-4]):([0-5][0-9])").matcher("");
    long lastCheckedTime;
    boolean lastState;
    WHLimit [] limits;

    private WHLimit getLimit(int index, String data)
    {
        WHLimit limit;
        if (null != limits)
        {
            if ((limit = limits[index]) != null)
               return limit;
        }
        else
        {
            limits = new WHLimit[7];
        }
        limit = new WHLimit();
        int l = 0;
        int h = 0; //Integer.MAX_VALUE;
        //work_monday":"9:00-18:00",
        // work_monday":"c 7:00 до 20:00",
        matcher.reset(data);
        if (matcher.find())
        {
            try
            {
                int h1 = Integer.parseInt(matcher.group(1));
                int m1 = Integer.parseInt(matcher.group(2));
                int h2 = Integer.parseInt(matcher.group(3));
                int m2 = Integer.parseInt(matcher.group(4));
                l = 60 * h1 + m1;
                h = 60 * h2 + m2;
            }
            catch (Throwable e)
            {
            }
        }
        limit.low = l;
        limit.hi  = h;
        limits[index] = limit;
        return limit;
    }


    boolean isWorkingHours(Calendar calendar, String data, int index)
    {
        if (null == data)
            return false;
        final int len = data.length();
        if (0 == len)
            return false;

        WHLimit limit = getLimit(index, data);
        if (null == limit)
            return true;
        int current = 60 * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);

        //return (current >= limit.low) && ((limit.hi > 0) ? current <= limit.hi: true);

        //if (current < limit.low)
        //    return false;
        if (limit.low < limit.hi)
            return (current >= limit.low) && (current <= limit.hi);

        if (current >= limit.low)
            return true;

        return  current <= limit.hi;
    }
}