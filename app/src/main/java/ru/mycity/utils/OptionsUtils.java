package ru.mycity.utils;

public class OptionsUtils
{
    /*
    private final static String KEY = "key";
    private final static String IS_DELETED = "is_deleted";
    private final static String VALUE = "value";

    public static boolean getBoolean(JSONArray options, String key, boolean _default)
    {
        JSONObject obj = getOption(options, key);
        return (null != obj) ? obj.optBoolean(VALUE, _default) : _default;
    }

    public static int getInt(JSONArray options, String key, int _default)
    {
        JSONObject obj = getOption(options, key);
        return (null != obj) ? obj.optInt(VALUE, _default) : _default;
    }

    public static double getDouble(JSONArray options, String key, double _default)
    {
        JSONObject obj = getOption(options, key);
        return (null != obj) ? obj.optDouble(VALUE, _default) : _default;
    }


    public static JSONObject getOption(JSONArray options, String key)
    {
        if (null == options || null == key)
            return null;

        int klen = key.length();
        if (0 == klen)
            return null;
        int len = options.length();
        for (int i = 0; i < len; i++)
        {
            JSONObject obj = options.optJSONObject(i);
            if (null != obj)
            {
                String keyVal = obj.optString(KEY, null);
                if (keyVal.length() == klen && keyVal.regionMatches(0, key, 0, klen))
                {
                     if (1 != obj.optInt(IS_DELETED))
                         return obj;
                     return null;
                }
            }
        }
        return null;
    }
    */
}
