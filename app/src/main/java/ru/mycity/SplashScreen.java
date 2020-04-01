
package ru.mycity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.analytics.HitBuilders;

import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerAdapter;


public class SplashScreen extends StartActivity
{
    private final static String TAG = "SplashScreen";
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        startTime = System.currentTimeMillis();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //dumpIntent(getIntent());
        start();
        /*
        findViewById(android.R.id.icon).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                start();
            }
        },1);
        */
    }

     void start()
     {
         if (checkPermissions())
             readData();
     }


    protected void onDataRead()
    {
        /*
        Bundle extras = getIntent().getExtras();
        Intent intent = new Intent(this, MainActivity.class);
        if (null != extras)
            intent.putExtras(extras);
        startActivity(intent);
        */
        try
        {
            _Application application = (_Application) getApplication();
            TrackerAdapter tracker = application.getTracker();
            if (null != tracker)
            {
                tracker.send(new HitBuilders.EventBuilder().setCategory(ITrackerEvents.CATEGORY_APPLICATION).setAction(ITrackerEvents.ACTION_START).setValue(System.currentTimeMillis() - startTime).build());
            }
        }
        catch (Throwable e)
        {
        }
        setResult(RESULT_OK);
        finish();
    }
}