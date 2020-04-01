package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import ru.mycity.data.Category;


public class DbCategoriesHelper
{
    private final static String CATEGORY_SELECT = "SELECT ID, NAME, PIC, PARENT_ID, IMAGE FROM CATEGORIES";
    public final static String TABLE_NAME = "CATEGORIES";

    public static ArrayList<Category> getCategories(SQLiteDatabase db, Category parent)
    {
        StringBuilder sb = new StringBuilder(286);
        //  0   1     2     3
        sb.append("SELECT ID, NAME, PIC, IMAGE");
        //SELECT id, title, site, address, latitude, longitude, type, open_time, info, work_monday, work_tuesday, work_wednesday, work_thursday, work_friday, work_saturday, work_sunday, work_always, highlight, promoted, phones_count, pos FROM ORGANIZATIONS, ORGANIZATION_CATEGORY WHERE CATEGORY_ID
        //ORGANIZATION_CATEGORY ORGANIZATION_ID, CATEGORY_ID
        if (null != parent)
        {
            //sb.append(", (SELECT COUNT(oc.ORGANIZATION_ID) FROM ORGANIZATION_CATEGORY AS oc, ORGANIZATIONS o WHERE oc.CATEGORY_ID=c.id and oc.ORGANIZATION_ID=o.id AND o.DELETED=0 AND oc.DELETED=0 AND o.PUBLISHED=1) AS org_count ");
            //sb.append(", ( (SELECT COUNT(oc.category_id) FROM ORGANIZATION_CATEGORY AS oc WHERE oc.CATEGORY_ID=c.id AND oc.DELETED !=1) + (SELECT COUNT(ID) FROM CATEGORIES cc WHERE cc.DELETED=0 AND cc.PUBLISHED=1 AND cc.PARENT_ID=c.id )) AS child_count ");
            //sb.append(", ( SELECT COUNT(oc.category_id) FROM ORGANIZATION_CATEGORY AS oc WHERE oc.CATEGORY_ID=c.id AND oc.DELETED !=1) AS child_count ");

            //sb.append(", ( SELECT COUNT(oc.category_id) FROM ORGANIZATION_CATEGORY AS oc WHERE (oc.CATEGORY_ID=c.id or oc.CATEGORY_ID in (select cc.id FROM CATEGORIES cc WHERE cc.PARENT_ID=c.id AND cc.DELETED=0 AND cc.PUBLISHED=1)) AND oc.DELETED !=1) AS child_count ");
            sb.append(", ( SELECT COUNT(oc.id) FROM ORGANIZATION_CATEGORY AS oc, ORGANIZATIONS o  WHERE (oc.CATEGORY_ID=c.id or oc.CATEGORY_ID in (select cc.id FROM CATEGORIES cc WHERE cc.PARENT_ID=c.id AND cc.DELETED=0 AND cc.PUBLISHED=1)) AND oc.DELETED !=1 and oc.ORGANIZATION_ID=o.id AND o.DELETED=0 AND oc.DELETED=0 AND o.PUBLISHED=1) AS child_count ");
            //sb.append(", ( (SELECT COUNT(oc.category_id) FROM ORGANIZATION_CATEGORY AS oc , ORGANIZATIONS o WHERE oc.CATEGORY_ID = c.id AND oc.DELETED !=1) + (SELECT COUNT(ID) FROM CATEGORIES cc WHERE cc.DELETED=0 AND cc.PUBLISHED=1 AND cc.PARENT_ID=c.id AND oc.ORGANIZATION_ID=o.id and o.DELETED=0 AND o.PUBLISHED=1 )) AS child_count ");
        }

        sb.append(" FROM CATEGORIES c WHERE PARENT_ID ");
        /*
        if (null == parent)
            sb.append("IS NULL");
        else
            sb.append('=').append(parent.id);
        */
        sb.append('=');
        if (null == parent)
            sb.append('0');
        else
            sb.append(parent.id);

        sb.append(" AND c.DELETED=0 AND c.PUBLISHED=1 ORDER BY POS");

        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Category> result = null;

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                Category category = new Category();
                category.id = cursor.getLong(0);
                if (null != parent)
                    category.parent_id = parent.id;

                category.parent = parent;
                category.name    = cursor.isNull(1) ? null : cursor.getString(1);
                category.picture = cursor.isNull(2) ? null : cursor.getString(2);
                category.image   = cursor.isNull(3) ? null : cursor.getString(3);

                if (null != parent)
                    category.childCount = cursor.getInt(4);
                result.add(category);
            }
            cursor.close();
        }
        return result;
    }


    public static ArrayList<String> getCategoriesIds(SQLiteDatabase db, long parentId)
    {
        StringBuilder sb = new StringBuilder(90);
        sb.append("SELECT ID FROM CATEGORIES c WHERE PARENT_ID=");
        sb.append(parentId);
        sb.append(" AND DELETED !=1 AND PUBLISHED=1");

        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> result = null;
        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                result.add(cursor.getString(0));
            }
            cursor.close();
        }
        return result;
    }


    public static ArrayList<Category> findCategories(SQLiteDatabase db, String filter)
    {
        StringBuilder sb = new StringBuilder(239 + filter.length() + 16);

        //  0   1     2      3       4      5          6
        sb.append("SELECT ID, NAME, PIC, PARENT_ID, IMAGE ");
        //sb.append(", (SELECT COUNT(oc.ORGANIZATION_ID) FROM ORGANIZATION_CATEGORY AS oc WHERE oc.CATEGORY_ID = c.id AND c.parent_id > 0) AS child_count ");
        sb.append(", ( SELECT COUNT(oc.id) FROM ORGANIZATION_CATEGORY AS oc, ORGANIZATIONS o  WHERE (oc.CATEGORY_ID=c.id or oc.CATEGORY_ID in (select cc.id FROM CATEGORIES cc WHERE cc.PARENT_ID=c.id AND cc.DELETED=0 AND cc.PUBLISHED=1)) AND oc.DELETED !=1 and oc.ORGANIZATION_ID=o.id AND o.DELETED=0 AND oc.DELETED=0 AND o.PUBLISHED=1 AND c.parent_id > 0) AS child_count ");

        sb.append(" FROM CATEGORIES C WHERE NAME_LOW LIKE '%").append(filter.toLowerCase());
        sb.append("%' AND C.DELETED !=1 AND PUBLISHED=1 ORDER BY PARENT_ID");

        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Category> result = null;

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                Category category = new Category();
                category.id = cursor.getLong(0);

                category.name = cursor.isNull(1) ? null : cursor.getString(1);
                category.picture = cursor.isNull(2) ? null : cursor.getString(2);
                category.parent_id = cursor.isNull(3) ? null : cursor.getLong(3);
                category.image = cursor.isNull(4) ? null : cursor.getString(4);
                category.childCount = cursor.getInt(5);
                result.add(category);
            }
            cursor.close();
        }
        return result;
    }


    public static void deleteCategories(SQLiteDatabase db)
    {
        db.execSQL("delete from CATEGORIES");
    }

    public static boolean insertCategories(SQLiteDatabase db, ArrayList<Category> categories, Long parent, boolean bDelete)
    {
        boolean result = false;
        SQLiteStatement statement = null;
        db.beginTransaction();
        try
        {
            if (bDelete)
            {
                if (null == parent)
                    db.execSQL("delete from CATEGORIES");
                else
                    db.execSQL("delete from CATEGORIES where PARENT_ID=" + parent);
            }
            if (null != categories)
            {
                statement = db.compileStatement("INSERT OR REPLACE INTO CATEGORIES(ID, PARENT_ID, NAME, PIC, POS, DELETED, NAME_LOW, PUBLISHED, UPDATED_AT, IMAGE) values(?,?,?,?,?,?,?,?,?,?)");

                for (Category category : categories)
                {
                    statement.bindLong(1, category.id);
                    statement.bindLong(2, category.parent_id);

                    final String name = category.name;
                    if (null != name && 0 != name.length())
                    {
                        statement.bindString(3, name);
                        statement.bindString(7, name.toLowerCase());
                    }
                    else
                    {
                        statement.bindNull(3);
                        statement.bindNull(7);
                    }

                    String pic = category.picture;
                    if (null != pic && 0 != pic.length())
                        statement.bindString(4, pic);
                    else
                        statement.bindNull(4);

                    statement.bindLong(5, category.position);
                    statement.bindLong(6, category.deleted ? 1 : 0);
                    statement.bindLong(8, category.published ? 1 : 0);

                    statement.bindLong(9, category.updated_at);

                    pic = category.image;
                    if (null != pic && 0 != pic.length())
                        statement.bindString(10, pic);
                    else
                        statement.bindNull(10);

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

    public static Category getCategory(SQLiteDatabase db, Category parent, long id)
    {
        StringBuilder sb = new StringBuilder(44);
        sb.append(CATEGORY_SELECT);
        sb.append(" WHERE ID=");
        sb.append(id);
        sb.append(" LIMIT 1");
        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        Category category = null;
        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                category = readCategory(cursor, parent);
                break;
            }
            cursor.close();
        }
        return category;
    }


    private static Category readCategory(Cursor cursor, Category parent)
    {
        final Category category = new Category();
        category.id = cursor.getLong(0);
        category.parent = parent;
        category.name = cursor.isNull(1) ? null : cursor.getString(1);
        category.picture = cursor.isNull(2) ? null : cursor.getString(2);

        if (null != parent)
        {
            category.parent_id = parent.id;
        }
        else
        {
            category.parent_id = cursor.isNull(3) ? 0 : cursor.getInt(3);
        }

        category.image = cursor.isNull(4) ? null : cursor.getString(4);
        return category;
    }

    public static Category getOrganizationCategory(SQLiteDatabase db, long organizationId)
    {
        StringBuilder sb = new StringBuilder(110);
        sb.append(CATEGORY_SELECT);
        sb.append(" WHERE ID=");
        sb.append("(SELECT CATEGORY_ID FROM ORGANIZATION_CATEGORY WHERE ORGANIZATION_ID=");
        sb.append(organizationId);
        sb.append(" LIMIT 1) LIMIT 1");
        String sql = sb.toString();
        Cursor cursor = db.rawQuery(sql, null);
        Category category = null;
        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                category = readCategory(cursor, null);
                break;
            }
            cursor.close();
        }
        return category;
    }


}
