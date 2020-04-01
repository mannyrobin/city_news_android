package ru.mycity.tasks;

import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import ru.mycity.AppData;
import ru.mycity.PushTableNames;
import ru.mycity._Application;
import ru.mycity.data.Action;
import ru.mycity.data.Button;
import ru.mycity.data.Category;
import ru.mycity.data.Event;
import ru.mycity.data.News;
import ru.mycity.data.PushData;
import ru.mycity.data.RootData;
import ru.mycity.database.DBHelper;
import ru.mycity.database.DbActionsHelper;
import ru.mycity.database.DbButtonsHelper;
import ru.mycity.database.DbCategoriesHelper;
import ru.mycity.database.DbDataHelper;
import ru.mycity.database.DbEventsHelper;
import ru.mycity.database.DbNewsHelper;
import ru.mycity.database.DbOrganizationCategoriesHelper;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.DateUtils;
import ru.utils.NetworkUtils;
import ru.utils._DBHelper;
import ru.utils._DateUtils;

//import android.util.Log;


public class StartApplicationTask extends UpdateTask
{
    /*
    private final static String buttons = "buttons";
    private final static String categories = "categories";
    private final static String events = "events";
    private final static String news = "news";
    private final static String organizations = "organizations";
    private final static String category_organization = "category_organization";
    private final static String actions = "actions";
    private final static String options = "options";
    private final static String about  = "about";
    */

    //private String errorDescription;
    //private AssetManager assetManager = null;
    private PushData pushData;
    //private String [] assetsList = null;
    private long startTime;
    private long maxTime;
    private long postUpdateMode = 0;

    /*
    private long insertOrganizationTime;
    private long insertOrganizationCategoryTime;
    private long orgParseTime;
    private long orCatParseTime;
    */

    //private boolean needLoadOrganizationsInBackground;

    public StartApplicationTask(_Application application, PushData pushData, int rc, IResultCallback resultCallback)
    {
        super(application, rc, resultCallback);
        this.pushData = pushData;
    }

    /*
    private Object readAssetEntityPart(AssetManager am, IParser parser, String assetName, String entityName)
    {
        InputStream inputStream = null;
        try
        {
            inputStream = am.open(assetName);

            long time = System.currentTimeMillis();
            Object data = parser.parse(inputStream);


            if (null != data)
            {
                //time = System.currentTimeMillis();
                onReadAsset(entityName, data);
                //if (assetName.startsWith(organizations))
                //    orgParseInsertTime+=(System.currentTimeMillis() - time);
            }
            return  data;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != inputStream)
               IoUtils.closeSilently(inputStream);
        }
        return null;
    }



    private void onReadAsset(String entityName, Object data)
    {
        int l = entityName.length();
        if (l == buttons.length() && buttons.regionMatches(0, entityName, 0, l))
        {
            DbButtonsHelper.insertButtons(application.getDbHelper().getWritableDatabase(), (ArrayList<Button>) data);
            return;
        }
        if (l == categories.length() && categories.regionMatches(0, entityName, 0, l))
        {
            DbCategoriesHelper.insertCategories(application.getDbHelper().getWritableDatabase(), (ArrayList<Category>) data, null, false);
            return;
        }

        if (l == organizations.length() && organizations.regionMatches(0, entityName, 0, l))
        {
            if (null != data)
            {
                OrganizationsParser.Result res = (OrganizationsParser.Result) data;
                //long s = System.currentTimeMillis();
                DbOrganizationsHelper.insertOrganizations(application.getDbHelper().getWritableDatabase(), res.organizations
                        //, res.organizationCategories
                        ,false
                        , true
                        );
                //insertOrganizationTime += (System.currentTimeMillis() - s);
            }
            return;
        }

        if (l == events.length() && events.regionMatches(0, entityName, 0, l))
        {
            DbEventsHelper.insertEvents(application.getDbHelper().getWritableDatabase(), (List<Event>) data);
            return;
        }

        if (l == actions.length() && actions.regionMatches(0, entityName, 0, l))
        {
            DbActionsHelper.insertActions(application.getDbHelper().getWritableDatabase(), (List<Action>) data);
            return;
        }

        if (l == news.length() && news.regionMatches(0, entityName, 0, l))
        {
            if (null != data)
                DbNewsHelper.insert(application.getDbHelper().getWritableDatabase(), (List<News>) data);
            return;
        }

        if (l == category_organization.length() && category_organization.regionMatches(0, entityName, 0, l))
        {
            ArrayList<OrganizationCategory> organization_categories = (ArrayList<OrganizationCategory>) data;
            //long s = System.currentTimeMillis();
            DbOrganizationCategoriesHelper.insertOrganizationCategories(application.getDbHelper().getWritableDatabase(), organization_categories, true);
            //insertOrganizationCategoryTime += (System.currentTimeMillis() - s);
            return;
        }

        if (l == options.length() && options.regionMatches(0, entityName, 0, l))
        {
            DbOptionsHelper.insert(application.getDbHelper().getWritableDatabase(), (ArrayList<Option>) data);
            return;
        }

        if (l == about.length() && about.regionMatches(0, entityName, 0, l))
        {
            DbAboutHelper.insert(application.getDbHelper().getWritableDatabase(), (About) data);
            return;
        }
    }
*/



