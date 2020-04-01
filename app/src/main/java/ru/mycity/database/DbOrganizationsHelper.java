package ru.mycity.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.data.OrganizationPhone;
import ru.utils._DBHelper;

public class DbOrganizationsHelper
{
    public static String TABLE = "ORGANIZATIONS";

    public final static int PAGE_SIZE = 25;

    public static ArrayList<Organization> findOrganizations(SQLiteDatabase db, String filter, ArrayList<Category> list, long categoryId, int limit, long offset)
    {
        int len = 336 + filter.length();
        if (null != list && !list.isEmpty())
        {
            len += 94 + list.size() * 7;
        }
        StringBuilder sb = new StringBuilder(len);
        sb.append("SELECT ");
        sb.append("id, title, site, address, latitude, longitude, type, open_time, info, work_monday, work_tuesday, work_wednesday, work_thursday, work_friday, work_saturday, work_sunday, work_always, highlight, promoted, phones_count, pos, has_coordinates, image");
        sb.append(" FROM ORGANIZATIONS WHERE ");
        if (null != list && !list.isEmpty() && 0 != categoryId)
        {
            sb.append(" ID in (SELECT ORGANIZATION_ID FROM ORGANIZATION_CATEGORY WHERE CATEGORY_ID IN (");
            sb.append(categoryId).append(',');
            for (Category c : list)
            {
                sb.append(c.id).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
            sb.append(") AND ");
        }
        else
        {
            if (0 != categoryId)
            {
                sb.append(" ID in (SELECT ORGANIZATION_ID FROM ORGANIZATION_CATEGORY WHERE CATEGORY_ID=")
                        .append(categoryId).append(')');
                sb.append(" AND ");
            }
            }

        sb.append("TITLE_LOW like '%");
        sb.append(filter.toLowerCase());
        sb.append('%');
        sb.append('\'');

        sb.append(" AND DELETED !=1 AND PUBLISHED=1");

        if (0 != limit)
            sb.append(" LIMIT ").append(limit);
        if (0 != offset)
            sb.append(" OFFSET ").append(offset);

        String sql = sb.toString();
        return getOrganizations(db, sql);
    }

    public static ArrayList<Organization> getOrganizationsByParentCategoryWithLocation(SQLiteDatabase db, ArrayList<Category> categories,  String filter, long categoryParentId)
    {
        StringBuilder sb = new StringBuilder(800);
        String recursiveCategoriesTableName = null;

        if (categoryParentId == 0 && null == categories)
            ;
        else
        {
            if (Build.VERSION.SDK_INT >= 21) // LOLLIPOP
            {
                //CTEs are available only in SQLite 3.8.3 or later, which is available only in Android 5.0
                recursiveCategoriesTableName = getCTECategoriesRecursiveQuery(categoryParentId, sb);
            }
        }

        sb.append(" SELECT ");
        sb.append("o.id, title, site, address, latitude, longitude, type, open_time, info, work_monday, work_tuesday, work_wednesday, work_thursday, work_friday, work_saturday, work_sunday, work_always, highlight, promoted, phones_count, 1 as pos, has_coordinates, image");
        //sb.append(" FROM ORGANIZATIONS o, ORGANIZATION_CATEGORY oc WHERE latitude is not null and longitude is not null ");
        sb.append(" FROM ORGANIZATIONS o WHERE has_coordinates=1");
        sb.append(" and o.DELETED!=1 AND o.PUBLISHED=1");
        sb.append(" and o.ID in (SELECT ORGANIZATION_ID FROM ORGANIZATION_CATEGORY oc WHERE oc.DELETED!=1 ");
        if (categoryParentId == 0 && null == categories)
            ;
        else
            addCategoriesRecursiveCondition(db, categories, categoryParentId, sb, recursiveCategoriesTableName, true);
        sb.append(')');

        if (null != filter && 0 != filter.length())
        {
            sb.append(" AND TITLE_LOW like '%");
            sb.append(filter.toLowerCase());
            sb.append('%');
            sb.append('\'');
        }

        String sql = sb.toString();
        return getOrganizations(db, sql);
    }

    private static void addCategoriesRecursiveCondition(SQLiteDatabase db, ArrayList<Category> categories, long categoryParentId, StringBuilder sb, String recursiveCategoriesTableName, boolean includeParent)
    {
        if (Build.VERSION.SDK_INT < 21) // PRE LOLLIPOP
        {
            if (categoryParentId > 0 || null != categories)
            {
                StringBuilder ids = getCategoryIdsList(db, categoryParentId, categories, includeParent);

                if (null != ids && 0 != ids.length())
                {
                    sb.append(" AND (CATEGORY_ID IN ");
                    sb.append(ids);
                    sb.append(" OR CATEGORY_ID IN ");
                    sb.append("(SELECT id from CATEGORIES WHERE ");
                    sb.append("DELETED !=1 AND PUBLISHED=1 AND PARENT_ID IN ");
                    ids.setCharAt(ids.length() - 1, ',');
                    ids.append(categoryParentId);
                    ids.append(')');
                    sb.append(ids);
                    sb.append(')');
                    sb.append(')');
                    //Only 3 level for non Lollipop.
                    //TODO  Make unlimited deep
                }
                else
                {
                    sb.append(" AND CATEGORY_ID IN (SELECT id from CATEGORIES WHERE ");
                    sb.append("DELETED !=1 AND PUBLISHED=1 AND PARENT_ID=");
                    sb.append(categoryParentId);
                    sb.append(')');
                }
            }
        }
        else
        {
            sb.append(" AND CATEGORY_ID IN (SELECT id FROM ").append(recursiveCategoriesTableName).append(')');
        }
    }


    private static String getCTECategoriesRecursiveQuery(long categoryParentId, StringBuilder sb)
    {
        /*
        sb.append("WITH RECURSIVE cats(ID) AS (VALUES (");
        sb.append(categoryParentId).append(')');
        sb.append(" UNION ALL SELECT c.ID FROM categories AS c ");
        sb.append("JOIN cats ON c.PARENT_ID=cats.ID WHERE c.DELETED=0 AND c.PUBLISHED=1 ");
        */

        sb.append("WITH RECURSIVE cats(ID) AS (SELECT ");
        sb.append(categoryParentId);
        sb.append(" UNION ALL SELECT c.ID FROM categories AS c ");
        sb.append("JOIN cats ON c.PARENT_ID=cats.ID) "); // WHERE c.DELETED=0 AND c.PUBLISHED=1 ");

        return "cats";
        //http://stackoverflow.com/questions/22086892/android-sqlite-database-recursive-query
    }

    private static ArrayList<Organization> getOrganizations(SQLiteDatabase db, String sql)
    {
        ArrayList<Organization> result = null;
        Cursor cursor = db.rawQuery(sql, null);

        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                Organization organization = readOrganization(cursor);
                result.add(organization);
            }
            cursor.close();
        }
        return result;
    }

    private static Organization readOrganization(Cursor cursor)
    {
        Organization organization = new Organization();
        organization.id = cursor.getLong(0);
        organization.title = cursor.isNull(1) ? null : cursor.getString(1);
        organization.site = cursor.isNull(2) ? null : cursor.getString(2);
        organization.address = cursor.isNull(3) ? null : cursor.getString(3);
        organization.hasCoordinates = 1 == cursor.getInt(21);
        if (organization.hasCoordinates)
        {
            organization.latitude = cursor.getInt(4);
            organization.longitude = cursor.getInt(5);
        }
        organization.type = cursor.isNull(6) ? null : cursor.getString(6);
        organization.open_time = cursor.isNull(7) ? null : cursor.getString(7);
        organization.info = cursor.isNull(8) ? null : cursor.getString(8);
        organization.work_monday = cursor.isNull(9) ? null : cursor.getString(9);
        organization.work_tuesday = cursor.isNull(10) ? null : cursor.getString(10);
        organization.work_wednesday = cursor.isNull(11) ? null : cursor.getString(11);
        organization.work_thursday = cursor.isNull(12) ? null : cursor.getString(12);
        organization.work_friday = cursor.isNull(13) ? null : cursor.getString(13);
        organization.work_saturday = cursor.isNull(14) ? null : cursor.getString(14);
        organization.work_sunday = cursor.isNull(15) ? null : cursor.getString(15);
        organization.work_always = cursor.isNull(16) ? false : 1 == cursor.getInt(16);
        organization.highlight = cursor.isNull(17) ? false : 1 == cursor.getInt(17);
        organization.promoted = cursor.isNull(18) ? 0 : cursor.getInt(18);
        organization.phones_count = cursor.isNull(19) ? null : cursor.getInt(19);
        organization.pos = cursor.isNull(20) ? 0 : cursor.getInt(20);
        organization.image = cursor.isNull(22) ? null : cursor.getString(22);

        return organization;
    }

    public static long getOrganizationsCount(SQLiteDatabase db, long categoryId)
    {
        return _DBHelper.simpleQueryForLong(db, "SELECT COUNT(*) FROM ORGANIZATIONS WHERE DELETED!=1 AND PUBLISHED=1 AND CATEGORY_ID=" + categoryId  , 0);
    }

    public static ArrayList<Organization> getOrganizations(SQLiteDatabase db, long category, int limit, long offset)
    {
        StringBuilder sb = new StringBuilder(462);
        sb.append("SELECT ");
        sb.append("o.id, title, site, address, latitude, longitude, type, open_time, info, work_monday, work_tuesday, work_wednesday, work_thursday, work_friday, work_saturday, work_sunday, work_always, highlight, promoted, phones_count, oc.pos, has_coordinates, image");
        sb.append(" FROM ORGANIZATIONS o, ORGANIZATION_CATEGORY oc WHERE ");
        if (category > 0)
        {
            sb.append("oc.CATEGORY_ID=");
            sb.append(category);
            sb.append(" AND ");
        }
        //sb.append(" AND oc.ORGANIZATION_ID = o.id AND o.DELETED!=1 AND oc.DELETED!=1 AND o.PUBLISHED=1 ORDER BY promoted desc, oc.pos asc");
        sb.append("(oc.ORGANIZATION_ID=o.id) AND (o.DELETED!=1) AND (oc.DELETED!=1) AND (o.PUBLISHED=1) ORDER BY o.promoted desc, oc.pos asc");

        if (0 != limit)
            sb.append(" LIMIT ").append(limit);
        if (0 != offset)
            sb.append(" OFFSET ").append(offset);

        String sql = sb.toString();
        return getOrganizations(db, sql);
    }

    /*
    public static void deleteOrganizations(SQLiteDatabase db)
    {
        db.execSQL("delete from ORGANIZATIONS");
        //db.execSQL("delete from ORGANIZATION_CATEGORY");
        db.execSQL("delete from ORGANIZATION_PHONE");
    }
    */

    public static boolean insertOrganizations(SQLiteDatabase db, ArrayList<Organization> organizations
                                              //, ArrayList<OrganizationCategory> organizationCategories
            ,boolean checkDeleted, boolean bulkInsert
    )
    {
        boolean result = false;
        SQLiteStatement statement = null;
        //SQLiteStatement statement_oc = null;
        SQLiteStatement statement_op = null;
        StringBuilder deleted = null;
        //long unixTime = System.currentTimeMillis() / 1000L;
        //final long temp_updated_at = unixTime;
        final long temp_updated_at = System.currentTimeMillis();

        //if (bulkInsert)
        //    DbDataHelper.setBulkMode(db);

        db.beginTransaction();
        try
        {
            if (null != organizations)
            {                                                                        //  1    2     3       4       5           6       7        8       9       10            11          12               13            14            15           16            17           18       19          20          21        22             23        24     25
                statement = db.compileStatement("INSERT OR REPLACE INTO ORGANIZATIONS(id,title, site, address, latitude, longitude, type, open_time, info, work_monday, work_tuesday, work_wednesday, work_thursday, work_friday, work_saturday, work_sunday, work_always, highlight, promoted, phones_count, pos, has_coordinates, updated_at, deleted, title_low,PUBLISHED,image,VIDEO,IS_VIDEO_HIDDEN) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                //statement_oc = db.compileStatement("INSERT OR REPLACE INTO ORGANIZATION_CATEGORY(ORGANIZATION_ID, CATEGORY_ID) values(?,?)");

                /*
                if (null != organizationCategories && !organizationCategories.isEmpty())
                {
                    for (OrganizationCategory oc : organizationCategories)
                    {
                        statement_oc.bindLong(1, oc.organizationId);
                        statement_oc.bindLong(2, oc.categoryId);
                        statement_oc.execute();
                    }
                }
                */

                int size = organizations.size();
                if (checkDeleted && size > 0)
                {
                    deleted = new StringBuilder(110 + size * 7);
                    deleted.append("delete from ORGANIZATION_PHONE where ORGANIZATION_ID IN (");

                }
                for (Organization organization : organizations)
                {
                    if (null != deleted)
                        deleted.append(organization.id).append(',');

                    statement.bindLong(1, organization.id);

                    String val = organization.title;
                    if (null != val && 0 != val.length())
                        statement.bindString(2, val);
                    //else
                    //    statement.bindNull(2);

                    val = organization.site;
                    if (null != val && 0 != val.length())
                        statement.bindString(3, val);
                    //else
                    //    statement.bindNull(3);

                    val = organization.address;
                    if (null != val && 0 != val.length())
                        statement.bindString(4, val);
                    //else
                    //    statement.bindNull(4);

                    statement.bindLong(5, organization.latitude);
                    statement.bindLong(6, organization.longitude);

                    val = organization.type;
                    if (null != val && 0 != val.length())
                        statement.bindString(7, val);
                    //else
                    //    statement.bindNull(7);

                    val = organization.open_time;
                    if (null != val && 0 != val.length())
                        statement.bindString(8, val);
                    //else
                    //    statement.bindNull(8);

                    val = organization.info;
                    if (null != val && 0 != val.length())
                        statement.bindString(9, val);
                    //else
                    //    statement.bindNull(9);

                    val = organization.work_monday;
                    if (null != val && 0 != val.length())
                        statement.bindString(10, val);
                    //else
                    //    statement.bindNull(10);

                    val = organization.work_tuesday;
                    if (null != val && 0 != val.length())
                        statement.bindString(11, val);
                    //else
                    //    statement.bindNull(11);

                    val = organization.work_wednesday;
                    if (null != val && 0 != val.length())
                        statement.bindString(12, val);
                    //else
                    //    statement.bindNull(12);

                    val = organization.work_thursday;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(13, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(13);
                    //}

                    val = organization.work_friday;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(14, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(14);
                    //}

                    val = organization.work_saturday;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(15, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(15);
                    //}

                    val = organization.work_sunday;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(16, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(16);
                    //}

                    statement.bindLong(17, organization.work_always ? 1 : 0);
                    statement.bindLong(18, organization.highlight ? 1 : 0);
                    statement.bindLong(19, organization.promoted);

                    statement.bindLong(20, organization.phones_count);
                    statement.bindLong(21, organization.pos);
                    statement.bindLong(22, organization.hasCoordinates ? 1 : 0);
                    statement.bindLong(23, organization.updatedAt);
                    statement.bindLong(24, organization.deleted ? 1 : 0);

                    val = organization.title;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(25, val.toLowerCase());
                    }
                    //else
                    //{
                    //    statement.bindNull(25);
                    //}

                    statement.bindLong(26, organization.published ? 1 : 0);

                    val = organization.image;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(27, val);
                    }
                    //else
                    //{
                    //    statement.bindNull(27);
                   // }

                    val = organization.video;
                    if (null != val && 0 != val.length())
                    {
                        statement.bindString(28, val);
                    }

                    statement.bindLong(29, organization.is_video_hidden ? 1 : 0);

                    statement.execute();
                    statement.clearBindings();
                }

                statement_op = db.compileStatement("INSERT OR REPLACE INTO ORGANIZATION_PHONE(ORGANIZATION_ID, PHONE, DESCRIPTION, UPDATED_AT) values(?,?,?,?)");
                for (Organization organization : organizations)
                {
                    List<OrganizationPhone> phones = organization.phones;
                    if (null != phones && !phones.isEmpty())
                    {
                        for (OrganizationPhone phone : phones)
                        {
                            statement_op.bindLong  (1, organization.id);
                            statement_op.bindString(2, phone.phone);

                            String val = phone.description;
                            if (null != val && 0 != val.length())
                            {
                                statement_op.bindString(3, val);
                            }
                            else
                            {
                                statement_op.bindNull(3);
                            }
                            statement_op.bindLong(4, temp_updated_at);
                            statement_op.execute();
                        }
                    }
                }

                /*
                if (null != deleted)
                {
                    deleted.setCharAt(deleted.length() - 1, ')');
                    deleted.append( " AND (UPDATED_AT <>").append(temp_updated_at).append(')');
                    db.execSQL(deleted.toString());
                }
                */
            }

            db.setTransactionSuccessful();
            if (null != deleted)
            {
                deleted.setCharAt(deleted.length() - 1, ')');
                deleted.append( " AND ((UPDATED_AT is NULL) OR UPDATED_AT <>").append(temp_updated_at).append(')');
                db.execSQL(deleted.toString());
            }
            result = true;
        }
        catch (Throwable e)
        {
            System.err.print(e.toString());
        }
        finally
        {
            if (null != statement)
                statement.close();

            //if (null != statement_oc)
            //    statement_oc.close();

            if (null != statement_op)
            {
                statement_op.close();
            }
            DBHelper.endTransaction(db);
            //if (bulkInsert)
            //    DbDataHelper.setNormalMode(db);
        }
        return result;
    }


    @Nullable
    private static StringBuilder getCategoryIdsList(SQLiteDatabase db, long categoryParentId, ArrayList<Category> cats, boolean includeParent)
    {
        StringBuilder ids = null;
        if (null == cats)
        {
            ArrayList<String> vals = DbCategoriesHelper.getCategoriesIds(db, categoryParentId);
            if (null != vals && !vals.isEmpty())
            {
                ids = new StringBuilder((null != vals) ? vals.size() * 6 : 10);
                ids.append('(');
                for (String v : vals)
                {
                    ids.append(v).append(',');
                }
            }
        }
        else
        {
            if (!cats.isEmpty())
            {
                ids = new StringBuilder((null != cats) ? cats.size() * 6 : 10);
                ids.append('(');
                for (Category c : cats)
                {
                    ids.append(c.id).append(',');
                }
            }
        }
        if (includeParent)
        {
            if (null == ids)
            {
                ids = new StringBuilder();
                ids.append('(');
            }
            ids.append(categoryParentId).append(')');
        }
        else
        {
            ids.setCharAt(ids.length() - 1, ')');
        }
        return ids;
    }


    public static Organization getOrganizationById(SQLiteDatabase db, long id)
    {
        StringBuilder sb = new StringBuilder(270);
        sb.append("SELECT ");
        sb.append("id, title, site, address, latitude, longitude, type, open_time, info, work_monday, work_tuesday, work_wednesday, work_thursday, work_friday, work_saturday, work_sunday, work_always, highlight, promoted, phones_count, pos, has_coordinates, image");
        sb.append(" FROM ORGANIZATIONS o WHERE ID=");
        sb.append(id);

        String sql = sb.toString();

        Cursor cursor = db.rawQuery(sql, null);

        Organization organization = null;
        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                organization = readOrganization(cursor);
                break;

            }
            cursor.close();
        }
        if (null != organization)
        {
            organization.phones = getOrganizationPhones(db, id);
        }
        return organization;
    }

    public static ArrayList<OrganizationPhone> getOrganizationPhones(SQLiteDatabase db, long organizationId)
    {
        ArrayList<OrganizationPhone> result = null;
        final String sql = "SELECT PHONE, DESCRIPTION FROM ORGANIZATION_PHONE WHERE ORGANIZATION_ID=" + organizationId + " order by _id";
        Cursor cursor = db.rawQuery(sql, null);
        if (null != cursor)
        {
            result = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext())
            {
                OrganizationPhone obj = new OrganizationPhone();
                obj.phone = cursor.getString(0);
                obj.description = cursor.isNull(1) ? null : cursor.getString(1);
                result.add(obj);
            }
            cursor.close();
        }
        return result;
    }
}

