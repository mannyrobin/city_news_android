package ru.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public abstract class _DBHelper
{    

    private final static String TAG = "_DBHelper";

    public static void dropTableIfExists(SQLiteDatabase db, String name)
    {
        db.execSQL(new StringBuilder(22 + name.length()).append("DROP TABLE IF EXISTS ").append(name).append(';').toString());
    }
    
    
    public static long simpleQueryForLong(SQLiteDatabase db, final String sql, final long _default)
    {
        long result = _default;
        SQLiteStatement statement = null; 
        try
        {
            statement = db.compileStatement(sql);
            result = statement.simpleQueryForLong(); 
        }
        catch (Throwable e)
        {
            if (null != e) {final String msg = e.getMessage(); if (null != msg && 0 != msg.length()) Log.d(TAG, msg);};
        }
        finally
        {
            if (null != statement)
                statement.close();
        }
        return result;        
    }    
    
    
    public static String simpleQueryForString(SQLiteDatabase db, final String sql, final  String _default)
    {
        String result = _default;
        SQLiteStatement statement = null; 
        try
        {
            statement = db.compileStatement(sql);
            result = statement.simpleQueryForString();           
        }
        catch (Throwable e)
        {
            if (null != e) {final String msg = e.getMessage(); if (null != msg && 0 != msg.length()) Log.d(TAG, msg);};
        }
        finally
        {
            if (null != statement)
                statement.close();
        }
        return result;
    }
    
    
    public static void execSQL(SQLiteDatabase db, final String sql)
    {
        try
        {
            db.execSQL(sql);
        }
        catch (Throwable e)
        {
            if (null != e) {final String msg = e.getMessage(); if (null != msg && 0 != msg.length()) Log.d(TAG, msg);};
        }
    }


    public static /*int*/ void executeUpdateDelete(SQLiteDatabase db, final String sql)
    {
        SQLiteStatement statement = null; 
        try
        {
            statement = db.compileStatement(sql);
            db.compileStatement(sql).execute();
            // return db.compileStatement(sql).executeUpdateDelete();           
        }
        catch (Throwable e)
        {
            if (null != e) {final String msg = e.getMessage(); if (null != msg && 0 != msg.length()) Log.d(TAG, msg);};
        }
        finally
        {
            if (null != statement)
                statement.close();
        }
        //return 0;
    }


    public static /*long*/ void executeInsert(SQLiteDatabase db, final String sql)
    {
        SQLiteStatement statement = null; 
        try
        {
            statement = db.compileStatement(sql);
            db.compileStatement(sql).execute();
            ///*return*/ db.compileStatement(sql).executeInsert();           
        }
        catch (Throwable e)
        {
            if (null != e) {final String msg = e.getMessage(); if (null != msg && 0 != msg.length()) Log.d(TAG, msg);};
        }
        finally
        {
            if (null != statement)
                statement.close();
        }
        //return 0;
    }


    public static void exportDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            //File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                //String currentDBPath = "//data//"+context.getPackageName()+"//databases//"+databaseName;
                String backupDBPath = databaseName;
                //File currentDB = new File(data, currentDBPath);
                File currentDB = context.getDatabasePath(databaseName);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Throwable e) {
            if (null != e)
            {
                final String msg = e.getMessage();
                if (null != msg && 0 != msg.length())
                    Log.e(TAG, msg, e);
            };
        }
    }
}