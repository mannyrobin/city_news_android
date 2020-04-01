package ru.mycity;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;


public class BaseImageDownloaderEx extends BaseImageDownloader
{

    public BaseImageDownloaderEx(Context context)
    {
        super(context);
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException
    {
        HttpURLConnection urlConnection = (HttpURLConnection) super.createConnection(url, extra);
        //urlConnection.setRequestProperty("x-api-uid",    AppData.getUID());
        //urlConnection.setRequestProperty("x-api-secret", AppData.getSecret());
        return urlConnection;
    }

}
