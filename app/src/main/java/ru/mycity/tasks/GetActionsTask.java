package ru.mycity.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.AppUrls;
import ru.mycity._Application;
import ru.mycity.data.Action;
import ru.mycity.database.DbActionsHelper;
import ru.mycity.database.DbDataHelper;
import ru.mycity.network.HttpClient;
import ru.mycity.parser.ActionsParser;

/**
 * Created by Sergey Rumyantsev on 23.05.2016.
 */
public class GetActionsTask extends AsyncTask<String, Void, IResultCallback.Result>
{
    private final IResultCallback resultCallback;
    private final int rc;
    private final _Application application;
    private Throwable error;

    public GetActionsTask(_Application application, int rc, IResultCallback resultCallback)
    {
        this.resultCallback = resultCallback;
        this.rc = rc;
        this.application = application;
    }

    @Override
    protected Result doInBackground(String ... params)
    {
        StringBuilder sb = new StringBuilder(64);
        sb.append(AppUrls.API_URL);

        long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbActionsHelper.TABLE_NAME);

        sb.append("actions?per-page=-1&actual=true");

        if (maxUpdatedAt > 0)
        {
            sb.append("&updated_at=gt:");
            sb.append(maxUpdatedAt);
        }

        String url = sb.toString();
        try
        {
            HttpClient.Result res = HttpClient.executeGet(url, new ActionsParser());

            if (null != res && res.rc == HttpClient.Result.SUCCESS)
            {
                /*
                String tableName = actions ? DbDataHelper.PROMOTIONS_TABLE:  DbDataHelper.EVENTS_TABLE;
                DbDataHelper.insertEvents(application.getDbHelper().getWritableDatabase(), (List<Event>) res.parseData, tableName, false);

                List<Event> events = DbDataHelper.getEvents(application.getDbHelper().getReadableDatabase(), tableName, !actions, !actions);
                */

                Result result = new Result();
                List<Action> events = (List<Action>) res.parseData;
                if (null != events && !events.isEmpty())
                {
                    DbActionsHelper.insertActions(application.getDbHelper().getWritableDatabase(), events);
                    result.actions    = DbActionsHelper.getActions(application.getDbHelper().getReadableDatabase(), true);
                    result.hasNewData = true;
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
        public ArrayList<Action> actions;
        public boolean hasNewData;
    }
}
