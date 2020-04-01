package ru.mycity;


import android.content.Context;
import android.content.SharedPreferences;

public class Config
{
    public static String push_suggested = "push_suggested";
    public static String push_enable = "push_enable";
    public static String run_count = "run_count";
    public static String intro_shown = "intro_shown";

    private static SharedPreferences preferences;

    public static SharedPreferences getSharedPreferences(final android.content.Context context)
    {
        return (null != preferences) ? preferences :
                (preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName() + ".ini",
                        Context.MODE_PRIVATE));
    }

    public static void putString(SharedPreferences prefs, String key,
                                 String value)
    {
        prefs.edit().putString(key, value).apply();
    }

    public static void removeKey(SharedPreferences prefs, String key)
    {
        prefs.edit().remove(key).apply();
    }

    public static void putInt(SharedPreferences prefs, String key, int value)
    {
        prefs.edit().putInt(key, value).apply();
    }

    public static void putLong(SharedPreferences prefs, String key, long value)
    {
        prefs.edit().putLong(key, value).apply();
    }

    public static void putBoolean(SharedPreferences prefs, String key, boolean value)
    {
        prefs.edit().putBoolean(key, value).apply();
    }

    public static void remove(SharedPreferences prefs, String key)
    {
        prefs.edit().remove(key).apply();
    }

}
