package ru.mycity.device;

import android.content.Context;
import android.content.SharedPreferences;

import ru.mycity.Config;

public class DeviceId
{
    public static String get(Context context)
    {
        DeviceIDGenerator generator = new DeviceIDGenerator();
        String id =  generator.build(context);
        if (null != id && id.length() > 0)
            return id;
        SharedPreferences prefs = Config.getSharedPreferences(context);
        id = prefs.getString("rdi", null);
        if (null != id && id.length() > 0)
            return id;
        id = generator.generateRandom();
        Config.putString(prefs, "rdi", id);
        return id;
    }
}