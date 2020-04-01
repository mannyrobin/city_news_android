package ru.mycity.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.mycity._Application;
import ru.mycity.data.Event;
import ru.mycity.data.PushData;
import ru.mycity.data.RootData;


public class ResolveEventTask extends AsyncTask<Void, Void, IResultCallback.Result>
{
    private final IResultCallback resultCallback;
    private final int rc;

    private final _Application application;
    private final RootData rootData;
    private final PushData pushData;
    private Throwable error;

    public ResolveEventTask(_Application application, RootData rootData, PushData pushData, int rc, IResultCallback resultCallback)
    {
        this.resultCallback = resultCallback;
        this.rc = rc;
        this.rootData = rootData;
        this.pushData = pushData;
        this.application = application;
    }

    @Override
    protected Result doInBackground(Void... params)
    {
        Event event = getEvent();
        if (null != event)
        {
            Result result = new Result();
            result.event = event;
            return result;
        }
        return null;
    }

    private Event getEvent()
    {
        /*
        long id = Long.parseLong(pushData.id);
        if (null != rootData)
        {
            Event event = find(rootData.actions, id);
            if (null != event)
               return event;

            event = find(rootData.events, id);
            if (null != event)
                return event;
            ArrayList<Event> events = loadEvents(true);
            if (null != event)
            {
                rootData.actions = events;
                event = find(events, id);
                if (null != event)
                    return event;
            }

            events = loadEvents(false);
            if (null != event)
            {
                rootData.events = events;
                event = find(events, id);
                if (null != event)
                    return event;
            }
        }
        */
        return null;
    }

    private Event find(List<Event> events, long id)
    {
        if (null == events)
        {
            return null;
        }
        for (Event e : events)
        {
            if (e.id == id)
            {
                return e;
            }
        }
        return null;
    }

    private ArrayList<Event> loadEvents(boolean promotions)
    {
//        String url = AppUrls.API_URL +
//                (promotions ? "events?is_shared=true" :
//                             "events?actual=true&sort=date");
//        try
//        {
//            HttpClient.Result res = HttpClient.executeGet(url, new EventsParser(!promotions));
//
//            if (null != res && res.rc == HttpClient.Result.SUCCESS)
//            {
//                String tableName = promotions ? DbDataHelper.ACTIONS_TABLE : DbDataHelper.EVENTS_TABLE;
//                DbDataHelper.insertEvents(application.getDbHelper().getWritableDatabase(), (List<Event>) res.parseData, tableName);
//                ArrayList<Event> events = DbDataHelper.getEvents(application.getDbHelper().getReadableDatabase(), tableName, !promotions, !promotions);
//                return events ;
//            }
//        }
//        catch (Throwable e)
//        {
//            error = e;
//        }
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
        public Event event;
    }
}
