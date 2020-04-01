package ru.mycity.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import ru.mycity.data.OrganizationCategory;


public class DbOrganizationCategoriesHelper
{
    public static String TABLE_NAME = "ORGANIZATION_CATEGORY";


    //public static void deleteOrganizationCategories(SQLiteDatabase db)
    //{
    //    db.execSQL("delete from ORGANIZATION_CATEGORY");
    //}


    public static boolean insertOrganizationCategories(SQLiteDatabase db, ArrayList<OrganizationCategory> organization_categories, boolean bulkInsert)
    {
        boolean result = false;

        SQLiteStatement statement = null;

        //if (bulkInsert)
        //    DbDataHelper.setBulkMode(db);
        db.beginTransaction();
        try
        {
            if (null != organization_categories)
            {                                                                            //   1        2              3         4      5           6
                statement = db.compileStatement("INSERT OR REPLACE INTO ORGANIZATION_CATEGORY(ID, CATEGORY_ID, ORGANIZATION_ID, POS, UPDATED_AT, DELETED) values(?,?,?,?,?,?)");

                for (OrganizationCategory obj : organization_categories)
                {

                    //if (obj.organizationId == 76267 || obj.organizationId == 74326 ||
                    //        obj.organizationId == 77598)
                    //    obj.deleted = false;

                    statement.bindLong(1, obj.id);
                    statement.bindLong(2, obj.categoryId);
                    statement.bindLong(3, obj.organizationId);
                    statement.bindLong(4, obj.pos);
                    statement.bindLong(5, obj.updatedAt);
                    statement.bindLong(6, obj.deleted ? 1 : 0);

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
            //if (bulkInsert)
            //    DbDataHelper.setNormalMode(db);
        }
        return result;
    }

}
