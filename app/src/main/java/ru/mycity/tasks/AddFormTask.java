package ru.mycity.tasks;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.AppUrls;
import ru.mycity._Application;
import ru.mycity.data.News;
import ru.mycity.database.DbDataHelper;
import ru.mycity.network.HttpClient;
import ru.mycity.parser.JsonObjectParser;
import ru.mycity.parser.NewsParser;
import ru.utils.NameValueItem;


public class AddFormTask extends AsyncTask<String, Void, IResultCallback.Result>
{
    private final IResultCallback resultCallback;
    private final int rc;
    private final _Application application;
    private final String urlPart;
    private final NameValueItem [] pairs;
    private Throwable error;

    public AddFormTask(_Application application, String urlPart, NameValueItem[] pairs, int rc, IResultCallback resultCallback)
    {
        this.resultCallback = resultCallback;
        this.rc = rc;
        this.urlPart = urlPart;
        this.pairs = pairs;
        this.application = application;
    }

    @Override
    protected Result doInBackground(String ... params)
    {
        String url = AppUrls.API_URL + urlPart;
        try
        {
            Result result = new Result();
            HttpClient.Result res = HttpClient.executePost(url, pairs, new JsonObjectParser());
            result.res = res;
            if (null != res && (result.bSuccess = (res.rc == HttpClient.Result.SUCCESS)))
            {
                result.obj = (JSONObject) res.parseData;
                return result;
            }
        }
        catch (Throwable e)
        {
            error = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(IResultCallback.Result result)
    {
        if (null != resultCallback)
        {
            if (null != result)
                resultCallback.onFinished(rc, result);
            else
                resultCallback.onFailed(rc, error);
        }
    }

    public final static class Result extends IResultCallback.Result
    {
        public HttpClient.Result res;
        public JSONObject obj;
        public  boolean bSuccess;
    }
}
