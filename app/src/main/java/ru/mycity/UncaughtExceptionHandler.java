package ru.mycity;

import android.app.Activity;

import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;

/**
 * Created by Serge Rumyantsev on 30.03.2016.
 */
public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler
{
    private final Activity m_context;

    public UncaughtExceptionHandler(Activity context) 
    {
        m_context = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) 
    {
        TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(m_context), exception, true);

        //android.os.Process.killProcess(android.os.Process.myPid());
        //System.exit(10);
    }

}
