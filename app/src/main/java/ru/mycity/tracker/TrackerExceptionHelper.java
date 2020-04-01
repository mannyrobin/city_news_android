package ru.mycity.tracker;

import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.mycity.AppData;
import ru.mycity.BuildConfig;

//import com.crashlytics.android.Crashlytics;

/**
 * Created by Serge Rumyantsev on 30.03.2016.
 */
public class TrackerExceptionHelper
{
    private final static String TAG = "MyCity";
    private final static char LINE_SEPARATOR = '\n';


    static String createExceptionInformation(Throwable exception)
    {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ EXCEPTION ************\n\n");
        errorReport.append(exception.toString());
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());
        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        return errorReport.toString();
    }


    private final static boolean logCatLogging;
    static
    {
        boolean _logCatLogging = BuildConfig.DEBUG;
        if (!_logCatLogging)
        {
            String id = AppData.getAppId();
            if ( (2 == id.length()) && "30".regionMatches(0, id, 0,  2)) // Testeburg
            {
                _logCatLogging = true;
            }
            else
            {
                if ( (2 == id.length()) && "44".regionMatches(0, id, 0,  2)) // Tomsk, temporary
                    _logCatLogging = true;
            }
        }
        logCatLogging = _logCatLogging;
    }

    public static void sendExceptionStatistics(TrackerAdapter tracker, Throwable exception, boolean fatal)
    {
        if (logCatLogging)
        {
            Log.e(TAG, "Exception, fatal = " + fatal, exception);
        }

        try
        {
            //Crashlytics.logException(exception);

            if (null != tracker)
                tracker.send(exception, fatal);
        }
        catch (Throwable e)
        {
            Log.e(TAG, "Exception, fatal = " + fatal, exception);
        }

    }
}
