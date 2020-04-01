package ru.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PhoneUtils
{
    public static String preparePhone(String phone)
    {
        if (null == phone)
            return phone;
        return phone.replaceAll("[^0-9|\\+]", "");
        /*
        StringBuilder sb = new StringBuilder(phone.length());
        final int len  = phone.length();

        for (int i = 0; i < len; i++)
        {
            char c = phone.charAt(i);
            if (Character.isDigit(c) || ('+' == c))
                sb.append(c);
        }
        return sb.toString();
        */
    }

    public static boolean makeCall(Context context, String phone)
    {
        if (null == phone)
            return false;

        if (0 == phone.length())
            return false;

        try
        {
            final String callPhone = preparePhone(phone);
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callPhone));
            context.startActivity(dialIntent);
            return true;
        }
        catch (SecurityException es)
        {
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }
        return false;

    }

}

