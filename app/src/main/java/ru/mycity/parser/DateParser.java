package ru.mycity.parser;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateParser
{
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static Date parse(String date)
    {
        if (null != date)
        {
            try
            {
                return formatter.parse(date);
            }
            catch (ParseException e)
            {
            }
        }
        return null;
    } 
}