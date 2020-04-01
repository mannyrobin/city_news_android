package ru.mycity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;

import java.io.File;

import ru.mycity.data.PushData;
import ru.mycity.tasks.ChangeCacheDestinationTask;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tasks.StartApplicationTask;
import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.PermissionsUtils;


public abstract class StartActivity extends _AppCompatActivity implements IResultCallback
{
    protected boolean checkPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //app.getImageLoader().stop();

            SharedPreferences prefs = Config.getSharedPreferences(this);
            //int val = prefs.getInt(Config.cp, 0);
            //if (0 == val)
            {
                //Config.putInt(prefs, Config.cp, 1);
                boolean isUseSDCardForCache = prefs.getBoolean(getString(R.string.use_sd_card_for_cache), true);
                //isUseSDCardForCache = true;

                if (isUseSDCardForCache && false == PermissionsUtils.checkPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, IPermissionCodes.PERMISSION_WRITE_EXTERNAL_STORAGE_RC);
                    //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ACQUIRE_REQUEST_CODE);
                    return false;
                }
            }
        }
        return true;
    }

    private void moveImageLoaderCache()
    {
        _Application application = (_Application) getApplication();
        if (null != application)
        {
            File oldImageDir = application.getCacheDirectory(false);
            application.setUseSDCardForCache(true);
            File newImageDir = application.getImageCacheDirectory();
            //TODO

            if (!oldImageDir.getAbsolutePath().equals(newImageDir.getAbsolutePath()))
            {
                //new ChangeCacheDestinationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldImageDir, newImageDir);

                _Application app =  (_Application) getApplicationContext();
                app.getAsyncTaskExecutor().execute(new ChangeCacheDestinationTask(), oldImageDir, newImageDir);

                application.restartImageLoader();
            }
        }
    }

    protected void readData()
    {
        //SharedPreferences prefs = Config.getSharedPreferences(this);
        /*
        if (getResources().getInteger(R.integer.intro_count) > 0 && !prefs.getBoolean(Config.info_shown, false))
        {
        }
        else
        */
        {
            //new StartApplicationTask(app, 0, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            _Application app =  (_Application) getApplication();
            PushData pushData = PushData.extract(app, getIntent());
            //if (null != pushData)
            //    Log.d(TAG, "#x# " + pushData.toString());


            //Log.d("#x#", "#x# StartApplicationTask");
            app.getAsyncTaskExecutor().execute(new StartApplicationTask(app, pushData, 0, this));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            switch (requestCode)
            {
                //case 512 + PrefsFragment.PERMISSION_ACQUIRE_PREFERENCES:
                //case PrefsFragment.PERMISSION_ACQUIRE_PREFERENCES:
                //{
                //    final PrefsFragment prefsFragment = (PrefsFragment) getSupportFragmentManager()
                //            .findFragmentByTag(ItemType.Settings.name());
                //    if (null != prefsFragment)
                //        prefsFragment.handlePermissions(permissions, grantResults);
                //  else
                //        handlePermissions(permissions, grantResults);
                //}
                //break;
                case IPermissionCodes.PERMISSION_WRITE_EXTERNAL_STORAGE_RC:
                {
                    if (PermissionsUtils.isAllPermissionsGranted(permissions, grantResults))
                        moveImageLoaderCache();
                    readData();
                }
                break;
            }
        }
    }


    @Override
    public void onFinished(int rc, Result result)
    {
        try
        {
            if (!isFinishing())
            {
                onDataRead();
            }
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(getApplication()), e, false);
            finish();
        }
    }

    abstract protected void onDataRead();

    /*
    public static void dumpIntent(Intent i)
    {
        Bundle bundle = i.getExtras();
        if (bundle != null)
        {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e(TAG, "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e(TAG,"[" + key + "=" + bundle.get(key)+"]");
            }
            Log.e(TAG,"Dumping Intent end");
        }
    }*/


    @Override
    public void onFailed(int rc, String description)
    {
        //showErrorToast(R.string.start_error, true);
        if (!isFinishing())
        {
            showErrorDialog(getText(R.string.start_error), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    finish();
                }
            });
        }
    }

    @Override
    public void onFailed(int rc, Throwable error)
    {
        onFailed(rc, (null != error) ? error.getMessage() : null);
    }
}
