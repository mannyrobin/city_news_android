package ru.mycity;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Build;

import com.google.zxing.client.android.common.executor.AsyncTaskExecInterface;
import com.google.zxing.client.android.common.executor.AsyncTaskExecManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.io.File;

import ru.mycity.data.RootData;
import ru.mycity.database.DBHelper;
import ru.mycity.device.DeviceId;
import ru.mycity.tracker.TrackerAdapter;
import ru.mycity.tracker.TrackerExceptionHelper;
import ru.utils.PermissionsUtils;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

public class _Application extends Application
{
    private DBHelper      dbHelper;
    private ImageLoader   imageLoader;
    private File          imageCacheDir;
    private File          baseCacheDir;
    private Boolean       useSDCardForCache;
    public  RootData      _rootData = new RootData();


    private String        lastPushId;
    private AsyncTaskExecInterface asyncTaskExecutor;
    private TrackerAdapter mTracker;
    private String deviceId;


    @Override
    public void onCreate()
    {
        super.onCreate();
        /*
        try
        {
            Fabric.with(this, new Crashlytics());
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(getTracker(), e, false);
        }
        */

        /*
        final SharedPreferences prefs = Config.getSharedPreferences(this);
        boolean db_deleted = prefs.getBoolean("db_deleted", false);
        boolean dbReplaced = false;
        if (!db_deleted)
        {
            File f = getDatabasePath(DBHelper.DB_NAME);
            if (f.exists())
            {
                if (f.delete())
                {
                    Config.putBoolean(prefs, "db_deleted", true);
                    dbReplaced = true;
                }
            }
        }
        Log.d("#r#", "#r# App Started, dbReplaced = " + dbReplaced);
        */

        try
        {

            Parse.initialize(this);
            ParseInstallation.getCurrentInstallation().saveInBackground();
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(getTracker(), e, false);
        }

        //DBHelper.deleteTempDataBases(this);
    }

    public AsyncTaskExecInterface getAsyncTaskExecutor()
    {
        if (null == asyncTaskExecutor)
            asyncTaskExecutor = new AsyncTaskExecManager().build();
        return asyncTaskExecutor;
    }

    public String getDeviceId()
    {
        if (null == deviceId)
            deviceId = DeviceId.get(this);
        return deviceId;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public TrackerAdapter getTracker()
    {
        if (mTracker == null)
        {
            mTracker = new TrackerAdapter(this);
        }
        return mTracker;
    }

    //https://github.com/piwik/piwik-sdk-android


    @Override
    public void onLowMemory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH && mTracker != null) {
            mTracker.dispatch();
        }
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        if ((level == TRIM_MEMORY_UI_HIDDEN || level == TRIM_MEMORY_COMPLETE) && mTracker != null) {
            mTracker.dispatch();
        }
        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate()
    {
        //if (null != dbHelper)
        //   dbHelper.close();
        if (null != imageLoader) imageLoader.destroy();
        super.onTerminate();
    }

    public void resetImageLoader()
    {
        if (null != imageLoader)
        {
            imageLoader.clearMemoryCache();
            imageLoader.clearDiskCache();
        }
    }