    private boolean readData(RootData rootData, boolean initRead)
    {
        //if (initRead)
        //    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        maxTime = initRead ? DateUtils.MILLIS_PER_SECOND * 18 : DateUtils.MILLIS_PER_SECOND * 12;
        //maxTime /= 1000;
        rootData.pushData = pushData;

        /*
        if (BuildConfig.DEBUG)
        {
            Log.d("0c0", "0c0 before" + " organizations count = " + DbDataHelper.getCount(application.getDbHelper().getReadableDatabase(), DbOrganizationsHelper.TABLE));
            Log.d("0c0", "0c0 before" + " organization category count = " + DbDataHelper.getCount(application.getDbHelper().getReadableDatabase(), DbOrganizationCategoriesHelper.TABLE_NAME));
        }*/

        networkConnected = NetworkUtils.isNetworkConnected(application);
        //test();
        rootData.buttons = readButtons(initRead);
        rootData.categories = readCategories(initRead);
        readOrganizations(initRead);
        readOrganizationCategories(initRead);
        rootData.events = readEvents(initRead);

        rootData.actions = readActions(initRead);
        rootData.news = readNews(initRead);
        //rootData.featuredNews = DbNewsHelper.get(application.getDbHelper().getReadableDatabase(), true, 0, 0);
        rootData.topNews = DbNewsHelper.getTopNews(application.getDbHelper().getReadableDatabase());

        readOptions(initRead);
        readAbout(initRead);

        /*
        if (BuildConfig.DEBUG)
        {
            Log.d("0c0", "0c0 after" + " organizations count = " + DbDataHelper.getCount(application.getDbHelper().getReadableDatabase(), DbOrganizationsHelper.TABLE));
            Log.d("0c0", "0c0 after" + " organization category count = " + DbDataHelper.getCount(application.getDbHelper().getReadableDatabase(), DbOrganizationCategoriesHelper.TABLE_NAME));
        }
        */

        //readFile(initRead, "about", "about_1.dat",    "about.dat");
        //readFile(initRead, "options","options_1.dat", "options.dat");

        //rootData.options = DataUtils.readJSONArray(application, "options_1.dat", "options.dat");

        //Organization o = DbDataHelper.getOrganizationById(application.getDbHelper().getReadableDatabase(), 76267);
        //if (null == o)
        //    return false;
        return true;
    }


