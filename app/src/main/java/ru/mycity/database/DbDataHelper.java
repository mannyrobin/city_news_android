package ru.mycity.database;


import android.database.sqlite.SQLiteDatabase;

import ru.utils._DBHelper;

public class DbDataHelper
{
    public static long getMaxUpdatedAt(SQLiteDatabase db, String tableName)
    {
        return getMaxUpdatedAt(db, tableName, 0);
    }

    public static long getMaxUpdatedAt(SQLiteDatabase db, String tableName, long _default)
    {
        return _DBHelper.simpleQueryForLong(db, "SELECT MAX(UPDATED_AT) FROM " + tableName, _default);
    }

    public static long getCount(SQLiteDatabase db, String tableName)
    {
        return _DBHelper.simpleQueryForLong(db, "SELECT COUNT(*) FROM " + tableName, 0);
    }

    public static boolean isObjectExist(SQLiteDatabase db, String tableName, long id)
    {
        return (id == _DBHelper.simpleQueryForLong(db, "SELECT ID FROM " + tableName + " WHERE ID=" + id, 0));
    }

    // Possible error
    public static void clearTable(SQLiteDatabase db, long maxUpdatedAt, String tableName, boolean hasPublished, int frequency)
    {
        if ( (0 == (System.currentTimeMillis() % frequency)) && (maxUpdatedAt > 0))
        {
            try
            {
                StringBuilder sb = new StringBuilder(90);
                sb.append("DELETE FROM ").append(tableName)
                        .append(" WHERE (UPDATED_AT < ").append(maxUpdatedAt)
                        .append(") AND (DELETED=1");
                if (hasPublished)
                    sb.append(" OR PUBLISHED=0");
                sb.append(')');
                String sql = sb.toString();
                db.execSQL(sql);
            }
            catch (Throwable e)
            {
                System.err.print(e);
            }
        }
    }

    //SQLiteDatabase.execSQL(“begin immediate transaction”);
    //SQLiteDatabase.execSQL(“commit transaction”);

    public static boolean setBulkMode(SQLiteDatabase db)
    {
        try
        {
            db.execSQL("PRAGMA synchronous=OFF");
            //db.execSQL("PRAGMA cache_size=4000"); // default 1000
            //db.execSQL("PRAGMA count_changes=OFF");

            //PRAGMA locking_mode = EXCLUSIVE
            db.setLockingEnabled(false);
            return true;
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }

        return false;
    }

    public static boolean setNormalMode(SQLiteDatabase db)
    {

        db.setLockingEnabled(true);
        try
        {
            db.execSQL("PRAGMA synchronous=NORMAL");
            //db.execSQL("PRAGMA synchronous=1");
            return true;
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }

        return false;
    }
    //http://www.enterra.ru/blog/android_issues_with_sqlite/



}

