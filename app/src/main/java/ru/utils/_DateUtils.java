package ru.utils;

public class _DateUtils
{
    public static long truncateTime(long d)
    {
        long msPortion = d % android.text.format.DateUtils.DAY_IN_MILLIS;
        return d - msPortion;
    }

    public static long getTruncatedCurrentTime()
    {
        return truncateTime(System.currentTimeMillis());
    }
}



