package ru.mycity.tracker;

import android.content.Context;
import android.view.View;

import ru.mycity._Application;

/**
 * Created by Serge Rumyantsev on 30.03.2016.
 */
public class TrackerHelper
{
    public static TrackerAdapter getTracker(Context context)
    {
        if (null == context)
        {
            return null;
        }
        _Application application = (_Application) context.getApplicationContext();
        return (null != application) ? application.getTracker() : null;
    }

    public static TrackerAdapter getTracker(View view)
    {
        return getTracker(view.getContext());
    }

    public static TrackerAdapter getTracker(android.app.Application application)
    {
        return ((_Application) application).getTracker();
    }
}

