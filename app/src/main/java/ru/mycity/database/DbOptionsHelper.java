package ru.mycity.database;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import ru.mycity.data.Option;
import ru.utils._DBHelper;

public class DbOptionsHelper
{
    public static boolean insert(SQLiteDatabase db, ArrayList<Option> options)
    {
        boolean result = false;

        SQLiteStatement statement = null;
        db.beginTransaction();
        try
        {
            if (null != options)
            {
                statement = db.compileStatement("INSERT OR REPLACE INTO OPTIONS(ID, KEY, TYPE, VALUE, DELETED, UPDATED_AT) values(?,?,?,?,?,?)");

                for (Option option : options)
                {
                    statement.bindLong(1, option.id);
                    statement.bindString(2, option.key);

                    String val = option.key;
                    if (null != val && 0 != val.length()) statement.bindString(3, val);
                    else statement.bindNull(3);

                    val = option.value;
                    if (null != val && 0 != val.length()) statement.bindString(4, val);
                    else statement.bindNull(4);

                    statement.bindLong(5, option.deleted ? 1 : 0);
                    statement.bindLong(6, option.updated_at);
                    statement.execute();
                }
            }
            db.setTransactionSuccessful();
            result = true;
        }
        catch (Throwable e)
        {
            System.err.print(e.toString());
        }
        finally
        {
            if (null != statement) statement.close();
            DBHelper.endTransaction(db);
        }
        return result;
    }

    private static final String createQuery(final String key)
    {
        StringBuilder sb = new StringBuilder(52 + key.length());
        sb.append("SELECT VALUE FROM OPTIONS WHERE KEY='").append(key)
                .append("' AND DELETED=0");
        return sb.toString();
    }

    public static String getString(SQLiteDatabase db, String key, String _default)
    {
        return _DBHelper.simpleQueryForString(db, createQuery(key), _default);
    }

    public static long getLong(SQLiteDatabase db, String key, long _default)
    {
        return _DBHelper.simpleQueryForLong(db, createQuery(key), _default);
    }

    public static int getInt(SQLiteDatabase db, String key, int _default)
    {
        return (int) getLong(db, key, _default);
    }

    public static double getDouble(SQLiteDatabase db, String key, double _default)
    {
        String val = getString(db, key, null);
        if (null != val && 0 != val.length())
        {
            try
            {
                return Double.valueOf(val);
            }
            catch (Throwable e)
            {
            }
        }
        return _default;
    }

    private final static String _true = "true";

    public static boolean getBoolean(SQLiteDatabase db, String key, boolean _default)
    {
        String val = getString(db, key, null);
        if (null != val)
        {
            final int len  = val.length();
            return (len == _true.length() && val.regionMatches(true, 0, _true, 0, len));
        }
        return _default;
    }
}


