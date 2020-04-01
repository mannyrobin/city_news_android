package ru.mycity.data;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.mycity._Application;

public class PushData
{
    public String table;
    public String id;
    public static final String INTENT_KEY = "com.parse.Data";

    @Override
    public String toString()
    {
        return "PushData{" +
                "table='" + table + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

     /*
    {
      "alert": "Праздничная акция! Букет 🌹 + бесплатная доставка 🚗 + 🎁. Спец-цена для пользователей приложения 😉",
      "open_id": "60",
      "table": "afisha"
    }
    */
    //"open_id": "5010",

            /*
            [com.parse.Data={"alert":"Праздничная акция! Букет 🌹 + бесплатная доставка 🚗 + 🎁. Спец-цена для пользователей приложения 😉","open_id":"5010","parsePushId":"KhgP5jgV1p","push_hash":"2ced006bd59d3fd66cbc057baefcf688","table":"afisha"}]
            */

    public static PushData extract(_Application application, Intent intent)
    {
        //dumpIntent(intent);
        if (null == intent)
        {
            return null;
        }
        Bundle bundle = intent.getExtras();
        if (null == bundle)
        {
            return null;
        }
        String parseData = bundle.getString(INTENT_KEY);
        if (null == parseData)
        {
            return null;
        }

        //{"alert":"Праздничная акция! Букет 🌹 + бесплатная доставка 🚗 + 🎁. Спец-цена для пользователей приложения 😉","open_id":"5010","parsePushId":"M46hyC6Ylr","push_hash":"2ced006bd59d3fd66cbc057baefcf688","table":"afisha"}
        try
        {
            JSONObject jsonObject = new JSONObject(parseData);
            String parsePushId = jsonObject.getString("parsePushId");
            if (null == parsePushId)
            {
                return null;
            }
            if (application.isPushHandled(parsePushId))
            {
                return null;
            }
            PushData pushData = new PushData();
            pushData.table = jsonObject.getString("table");
            pushData.id = jsonObject.getString("open_id");
            return pushData;
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(application), e, false);
        }
        return null;
    }
}
