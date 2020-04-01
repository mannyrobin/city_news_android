package ru.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionsUtils
{
    public static boolean isAllPermissionsGranted(String[] permissions, int[] grantResults)
    {
        final int len;
        if ((len = permissions.length) == grantResults.length)
        {
            int s = 0;
            for (int i = 0; i < len; i++)
            {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    s++;
            }
            return (s == len);
        }
        return false;
    }

    public static boolean checkPermission(Context context, String permission)
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, permission);
        return true;
    }
}


