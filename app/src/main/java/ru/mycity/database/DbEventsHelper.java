package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.data.Event;
import ru.utils._DateUtils;

public class DbEventsHelper
{
    public final static String TABLE_NAME = "EVENTS";
    private final static String EVENT_SELECT = "SELECT ID,TITLE,ADDRESS,AVATAR,DATE,TYPE,PRICE,PHONES,PHOTOS,INFO,SITE,SITE_TITLE,ORGANIZATION_ID,HIGHLIGHT,PROMOTED FROM ";


    public static Event getEvent(SQLiteDatabase db, String table, long id)
    {
        StringBuilder sb = new StringBuilder(150);
        sb.append(EVENT_SELECT);
        sb.append(table);
        sb.append(" WHERE ID = ");
        sb.append(id);

        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        Event event = null;

        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                event = readEvent(cursor);
                break;
            }
            cursor.close();
        }
        return event;
    }

    public static ArrayList<Event> getEvents(SQLiteDatabase db, boolean actualOnly, boolean dateSort)
    {
        StringBuilder sb = new StringBuilder(232);
        sb.append(EVENT_SELECT);
        sb.append(TABLE_NAME);
        sb.append(" WHERE DELETED!=1 AND PUBLISHED=1 ");
        if (actualOnly)
        {
            sb.append(" AND (date > ");
            long l = _DateUtils.getTruncatedCurrentTime() - 1;
            sb.append(l);
            sb.append(')');
        }
        sb.append(" ORDER BY PROMOTED DESC");
        if (dateSort)
        {
            sb.append(",DATE");
        }
        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Event> result = null;

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                Event event = readEvent(cursor);
                result.add(event);
            }
            cursor.close();
        }
        return result;
    }


    private static Event readEvent(Cursor cursor)
    {
        Event event = new Event();
        event.id = cursor.getLong(0);
        event.title = cursor.isNull(1) ? null : cursor.getString(1);
        event.address = cursor.isNull(2) ? null : cursor.getString(2);
        event.avatar = cursor.isNull(3) ? null : cursor.getString(3);
        event.date = cursor.isNull(4) ? 0 : cursor.getLong(4);

        event.type = cursor.isNull(5) ? null : cursor.getString(5);
        event.price = cursor.isNull(6) ? null : cursor.getString(6);
        event.phones = cursor.isNull(7) ? null : cursor.getString(7);
        event.photos = cursor.isNull(8) ? null : cursor.getString(8);

        event.info = cursor.isNull(9) ? null : cursor.getString(9);
        event.site = cursor.isNull(10) ? null : cursor.getString(10);
        event.site_title = cursor.isNull(11) ? null : cursor.getString(11);

        event.organization_id = cursor.isNull(12) ? 0 : cursor.getInt(12);
        event.highlight = cursor.isNull(13) ? false : 1 == cursor.getInt(13);
        event.promoted = cursor.isNull(14) ? 0 : cursor.getInt(14);
        return event;
    }

    public static boolean insertEvents(SQLiteDatabase db, List<Event> events)
    {
        boolean result = false;

        SQLiteStatement statement = null;
        db.beginTransaction();
        try
        {
            if (null != events)
            {
                statement = db.compileStatement("INSERT OR REPLACE INTO " + TABLE_NAME +
                        "(ID,TITLE,ADDRESS,AVATAR,DATE,TYPE,PRICE,PHONES,PHOTOS,INFO,SITE,SITE_TITLE,ORGANIZATION_ID,HIGHLIGHT,PROMOTED,UPDATED_AT,DELETED,PUBLISHED,VIDEO,IS_VIDEO_HIDDEN) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                for (Event event : events)
                {
                    statement.bindLong(1, event.id);

                    String val = event.title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(2, val);
                    }
                    else
                    {
                        statement.bindNull(2);
                    }

                    val = event.address;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(3, val);
                    }
                    else
                    {
                        statement.bindNull(3);
                    }

                    val = event.avatar;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(4, val);
                    }
                    else
                    {
                        statement.bindNull(4);
                    }

                    long v = event.date;
                    if (0 != v)
                    {
                        statement.bindLong(5, v);
                    }
                    else
                    {
                        statement.bindNull(5);
                    }

                    val = event.type;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(6, val);
                    }
                    else
                    {
                        statement.bindNull(6);
                    }

                    val = event.price;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(7, val);
                    }
                    else
                    {
                        statement.bindNull(7);
                    }

                    val = event.phones;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(8, val);
                    }
                    else
                    {
                        statement.bindNull(8);
                    }

                    val = event.photos;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(9, val);
                    }
                    else
                    {
                        statement.bindNull(9);
                    }

                    val = event.info;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(10, val);
                    }
                    else
                    {
                        statement.bindNull(10);
                    }

                    val = event.site;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(11, val);
                    }
                    else
                    {
                        statement.bindNull(11);
                    }

                    val = event.site_title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(12, val);
                    }
                    else
                    {
                        statement.bindNull(12);
                    }

                    statement.bindLong(13, event.organization_id);
                    statement.bindLong(14, event.highlight ? 1 : 0);
                    statement.bindLong(15, event.promoted);
                    statement.bindLong(16, event.updated_at);
                    statement.bindLong(17, event.deleted ? 1 : 0);
                    statement.bindLong(18, event.published ? 1 : 0);

                    val = event.video;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(19, val);
                    }
                    else
                    {
                        statement.bindNull(19);
                    }

                    statement.bindLong(20, event.is_video_hidden ? 1 : 0);

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

    //public static List<Event> getEvents(SQLiteDatabase db)
    //{
    //    return getEvents(db, true, true);
    //}




}
