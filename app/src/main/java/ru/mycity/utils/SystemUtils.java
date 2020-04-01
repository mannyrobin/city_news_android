package ru.mycity.utils;


import android.content.Context;
import android.content.res.Resources;

import ru.utils.Density;

public class SystemUtils
{
    public static int getStatusBarHeight(final Context context)
    {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
        {
            return resources.getDimensionPixelSize(resourceId);
        }
        else
        {
            return (int) Density.getIntValue(resources, 25);
        }
    }

}