    private ArrayList<News> readNews(boolean initRead)
    {
        final long time = _DateUtils.getTruncatedCurrentTime();

        //final int deleteCount = 60;
        final int deleteCount = 31;
        _DBHelper.execSQL(application.getDbHelper().getWritableDatabase(),"delete from news where date < "  + (time - deleteCount * DateUtils.MILLIS_PER_DAY)
                //+ " AND (FEATURED=0 OR DELETED=1)"
                        + " AND DELETED=1"
                );

        long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbNewsHelper.TABLE_NAME);
        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbNewsHelper.TABLE_NAME, true, 4);

        boolean bRead = false;
        if (networkConnected)
        {
            if (initRead)
               loadNews(maxUpdatedAt, 10);

//            final long diff = maxTime - (System.currentTimeMillis() - startTime);
//            if (diff > 0)
//            {
//                bRead = (loadNews(maxUpdatedAt, initRead ? 50 : 60) >= 0);
//            }
            bRead = (loadNews(maxUpdatedAt, initRead ? 70 : 60) >= 0);
        }

        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_NEWS;

        //delete duplicates
        //DBHelper.executeUpdateDelete(application.getDbHelper().getWritableDatabase(),"delete from news where rowid not in (select max(rowid) from news group by TITLE)");

        ArrayList<News> news = DbNewsHelper.get(application.getDbHelper().getReadableDatabase(), false,
                //initRead ? 3 * DbNewsHelper.PAGE_SIZE : DbNewsHelper.PAGE_SIZE
                DbNewsHelper.PAGE_SIZE, 0);
        if (null == news)
            news = new ArrayList<>();
        return news;
    }

    private ArrayList<Button> readButtons(boolean initRead)
    {
        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbButtonsHelper.TABLE_NAME);
        boolean bRead = false;
        if (networkConnected)
        {
            //long maxUpdatedAt = 1460537930;
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            if (diff > 0)
                bRead = (loadButtons(maxUpdatedAt) >= 0);
        }

        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_BUTTONS;

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbButtonsHelper.TABLE_NAME, true, 9);

        //long d = _DBHelper.simpleQueryForLong(application.getDbHelper().getReadableDatabase(), "select count(*) from ORGANIZATION_CATEGORY where category_id=264",0);
        //if (d == -1)
        //    return null;

        ArrayList<Button> buttons = DbButtonsHelper.getButtons(application.getDbHelper().getReadableDatabase());
        if (null != buttons && !buttons.isEmpty())
            return buttons;

        if (null != buttons)
            buttons = new ArrayList<>();

        return buttons;
    }

    private ArrayList<Category> readCategories(boolean initRead)
    {
        //if (initRead)
        //    DbCategoriesHelper.deleteCategories(application.getDbHelper().getWritableDatabase());

        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbCategoriesHelper.TABLE_NAME);
        boolean bRead = false;
        if (networkConnected)
        {
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            if (diff > 0)
                bRead = (loadCategories(maxUpdatedAt) >= 0);
        }

        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_CATEGORIES;

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbCategoriesHelper.TABLE_NAME, true, 4);
        return readCategories();
    }

    private void readOrganizations(boolean initRead)
    {
        /*
        if (initRead)
        {
            DbOrganizationsHelper.deleteOrganizations(application.getDbHelper().getWritableDatabase());
            OrganizationsParser parser = new OrganizationsParser();
            parser.setSkipDeleted(true);
            parser.setDefaultObjectCount(500);
        }
        */

        boolean bRead = false;
        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbOrganizationsHelper.TABLE);

        if (networkConnected)
        {
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            //Log.d("0c0", "0c0 " + " organizations diff = " + diff);
            if (diff > 0)
                bRead = loadOrganizations(maxUpdatedAt, diff);
        }

        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_ORGANIZATIONS;

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbOrganizationsHelper.TABLE, true, 5);
    }


    /*
    private int loadOrganizationsPart()
    {
        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), "ORGANIZATIONS");
        final HttpClient.Result result = executeGet(AppUrls.API_URL +
                                    //addInfiniteRead("organizations?updated_at=gt:" + maxUpdatedAt)
                                     "organizations?sort=updated_at&per-page=50&updated_at=gte:" + maxUpdatedAt
                                    , new OrganizationsParser());
        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            final OrganizationsParser.Result res = (OrganizationsParser.Result) result.parseData;
            if (null != res)
            {
                final ArrayList<Organization> list = res.organizations;
                final int count;
                if (null != list &&  (0 != (count = list.size())))
                {
                    DbDataHelper.insertOrganizations(application.getDbHelper().getWritableDatabase(), list, res.organizationCategories);
                    return count;
                }
            }
        }
        return 0;
    }
    */

    private ArrayList<Event> readEvents(boolean initRead)
    {
        if (0 == (System.currentTimeMillis() % 3))
        {
            final long time = _DateUtils.getTruncatedCurrentTime();
            _DBHelper.execSQL(application.getDbHelper().getWritableDatabase(), "delete from events where date < " + time);
        }

        long maxUpdatedAt  = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbEventsHelper.TABLE_NAME);
        boolean bRead = false;
        if (networkConnected)
        {
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            if (diff > 0)
                bRead = (loadEvents(maxUpdatedAt) >= 0);
        }

        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_EVENTS;

        ArrayList<Event> events = DbEventsHelper.getEvents(application.getDbHelper().getReadableDatabase(), true, true);
        if (null == events)
            events = new ArrayList<>();

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbEventsHelper.TABLE_NAME, true, 9);
        return events;
    }


    private ArrayList<Action> readActions(boolean initRead)
    {
        if (0 == (System.currentTimeMillis() % 4))
        {
            long time = _DateUtils.getTruncatedCurrentTime();
            _DBHelper.execSQL(application.getDbHelper().getWritableDatabase(), "delete from actions where life_time_type = 2 and date_end < " +  time);
        }

        long maxUpdatedAt  = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbActionsHelper.TABLE_NAME);

        boolean bRead = false;
        if (networkConnected)
        {
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            if (diff > 0)
                bRead = (loadActions(maxUpdatedAt) >= 0);
        }

        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_ACTIONS;

        ArrayList<Action> actions = DbActionsHelper.getActions(application.getDbHelper().getReadableDatabase(), true);
        if (null == actions)
            actions = new ArrayList<>();

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbActionsHelper.TABLE_NAME, true, 9);
        return actions;
    }


    private void _readData(RootData rootData, boolean initRead)
    {
        try
        {
            readData(rootData, initRead);
        }
        catch (SQLiteException se)
        {
            Log.e("StartApplicationTask", "read data error", se);
            TrackerExceptionHelper.sendExceptionStatistics(application.getTracker(), se, false);
            if (needRestore(se))
            {
                tryToRestoreDataBase(true);
                try
                {
                    readData(rootData, initRead);
                }
                catch (Throwable e)
                {
                    Log.e("StartApplicationTask", "read data error", e);
                    TrackerExceptionHelper.sendExceptionStatistics(application.getTracker(), e, false);
                }
            }
        }
        catch (Throwable e)
        {
            Log.e("StartApplicationTask", "read data error", e);
            TrackerExceptionHelper.sendExceptionStatistics(application.getTracker(), e, false);
        }
    }

    private void tryToRestoreDataBase(boolean closePrevious)
    {
        if (closePrevious)
        try
        {
            application.getDbHelper().getWritableDatabase().close();
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }
        try
        {
            File file = application.getDatabasePath(DBHelper.DB_NAME);
            File temp = new File(file.getAbsolutePath() + ".tmp");
            file.renameTo(temp);
            temp.delete();
            file.delete();
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }

        try
        {
            application.getDbHelper().getWritableDatabase();
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }
    }

    private void migrateDataBase()
    {
        String appId = AppData.getAppId();

        //Norilsk
        checkMigrate(appId, "160", 2016, 11, 1);
    }

    private void checkMigrate(String appId, String targetAppId, int year, int month, int day)
    {
        if (3 == appId.length() && appId.regionMatches(0, targetAppId, 0, 3))
        {
            File file = application.getDatabasePath(DBHelper.DB_NAME);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            //MONTH IS ZERO BASED
            calendar.set(year, month - 1, day, 0, 0, 0);
            long t1 = file.lastModified();
            long t2 = calendar.getTimeInMillis();
            if (t1 < t2)
            {
                tryToRestoreDataBase(false);
            }
        }
    }


    private boolean needRestore(SQLiteException se)
    {
        String msg = se.getMessage();
        return ( null != msg && msg.contains("upgrade read-only database") || msg.contains("Could not open database")  || msg.contains("Missing databases"));
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        //Log.d("#c#", "#c# " + "StartApplicationTask");
        //_DBHelper.exportDatabase(application, DBHelper.DB_NAME);
        migrateDataBase();
        long now = System.currentTimeMillis();
        startTime = now;
        RootData rootData = application._rootData;

        boolean firstRun = true;
        //boolean directWriteMode = false;
        try
        {
            //Don't remove next line
            //application.getDbHelper().getWritableDatabase();
            //directWriteMode = DbDataHelper.setDirectWriteMode(application.getDbHelper().getWritableDatabase());

            final long val = _DBHelper.simpleQueryForLong(application.getDbHelper().getReadableDatabase(), "select count(*) from version", 0);
            firstRun = (0 == val);
        }
        catch (SQLiteException se)
        {
            if (needRestore(se))
                tryToRestoreDataBase(true);
        }
        catch (Throwable e)
        {
            Log.e("StartApplicationTask", "read data error", e);
            TrackerExceptionHelper.sendExceptionStatistics(application.getTracker(), e, false);
        }

        if (firstRun)
        {
            //long time = System.currentTimeMillis();
             _readData(rootData, true);
             insertVersion();
            //Log.d("#c#", "#c# " + "all time=" + ((System.currentTimeMillis() - time) / 1000));
        }
        else
        {
            //Log.d("#x#", "#x# " + count + " , " + count1 + " , " + count2);
            _readData(rootData, false);
        }

        loadPushDataIfNeeded();

        //if (null != assetManager)
        //    assetManager.close();

        //if (directWriteMode)
        //    DbDataHelper.setNormalWriteMode(application.getDbHelper().getWritableDatabase());

        long diff = 1000 - (System.currentTimeMillis() - now);
        if (diff > 0)
        {
            try
            {
                Thread.sleep(diff);
            }
            catch (Throwable e)
            {
            }
        }

        //Log.d("#c#", "#c# " + "StartApplicationTask time = " + (System.currentTimeMillis()  - startTime) / 1000); ;

        /*+
        Log.d("#c#", "#c# " +
                "All time = " + (System.currentTimeMillis()  - startTime) / 1000
                + "\nparse organizations time: " +orgParseTime / 1000
                + "\ninsert organization time: " + insertOrganizationTime / 1000
                + "\nparse links: " + orCatParseTime / 1000
                + "\ninsert link time: " + insertOrganizationCategoryTime / 1000
        );*/

        return true;
    }


    private void insertVersion()
    {
        String version = application.getVersionString();
        if (TextUtils.isEmpty(version))
            version = "1";
        try
        {
            application.getDbHelper().getWritableDatabase().execSQL("INSERT INTO VERSION(NAME) VALUES('" + version + "')");
        }
        catch (Throwable e)
        {
            Log.e("StartApplicationTask", "insert version error", e);
            TrackerExceptionHelper.sendExceptionStatistics(application.getTracker(), e, false);
        }
    }


    protected void onPostExecute(Boolean result)
    {
        if (0 != postUpdateMode)
        {
            if (!networkConnected)
                networkConnected = NetworkUtils.isNetworkConnected(application);

            if (networkConnected)
                application.getAsyncTaskExecutor().execute(new BackgroundUpdateTask(application, 0, null, postUpdateMode));
        }

        if (null != resultCallback)
        {
            /*
            if (result)
            {
                resultCallback.onFinished(rc, null);
            }
            else
            {
                resultCallback.onFailed(rc, errorDescription);
            }
            */
            resultCallback.onFinished(rc, null);
        }
    }


    private void readOrganizationCategories(boolean initRead)
    {
        long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbOrganizationCategoriesHelper.TABLE_NAME);
        //if (initRead || (0 == maxUpdatedAt))
        //    DbOrganizationCategoriesHelper.deleteOrganizationCategories(application.getDbHelper().getWritableDatabase());

        boolean bRead = false;

        if (networkConnected)
        {
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            if (diff > 0)
                bRead = loadOrganizationsCategories(maxUpdatedAt, diff);
        }
        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_CATEGORY_ORGANIZATIONS;

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbOrganizationCategoriesHelper.TABLE_NAME, false, 9);
    }


    private void readOptions(boolean initRead)
    {
        boolean bRead = false;
        if (networkConnected)
        {
            final long diff = maxTime - (System.currentTimeMillis() - startTime);
            if (( diff > 0) || initRead)
                bRead = loadOptions();
        }
        if (!bRead)
            postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_OPTIONS;
    }


    private void readAbout(boolean initRead)
    {
        if (initRead || (0 == (System.currentTimeMillis() % 3)) ||
                (0 == _DBHelper.simpleQueryForLong(application.getDbHelper().getReadableDatabase(),
                        "SELECT COUNT(*) FROM ABOUT", 0)))
        {
            boolean bRead = false;
            if (networkConnected)
            {
                final long diff = maxTime - (System.currentTimeMillis() - startTime);
                if (diff > 0)
                    bRead = loadAbout();
            }

            if (!bRead)
                postUpdateMode |= BackgroundUpdateTask.MODE_UPDATE_ABOUT;
        }
    }

    private void loadPushDataIfNeeded()
    {
        if (null == pushData)
            return;

        String table = pushData.table;
        if (TextUtils.isEmpty(table))
            return;

        long id = 0;
        if (null != pushData.id)
        {
            try
            {
                id = Long.parseLong(pushData.id);
            }
            catch (Throwable e)
            {
                TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(application), e, false);
            }
        }

        if (0 == id)
            return;

        final int len = table.length();

        //category
        if (len == PushTableNames.CATEGORY.length() && table.regionMatches(0, PushTableNames.CATEGORY, 0, len))
        {
            if (!DbDataHelper.isObjectExist(application.getDbHelper().getReadableDatabase(), DbCategoriesHelper.TABLE_NAME, id))
            {
                if (loadCategory(id))
                    application._rootData.categories = readCategories();
            }
            return;
        }

        //event, afisha
        if ((len == PushTableNames.EVENTS_TABLE.length() && table.regionMatches(0, PushTableNames.EVENTS_TABLE, 0, len)) //||
           //     (len == PushTableNames.AFISHA.length() && table.regionMatches(0, PushTableNames.AFISHA, 0, len))
           )
        {
            if (!DbDataHelper.isObjectExist(application.getDbHelper().getReadableDatabase(), DbEventsHelper.TABLE_NAME, id))
            {
                loadEvent(id);
            }
            return;
        }

        //if ((len == PushTableNames.ACTIONS.length() && table.regionMatches(0, PushTableNames.ACTIONS, 0, len)) ||
        //    (len == PushTableNames.PROMOTIONS.length() && table.regionMatches(0, PushTableNames.PROMOTIONS, 0, len)))
        if (table.startsWith(PushTableNames.ACTION_PREFIX) ||
                table.startsWith(PushTableNames.PROMOTION_PREFIX))
        {
            if (!DbDataHelper.isObjectExist(application.getDbHelper().getReadableDatabase(), DbActionsHelper.TABLE_NAME, id))
            {
                loadAction(id);
            }
            return;
        }

        //organization
        if (len == PushTableNames.ORGANIZATIONS_TABLE.length() && table.regionMatches(0, PushTableNames.ORGANIZATIONS_TABLE, 0, len))
        {
            if (!DbDataHelper.isObjectExist(application.getDbHelper().getReadableDatabase(), DbOrganizationsHelper.TABLE, id))
            {
                loadOrganization(id);
            }
            return;
        }

        //news
        if (len == PushTableNames.NEWS.length() && table.regionMatches(0, PushTableNames.NEWS, 0, len))
        {
            if (!DbDataHelper.isObjectExist(application.getDbHelper().getReadableDatabase(), DbNewsHelper.TABLE_NAME, id))
            {
                loadNew(id);
            }
            return;
        }
    }
}