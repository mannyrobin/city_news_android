package ru.mycity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper
{
    public  final  static int    DB_VER  = 9;
    private final  static String DB_NAME_PREFIX = "mg.dat";
    public final static String DB_NAME = DB_NAME_PREFIX;
    private final Context mContext;

    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VER);
        mContext = context;
    }

    public Context getContext()
    {
        return mContext;
    }

    public static void endTransaction(SQLiteDatabase db)
    {
        try
        {
            db.endTransaction();
        }
        catch (Throwable e)
        {
        }
    }
}