package ru.mycity.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.AppUrls;
import ru.mycity._Application;
import ru.mycity.data.News;
import ru.mycity.database.DbDataHelper;
import ru.mycity.database.DbNewsHelper;
import ru.mycity.network.HttpClient;
import ru.mycity.parser.NewsParser;

public class GetNewsTask extends AsyncTask<String, Void, IResultCallback.Result>
{
    private final IResultCallback resultCallback;
    private final int rc;
    private final _Application application;
    private Throwable error;

    public GetNewsTask(_Application application, int rc, IResultCallback resultCallback)
    {
        this.resultCallback = resultCallback;
        this.rc = rc;
        this.application = application;
    }

    @Override
    protected Result doInBackground(String ... params)
    {
        long maxUpdatedAt  = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbNewsHelper.TABLE_NAME);
        String url = AppUrls.API_URL +
                //"news?actual=true&sort=-date&per-page=-1"
                //"news?actual=true&sort=-date";
                //"news?actual=true&sort=-date&updated_at=gt:" + maxUpdatedAt;
                "news?sort=-date&per-page=60&updated_at=gt:" + maxUpdatedAt;
        try
        {
            HttpClient.Result res = HttpClient.executeGet(url, 7 * 1000, false, new NewsParser());
            if (null != res && res.rc == HttpClient.Result.SUCCESS)
            {
                List<News> news = (List<News>) res.parseData;

                Result result = new Result();
                if (null != news && !news.isEmpty())
                {
                    DbNewsHelper.insert(application.getDbHelper().getWritableDatabase(), news);
                    result.news = DbNewsHelper.get(application.getDbHelper().getReadableDatabase(),
                            false, DbNewsHelper.PAGE_SIZE, 0);
                    //result.featuredNews = DbNewsHelper.get(application.getDbHelper().getReadableDatabase(),
                    //        true, 0, 0);
                    result.topNews = DbNewsHelper.getTopNews(application.getDbHelper().getReadableDatabase());
                    result.bSuccess = true;
                }
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
        public ArrayList<News> news;
        //public ArrayList<News> featuredNews;
        public ArrayList<News> topNews;
        public boolean bSuccess;
    }
}