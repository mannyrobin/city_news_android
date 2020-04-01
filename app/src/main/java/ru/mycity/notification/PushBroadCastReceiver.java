package ru.mycity.notification;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import ru.mycity.Config;

public class PushBroadCastReceiver extends ParsePushBroadcastReceiver
{
    @Override
    protected void onPushReceive(Context context, Intent intent)
    {
        if (Config.getSharedPreferences(context).getBoolean(Config.push_enable, false))
            super.onPushReceive(context, intent);
    }
}