package ru.mycity.tracker;

import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;

import org.piwik.sdk.Piwik;

import java.net.MalformedURLException;
import java.util.Map;

import ru.mycity.AppData;
import ru.mycity.R;
import ru.mycity._Application;

/**
 * Created by Sergey Rumyantsev on 15.06.2016.
 */
public class TrackerAdapter
{
    public TrackerAdapter(_Application application)
    {
        m_application = application;

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(m_application);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        m_googleTracker = analytics.newTracker(R.xml.global_tracker);
        m_googleTracker.set("&uid", m_application.getDeviceId());
        m_googleTracker.enableAdvertisingIdCollection(true);
        m_googleTracker.enableExceptionReporting(true);
        //m_googleTracker.enableAutoActivityTracking(true);


        //m_googleTracker.enableExceptionReporting(true);
        //m_googleTracker.enableAutoActivityTracking(true);

        //m_googleTracker.enableAdvertisingIdCollection(true);


        String appId = AppData.getAppId();
        if (2 == appId.length() && appId.regionMatches(0, "30", 0, 2)) //Testenburg
        {
            try
            {
                m_piwikTracker = Piwik.getInstance(m_application).newTracker("http://88.212.234.126/piwik/piwik.php", 1);
                m_piwikTracker.setVisitCustomVariable(1, "ApplicationID", AppData.getAppId());
            }
            catch (MalformedURLException e)
            {
                Log.w("Application", "url is malformed", e);
            }
        }
    }

    public void send(Map<String, String> data)
    {
        if (null != m_googleTracker)
        {
            m_googleTracker.send(data);
        }

        if (null != m_piwikTracker)
        {
            String eventType = data.get("&t");
            if (0 == eventType.compareTo("event"))
            {
                if (true == data.containsKey("&ev"))
                {
                    float val = Float.parseFloat(data.get("&ev"));
                    org.piwik.sdk.TrackHelper.track().event(data.get("&ec"), data.get("&ea")).name(data.get("&el")).value(val).with(m_piwikTracker);
                }
                else
                {
                    org.piwik.sdk.TrackHelper.track().event(data.get("&ec"), data.get("&ea")).name(data.get("&el")).with(m_piwikTracker);
                }
            }
            else if (0 == eventType.compareTo("exception"))
            {
                throw new RuntimeException("Unexpected event type- exception!!!");
                //org.piwik.sdk.TrackHelper.track().exception(exception).fatal(fatal);
            }

            //TODO
            //m_piwikTracker.send
            //org.piwik.sdk.TrackHelper.track().event(category, action).name(label).with(application.getPiwikTracker());

            //float val = value;
            //org.piwik.sdk.TrackHelper.track().event(category, action).name(label).value(val).with(application.getPiwikTracker());

//            String appId = AppData.getAppId();
//            if (2 == appId.length() && appId.regionMatches(0, "30", 0, 2)) //Testenburg
//            {
//                org.piwik.sdk.TrackHelper.track().exception(exception).fatal(fatal);
//                //application.getPiwikTracker().track()
//            }

        }
    }

    public void send(Throwable exception, boolean fatal)
    {
        if (null != m_googleTracker)
        {
            HitBuilders.ExceptionBuilder builder = new HitBuilders.ExceptionBuilder().setDescription(TrackerExceptionHelper.createExceptionInformation(exception)).setFatal(fatal);
            m_googleTracker.send(builder.build());
        }

        if (null != m_piwikTracker)
        {
            org.piwik.sdk.TrackHelper.track().exception(exception).fatal(fatal);
        }
    }

    public boolean dispatch()
    {
        if (null != m_piwikTracker)
        {
            return m_piwikTracker.dispatch();
        }

        return true;
    }

    com.google.android.gms.analytics.Tracker m_googleTracker;
    org.piwik.sdk.Tracker                    m_piwikTracker;

    _Application                             m_application;
}
