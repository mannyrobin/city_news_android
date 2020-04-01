package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.data.Action;

/**
 * Created by Sergey Rumyantsev on 21.05.2016.
 */
public class DbActionsHelper
{
    private final static String ACTION_SELECT = "SELECT ID,TITLE,ADDRESS,AVATAR,DATE,DATE_END,LIFE_TIME_TYPE,TYPE,PRICE,PHONES,PHOTOS,INFO,SITE,SITE_TITLE,ORGANIZATION_ID,HIGHLIGHT,PROMOTED FROM ";
    public final static String TABLE_NAME = "ACTIONS";

    public static boolean insertActions(SQLiteDatabase db, List<Action> actions)
    {
        boolean result = false;

        SQLiteStatement statement = null;
        db.beginTransaction();
        try
        {
            if (null != actions)
            {
                statement = db.compileStatement("INSERT OR REPLACE INTO " + TABLE_NAME +
                        "(ID,TITLE,ADDRESS,AVATAR,DATE,DATE_END,LIFE_TIME_TYPE,TYPE,PRICE,PHONES,PHOTOS,INFO,SITE,SITE_TITLE,ORGANIZATION_ID,HIGHLIGHT,PROMOTED,UPDATED_AT,DELETED,PUBLISHED,VIDEO,IS_VIDEO_HIDDEN) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                for (Action action : actions)
                {
                    int sqlParamIndex = 0;
                    statement.bindLong(++sqlParamIndex, action.id);

                    String val = action.title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.address;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.avatar;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    long v = action.date;
                    if (0 != v)
                    {
                        statement.bindLong(++sqlParamIndex, v);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    v = action.date_end;
                    if (0 != v)
                    {
                        statement.bindLong(++sqlParamIndex, v);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    int integerValue = action.life_time_type;
                    if (0 != integerValue)
                    {
                        statement.bindLong(++sqlParamIndex, integerValue);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.type;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.price;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.phones;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.photos;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.info;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.site;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    val = action.site_title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    statement.bindLong(++sqlParamIndex, action.organization_id);
                    statement.bindLong(++sqlParamIndex, action.highlight ? 1 : 0);
                    statement.bindLong(++sqlParamIndex, action.promoted);
                    statement.bindLong(++sqlParamIndex, action.updated_at);
                    statement.bindLong(++sqlParamIndex, action.deleted ? 1 : 0);
                    statement.bindLong(++sqlParamIndex, action.published ? 1 : 0);

                    val = action.video;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(++sqlParamIndex, val);
                    }
                    else
                    {
                        statement.bindNull(++sqlParamIndex);
                    }

                    statement.bindLong(++sqlParamIndex, action.is_video_hidden ? 1 : 0);

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
            {
                statement.close();
            }
            DBHelper.endTransaction(db);
        }
        return result;
    }

    public static ArrayList<Action> getActions(SQLiteDatabase db, boolean dateSort)
    {
        StringBuilder sb = new StringBuilder(232);
        sb.append(ACTION_SELECT);
        sb.append(TABLE_NAME);
        sb.append(" WHERE DELETED!=1 AND PUBLISHED=1 ");

        long currentDate = System.currentTimeMillis();
        /*
        sb.append(
            String.format(
                "AND (LIFE_TIME_TYPE IS NULL OR LIFE_TIME_TYPE = 1 OR (LIFE_TIME_TYPE = 2 AND (DATE IS NULL OR DATE < %d) AND (DATE IS NULL OR %d < DATE_END)))",
                currentDate, currentDate
            )
        );
        */

        sb.append(
                String.format(
                        "AND (LIFE_TIME_TYPE IS NULL OR LIFE_TIME_TYPE = 1 OR (LIFE_TIME_TYPE = 2 AND (DATE_END IS NULL OR %d < DATE_END)))",
                        currentDate
                )
        );


        sb.append(" ORDER BY PROMOTED DESC");
        if (dateSort)
        {
            sb.append(",DATE");
        }
        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Action> result = null;

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                Action event = readAction(cursor);
                result.add(event);
            }
            cursor.close();
        }
        return result;
    }

    private static Action readAction(Cursor cursor)
    {
        int reslutsIndex = -1;
        Action event = new Action();
        event.id = cursor.getLong(++reslutsIndex);
        event.title = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.address = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.avatar = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.date = cursor.isNull(++reslutsIndex) ? 0 : cursor.getLong(reslutsIndex);
        event.date_end = cursor.isNull(++reslutsIndex) ? 0 : cursor.getLong(reslutsIndex);

        event.life_time_type = cursor.isNull(++reslutsIndex) ? 0 : cursor.getInt(reslutsIndex);

        event.type = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.price = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.phones = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.photos = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);

        event.info = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.site = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);
        event.site_title = cursor.isNull(++reslutsIndex) ? null : cursor.getString(reslutsIndex);

        event.organization_id = cursor.isNull(++reslutsIndex) ? 0 : cursor.getInt(reslutsIndex);
        event.highlight = cursor.isNull(++reslutsIndex) ? false : 1 == cursor.getInt(reslutsIndex);
        event.promoted = cursor.isNull(++reslutsIndex) ? 0 : cursor.getInt(reslutsIndex);
        return event;
    }
}
