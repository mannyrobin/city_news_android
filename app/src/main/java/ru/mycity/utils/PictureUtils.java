package ru.mycity.utils;


import android.content.Context;

import ru.mycity.AppData;

public class PictureUtils
{
    private final static int [] supportedValues =
            {24, 36, 48, 72, 96, 100, 144, 150, 192, 200, 300, 320, 400, 480, 720, 800, 1080, 1280, 1440, 1920, 2560};

    // 100dp

    //https://pixplicity.com/dp-px-converter/

    public final static int getPictureValue(Context context, int sizeInPx)
    {
        for (int v : supportedValues)
        {
            if (v >= sizeInPx)
                return v;
        }
        return supportedValues[supportedValues.length -1];
    }

    public static String generatePrefix(int size)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(size).append('x').append(size);
        return sb.toString();
    }

    public static String prepareUrl(String url, String prefix)
    {
        if (null == url)
            return null;

        final int urlLen = url.length();
        if (0 == urlLen)
            return null;

        final boolean relative =  url.charAt(0) == '/';

        int size = prefix.length() + urlLen + 1;
        if (relative)
            size += AppData.BASE_URL.length();
        final StringBuilder sb = new StringBuilder(size);

        if (relative)
            sb.append(AppData.BASE_URL);

        int index = url.lastIndexOf('/');
        if (index > 0)
        {
            sb.append(url, 0, index);
            sb.append('/');
            sb.append(prefix);
            sb.append(url, index, urlLen);
        }
        return sb.toString();
        //http://mytishchiapp.ru/files/news/200x200/ac71a8df.jpg
        //http://mytishchiapp.ru/files/news/200x200/ac71a8df.jpg
    }
}


