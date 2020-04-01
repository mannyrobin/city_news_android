package ru.mycity.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Locale;

import ru.mycity.R;
import ru.utils.FormatUtils;

public class DateFormatter
{
    public static final String [] monthsNames = new String[12];
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());
    static
    {
        for (int i = 0; i < 12; i++)
        {
            monthsNames[i] = DateUtils.getMonthString(i, DateUtils.LENGTH_LONG);
        }
    }
    private final String  todayString, yesterdayString;

    public DateFormatter(final Context context)
    {
        Resources res = context.getResources();
        todayString 	 = res.getString(R.string.today);
        yesterdayString  = res.getString(R.string.yesterday);
    }


    public CharSequence formatDateTime(long date)
    {
        if (0 == date)
            return "";

        final long today = System.currentTimeMillis();
        calendar.setTimeInMillis(today);
        final long currentYear = calendar.get(Calendar.YEAR);
        final long currentMonth = calendar.get(Calendar.MONTH);
        final long currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(date);

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        boolean sameYear = (year == currentYear);

        StringBuilder sb = new StringBuilder(23);
        if (sameYear && (currentMonth == month))
        {
            if (currentDay == day)
              sb.append(todayString);
        }
        else
        {
            if ( (currentDay -  day) == 1)
                sb.append(yesterdayString);
        }

        if (0 == sb.length())
        {
            FormatUtils.appendTwoDigitNumber(day, sb);
            sb.append(' ');
            sb.append(monthsNames[month]);

            if (!sameYear)
            {
                sb.append(' ');
                sb.append(year);
            }
        }

        sb.append(',');
        sb.append(' ');
        FormatUtils.zeroPaddingAppendTwoDigitNumber(calendar.get(Calendar.HOUR_OF_DAY), sb);
        sb.append(':');
        FormatUtils.zeroPaddingAppendTwoDigitNumber(calendar.get(Calendar.MINUTE), sb);
        return sb;
    }

    public CharSequence formatTime(long date)
    {
        if (0 == date)
            return "";

        StringBuilder sb = new StringBuilder(5);
        calendar.setTimeInMillis(date);
        FormatUtils.zeroPaddingAppendTwoDigitNumber(calendar.get(Calendar.HOUR_OF_DAY), sb);
        sb.append(':');
        FormatUtils.zeroPaddingAppendTwoDigitNumber(calendar.get(Calendar.MINUTE), sb);
        return sb;
    }

    @SuppressWarnings("deprecation")
    public CharSequence formatDate(long date)
    {
        final long today = System.currentTimeMillis();
        final long julianDayNumber1 = today / ru.utils.DateUtils.MILLIS_PER_DAY;
        final long julianDayNumber2 = date / ru.utils.DateUtils.MILLIS_PER_DAY;

        long days = julianDayNumber1 - julianDayNumber2;

        if (0 == days)
            return todayString;

        if (1 == days)
            return yesterdayString;

        calendar.setTimeInMillis(date);

        StringBuilder sb = new StringBuilder(16);

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);

        FormatUtils.appendTwoDigitNumber(day, sb);
        sb.append(' ');
        sb.append(monthsNames[month]);

        int year = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(today);

        if (year != calendar.get(Calendar.YEAR))
        {
            sb.append(' ');
            sb.append(year);
        }
        return sb;
    }
}