    public String getVersionString()
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            sb.append(info.versionName).append(' ').append(info.versionCode);
        }
        catch (Throwable err)
        {
            err.printStackTrace();
        }
        return sb.toString();
    }

    public ImageLoader getImageLoader()
    {
        if (null == imageLoader)
        {
            imageLoader = createImageLoader();
        }
        return imageLoader;
    }

    public File getImageCacheDirectory()
    {
        if (null == imageCacheDir)
        {
            File parent = getBaseCacheDirectory();
            if (null == parent)
                return null;
            imageCacheDir = new File(parent, "images");
            if (!imageCacheDir.exists())
                imageCacheDir.mkdirs();
        }
        return imageCacheDir;
    }

    public File getBaseCacheDirectory()
    {
        if (null == baseCacheDir)
        {
            baseCacheDir = getCacheDirectory(isUseSDCardForCache());
        }
        return baseCacheDir;
    }

    public File getCacheDirectory(boolean useSDCard)
    {
        File dir = StorageUtils.getCacheDirectory(this, useSDCard);
        if (null != dir && !dir.exists())
            dir.mkdirs();
        return dir;
    }

    public boolean isUseSDCardForCache()
    {
        if (null == useSDCardForCache)
        {
            boolean SDCardWriteEnabled = PermissionsUtils.checkPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            useSDCardForCache = SDCardWriteEnabled ? Config.getSharedPreferences(this)
                    .getBoolean(getString(R.string.use_sd_card_for_cache), true) : false;
        }
        return useSDCardForCache;
    }

    public void setUseSDCardForCache(boolean value)
    {
        useSDCardForCache = value;
        Config.putBoolean(Config.getSharedPreferences(this), getString(R.string.use_sd_card_for_cache), value);
    }

    public DisplayImageOptions.Builder generateDefaultImageOptionsBuilder()
    {
        return
        new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.ic_stub) // resource or drawable
                //.showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
                //.showImageOnFail(R.drawable.ic_error) // resource or drawable
                .resetViewBeforeLoading(true)

                //.resetViewBeforeLoading(false)  // default
                //.delayBeforeLoading(1000)

                .cacheInMemory(true)
                .cacheOnDisk(true)


                //.preProcessor(...)
                //.postProcessor(...)
                //.extraForDownloader(...)

                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                //.imageScaleType(ImageScaleType.EXACTLY)
                //.bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .bitmapConfig(Bitmap.Config.RGB_565)

                //.decodingOptions(...)
                //.displayer(new SimpleBitmapDisplayer()) // default
                //.handler(new Handler()) // default
        ;
    }

    public boolean isPushHandled(String id)
    {
        if (null != lastPushId && null != id)
        {
            final int len = id.length();
            if (len == lastPushId.length() && lastPushId.regionMatches(0, id, 0, len))
                return true;
        }
        lastPushId = id;
        return false;
    }

    public void restartImageLoader()
    {
        if (null != imageLoader)
        {
            imageLoader.stop();
            imageLoader = createImageLoader();
        }
    }

    private ImageLoader createImageLoader()
    {
        /*
        displayOptions = new DisplayImageOptions.Builder()
//                                .showStubImage(R.drawable.kiosk_ic_coverstub) // resource or drawable
//                                .showImageForEmptyUri(R.drawable.kiosk_ic_coverstub) // resource or drawable
//                                .showImageOnFail(R.drawable.kiosk_ic_coverstub) // resource or drawable
                                .resetViewBeforeLoading(true)  // default
                                .delayBeforeLoading(1)
                                .cacheInMemory(false) // default
                                .cacheOnDisk(true) // default
                                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) // default
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .displayer(new SimpleBitmapDisplayer())
                                .build();
        */

        /*
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int max   = Math.max(height, width);
        */

        File cacheDir = getImageCacheDirectory();
        File reservedDir = null;
        try
        {
            reservedDir = new File(getFilesDir(), "images");
            if (!reservedDir.exists())
                reservedDir.mkdirs();
        }
        catch (Throwable e)
        {

        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

                //.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                //.memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(12)
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiskCache(cacheDir, reservedDir))
                //.diskCacheFileNameGenerator(new ImageLoaderFileNameGenerator())

                /*
                .diskCache(new UnlimitedDiskCache(cacheDir, reservedDir,
                        new ImageLoaderFileNameGenerator()
                        //new HashCodeFileNameGenerator()
                        ))
                 */
                //.imageDownloader(new BaseImageDownloaderEx(this)) // default
                .imageDownloader(new BaseImageDownloader(this))
                .imageDecoder(new BaseImageDecoder(false)) // default
                .build();

        ImageLoader il  = ImageLoader.getInstance();
        il.init(config);
        return il;
    }

    /*
    public JSONArray getSettings()
    {
        return settings;
    }

    public void setSettings(JSONArray settings)
    {
        this.settings = settings;
    }
    */


    public DBHelper getDbHelper()
    {
        if (null != dbHelper)
            return dbHelper;

        synchronized (this)
        {
            if (null == dbHelper)
            {
                try
                {
                    dbHelper = new DBHelper(this);
                }
                catch (Throwable e)
                {
                    TrackerExceptionHelper.sendExceptionStatistics(getTracker(), e, false);
                }
            }
        }
        return dbHelper;
    }
}


/*
// push
http://stackoverflow.com/questions/24868854/parse-com-disable-push-notification-notification
https://parse.com/questions/how-to-turn-on-and-off-the-push-notifications-from-settings
http://stackoverflow.com/questions/28661331/let-user-enable-disable-push-notifications-in-parse
 */

