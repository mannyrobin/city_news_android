package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import ru.mycity.data.About;


public class DbAboutHelper
{
    public static boolean insert(SQLiteDatabase db, About about)
    {
        boolean result = false;

        if (null == about)
            return false;

        SQLiteStatement statement = null;
        SQLiteStatement statementOwner = null;
        db.beginTransaction();
        try
        {
            db.execSQL("DELETE FROM ABOUT");

            statement = db.compileStatement("INSERT OR REPLACE INTO ABOUT(ID, PHONE, ABOUT_PROJECT, UPDATED_AT) values(?,?,?,?)");

            statement.bindLong(1, about.id);

            String val = about.phone;
            if (null != val && 0 != val.length())
                statement.bindString(2, val);
            else
                statement.bindNull(2);

            val = about.about_project;
            if (null != val && 0 != val.length())
                statement.bindString(3, val);
            else
                statement.bindNull(3);

            statement.bindLong(4, about.updated_at);
            statement.execute();

            db.execSQL("DELETE FROM OWNERS");

            ArrayList<About.Owner> owners = about.owners;
            if (null != owners && !owners.isEmpty())
            {
                statementOwner = db.compileStatement("INSERT OR REPLACE INTO OWNERS(PIC_LINK, NAME, DESC) values(?,?,?)");

                for (About.Owner owner : owners)
                {
                    val = owner.pic_link;

                    if (null != val && 0 != val.length())
                        statementOwner.bindString(1, val);
                    else
                        statementOwner.bindNull(1);

                    val = owner.name;
                    if (null != val)
                        statementOwner.bindString(2, val);
                    else
                        statementOwner.bindNull(2);

                    val = owner.desc;
                    if (null != val && 0 != val.length())
                        statementOwner.bindString(3, val);
                    else
                        statementOwner.bindNull(3);

                    statementOwner.execute();
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
            if (null != statementOwner) statementOwner.close();
            DBHelper.endTransaction(db);
        }
        return result;
    }

    public static About read(SQLiteDatabase db)
    {
        About about = new About();

        String sql = "SELECT ID, PHONE, ABOUT_PROJECT FROM ABOUT";
        Cursor cursor = db.rawQuery(sql, null);

        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                about.id = cursor.getLong(0);
                about.phone         = cursor.isNull(1) ? null : cursor.getString(1);
                about.about_project = cursor.isNull(2) ? null : cursor.getString(2);
                break;
            }
            cursor.close();
        }

        sql = "SELECT name, pic_link, desc FROM OWNERS";
        cursor = db.rawQuery(sql, null);
        if (null != cursor)
        {
            ArrayList<About.Owner> owners = new ArrayList<>(cursor.getCount());
            about.owners = owners;
            while (cursor.moveToNext())
            {
                About.Owner owner = new About.Owner();
                owner.name      = cursor.isNull(0) ? null : cursor.getString(0);
                owner.pic_link  = cursor.isNull(1) ? null : cursor.getString(1);
                owner.desc      = cursor.isNull(2) ? null : cursor.getString(2);
                owners.add(owner);
            }
            cursor.close();
        }
        return about;
    }
}
