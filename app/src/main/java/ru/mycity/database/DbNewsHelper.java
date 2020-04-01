package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.data.News;

public class DbNewsHelper
{
    private final static String SELECT = "SELECT ID,TITLE,PIC,ORIGIN_TITLE,ORIGIN_LINK,BODY,HIGHLIGHT,PROMOTED,DATE,FEATURED FROM NEWS";
    public final static int PAGE_SIZE = 20;
    public final static String TABLE_NAME = "NEWS";

    public static ArrayList<News> get(SQLiteDatabase db, boolean featuredOnly, int limit, int offset)
    {
        final StringBuilder sb = new StringBuilder(160);
        sb.append(SELECT);
        sb.append(" WHERE DELETED=0 AND PUBLISHED=1");
		if (featuredOnly)
			sb.append(" AND FEATURED=1");
 
		sb.append(" ORDER BY PROMOTED DESC, DATE DESC");

        if (0 != limit)
            sb.append(" LIMIT ").append(limit);
        if (0 != offset)
            sb.append(" OFFSET ").append(offset);

        String sql = sb.toString();

        return readNewses(db, sql);
    }

    @Nullable
    static ArrayList<News> readNewses(SQLiteDatabase db, String sql)
    {
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<News> result = null;

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                News news = get(cursor);
                result.add(news);
            }
            cursor.close();
        }
        return result;
    }


    private static News get(Cursor cursor)
    {
        News news = new News();
        news.id = cursor.getLong(0);
        news.title = cursor.isNull(1) ? null : cursor.getString(1);
        news.pic = cursor.isNull(2) ? null : cursor.getString(2);

        news.origin_title = cursor.isNull(3) ? null : cursor.getString(3);
        news.origin_link = cursor.isNull(4) ? null : cursor.getString(4);

        news.body = cursor.isNull(5) ? null : cursor.getString(5);
        news.highlight = cursor.isNull(6) ? false : 1 == cursor.getInt(6);
        news.promoted = cursor.isNull(7) ? 0 : cursor.getInt(7);
        news.date = cursor.isNull(8) ? 0 : cursor.getLong(8);
        news.featured = cursor.isNull(9) ? false : 1 == cursor.getInt(9);
        return news;
    }

    public static News getById(SQLiteDatabase db, long id)
    {
        StringBuilder sb = new StringBuilder(108);
        sb.append(SELECT);
        sb.append(" WHERE ID=");
        sb.append(id);
        String sql = sb.toString();

        Cursor cursor = db.rawQuery(sql, null);
        News result = null;

        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                result = get(cursor);
                break;
            }
            cursor.close();
        }
        return result;
    }

    public static boolean insert(SQLiteDatabase db, List<News> news)
    {
        boolean result = false;

        SQLiteStatement statement = null;
        db.beginTransaction();
        try
        {
            if (null != news)
            {
                statement = db.compileStatement("INSERT OR REPLACE INTO NEWS(ID,TITLE,PIC,ORIGIN_TITLE,ORIGIN_LINK,BODY,HIGHLIGHT,PROMOTED,DATE,DELETED,UPDATED_AT,FEATURED,PUBLISHED,VIDEO,IS_VIDEO_HIDDEN) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                for (News n : news)
                {
                    statement.bindLong(1, n.id);

                    String val = n.title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(2, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(2);
                    //}

                    val = n.pic;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(3, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(3);
                    //}

                    val = n.origin_title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(4, val);
                    }
                    else
                    {
                        statement.bindNull(4);
                    }

                    val = n.origin_link;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(5, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(5);
                    //}

                    val = n.body;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(6, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(6);
                    //}

                    statement.bindLong(7, n.highlight ? 1 : 0);
                    statement.bindLong(8, n.promoted);

                    long v = n.date;
                    if (0 != v)
                    {
                        statement.bindLong(9, v);
                    }
                    //else
                    //{
                    //    statement.bindNull(9);
                    //}

                    statement.bindLong(10, n.deleted ? 1 : 0);

                    statement.bindLong(11, n.updated_at);

                    statement.bindLong(12, n.featured ? 1 : 0);
                    statement.bindLong(13, n.published ? 1 : 0);

                    val = n.video;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(14, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(14);
                    //}

                    statement.bindLong(15, n.is_video_hidden ? 1 : 0);

                    statement.execute();
                    statement.clearBindings();
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

	/*
    public static ArrayList<News> getFeatured(ArrayList<News> news)
    {
        ArrayList<News> featured = new ArrayList<>();
        for (News n : news)
        {
            if (n.featured)
            {
                featured.add(n);
            }
        }
        return featured;
    }
	*/

    public static ArrayList<News> getTopNews(SQLiteDatabase db)
    {
        final StringBuilder sb = new StringBuilder(160);
        sb.append(SELECT);
        sb.append(" WHERE DELETED=0 AND PUBLISHED=1");
        sb.append(" ORDER BY DATE DESC LIMIT 10");
        ArrayList<News>  news = readNewses(db, sb.toString());
        if (null != news  && !news.isEmpty())
        {
            for (News n : news)
            {
                n.highlight = false;
                n.promoted = 0;
            }
        }
        return news;
    }

}
