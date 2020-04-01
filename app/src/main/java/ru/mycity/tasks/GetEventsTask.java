package ru.mycity.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.AppUrls;
import ru.mycity._Application;
import ru.mycity.data.Event;
import ru.mycity.database.DbDataHelper;
import ru.mycity.database.DbEventsHelper;
import ru.mycity.network.HttpClient;
import ru.mycity.parser.EventsParser;


public class GetEventsTask extends AsyncTask<String, Void, IResultCallback.Result>
{
    private final IResultCallback resultCallback;
    private final int rc;
    private final _Application application;
    private Throwable error;

    public GetEventsTask(_Application application, int rc, IResultCallback resultCallback)
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

        long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbEventsHelper.TABLE_NAME);

        sb.append("events?actual=true&sort=date");

        if (maxUpdatedAt > 0)
        {
            sb.append("&updated_at=gt:");
            sb.append(maxUpdatedAt);
        }

        String url = sb.toString();
        try
        {
            HttpClient.Result res = HttpClient.executeGet(url, new EventsParser(true));

            if (null != res && res.rc == HttpClient.Result.SUCCESS)
            {
                /*
                String tableName = actions ? DbDataHelper.PROMOTIONS_TABLE:  DbDataHelper.EVENTS_TABLE;
                DbDataHelper.insertEvents(application.getDbHelper().getWritableDatabase(), (List<Event>) res.parseData, tableName, false);

                List<Event> events = DbDataHelper.getEvents(application.getDbHelper().getReadableDatabase(), tableName, !actions, !actions);
                */

                Result result = new Result();
                List<Event> events = (List<Event>) res.parseData;
                if (null != events && !events.isEmpty())
                {
                    DbEventsHelper.insertEvents(application.getDbHelper().getWritableDatabase(), events);
                    result.events = DbEventsHelper.getEvents(application.getDbHelper().getReadableDatabase(), true, true);
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
        public ArrayList<Event> events;
        public boolean hasNewData;
    }
}
