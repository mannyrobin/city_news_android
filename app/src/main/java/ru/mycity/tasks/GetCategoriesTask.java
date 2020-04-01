package ru.mycity.tasks;

import android.os.AsyncTask;

import java.util.List;

import ru.mycity.AppUrls;
import ru.mycity.data.Category;
import ru.mycity.network.HttpClient;
import ru.mycity.parser.CategoriesParser;


public class GetCategoriesTask extends AsyncTask<String, Void, IResultCallback.Result>
{
    private final IResultCallback resultCallback;
    private final int rc;
    private Throwable error;

    public GetCategoriesTask(int rc, IResultCallback resultCallback)
    {
        this.resultCallback = resultCallback;
        this.rc = rc;
    }

    @Override
    protected Result doInBackground(String ... params)
    {
        StringBuilder sb  = new StringBuilder(34 + ((null != params) ? params.length * 16 : 0) );
        sb.append(AppUrls.API_URL).append("categories");
        if (null != params)
        {
            for (String param : params)
            {
                sb.append('/').append(param).append("/categories");
            }
            sb.append("?per-page=-1");
        }
        String url = sb.toString();
        try
        {
            HttpClient.Result res = HttpClient.executeGet(url, new CategoriesParser());
            Result result = new Result();
            result.categories = (List<Category>) res.parseData;
            return result;
        }
        catch (Throwable e)
        {
            error = e;
            return null;
        }
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
        public List<Category> categories;
    }
}


