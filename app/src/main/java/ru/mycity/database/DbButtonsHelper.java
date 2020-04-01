package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import ru.mycity.data.Button;

public class DbButtonsHelper
{
    public static String TABLE_NAME = "BUTTONS";

    public static boolean deleteButtons(SQLiteDatabase db)
    {
        db.execSQL("delete from BUTTONS");
        return true;
    }

    public static boolean insertButtons(SQLiteDatabase db, ArrayList<Button> buttons)
    {
        boolean result = false;

        SQLiteStatement statement = null;
        db.beginTransaction();
        try
        {
            //db.execSQL("delete from BUTTONS");

            if (null != buttons)
            {
                    statement = db.compileStatement("INSERT OR REPLACE INTO BUTTONS(ID, TITLE, ICON, ROW_ID, _TABLE, PHONE, DELETED, PUBLISHED, LINK, POS, UPDATED_AT, IMAGE) values(?,?,?,?,?,?,?,?,?,?,?,?)");

                for (Button button : buttons)
                {
                    statement.bindLong(1, button.id);


                    String val = button.title;
                    if (null != val && 0 != val.length())
                        statement.bindString(2, val);
                    else
                        statement.bindNull(2);

                    val = button.icon;
                    if (null != val && 0 != val.length())
                        statement.bindString(3, val);
                    else
                        statement.bindNull(3);

                    statement.bindLong(4, button.row_id);

                    val = button.table;
                    if (null != val && 0 != val.length())
                        statement.bindString(5, val);
                    else
                        statement.bindNull(5);

                    val = button.phone;
                    if (null != val && 0 != val.length())
                        statement.bindString(6, val);
                    else
                        statement.bindNull(6);

                    statement.bindLong(7, button.deleted ? 1 : 0);
                    statement.bindLong(8, button.published ? 1 : 0);

                    val = button.link;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(9, val);
                    }
                    else
                    {
                        statement.bindNull(9);
                    }

                    statement.bindLong(10, button.pos);
                    statement.bindLong(11, button.updated_at);

                    val = button.image;
                    if (null != val && 0 != val.length())
                        statement.bindString(12, val);
                    else
                        statement.bindNull(12);


                    statement.execute();
                }
            }
            db.setTransactionSuccessful();
            result = true;
        }
        catch (Throwable e)
        {
            //if (null != e) //don't remove
            //{
            //Log.e("#_#", "#_# " + e.getMessage(), e);
            System.err.print(e.toString());
            //}
        }
        finally
        {
            if (null != statement)
                statement.close();
            DBHelper.endTransaction(db);
        }
        return result;
    }

    public static ArrayList<Button> getButtons(SQLiteDatabase db)
    {                    //   0    1     2     3      4      5       6       7
        String sql = "SELECT ID, TITLE, ICON, LINK, ROW_ID, _TABLE, PHONE, IMAGE FROM BUTTONS WHERE DELETED!=1 AND PUBLISHED=1 ORDER BY POS";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Button> result = null;

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                Button button = new Button();
                button.id = cursor.getLong(0);

                button.title  = cursor.isNull(1) ? null : cursor.getString(1);
                button.icon   = cursor.isNull(2) ? null : cursor.getString(2);
                button.link   = cursor.isNull(3) ? null : cursor.getString(3);

                button.row_id = cursor.isNull(4) ? 0 : cursor.getInt(4);
                button.table  = cursor.isNull(5) ? null : cursor.getString(5);
                button.phone  = cursor.isNull(6) ? null : cursor.getString(6);
                button.image  = cursor.isNull(7) ? null : cursor.getString(7);
                result.add(button);
            }
            cursor.close();
        }
        return result;
    }


}
