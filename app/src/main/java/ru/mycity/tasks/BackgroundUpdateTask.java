package ru.mycity.tasks;


import android.support.annotation.NonNull;

import java.util.ArrayList;

import ru.mycity._Application;
import ru.mycity.data.Action;
import ru.mycity.data.Button;
import ru.mycity.data.Event;
import ru.mycity.data.News;
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
import ru.utils.NetworkUtils;

public class BackgroundUpdateTask extends UpdateTask
{
    private long mode;

    public static int MODE_UPDATE_BUTTONS                = 0x0001;
    public static int MODE_UPDATE_NEWS                   = 0x0002;
    public static int MODE_UPDATE_EVENTS                 = 0x0004;
    public static int MODE_UPDATE_ACTIONS                = 0x0008;
    public static int MODE_UPDATE_CATEGORIES             = 0x0010;
    public static int MODE_UPDATE_ORGANIZATIONS          = 0x0020;
    public static int MODE_UPDATE_CATEGORY_ORGANIZATIONS = 0x0040;
    public static int MODE_UPDATE_ABOUT                  = 0x0080;
    public static int MODE_UPDATE_OPTIONS                = 0x0100;

    public BackgroundUpdateTask(_Application application, int rc, IResultCallback resultCallback, long mode)
    {
        super(application, rc, resultCallback);
        this.mode = mode;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            return execute();
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(application), e, false);
            return null;
        }
    }

    @NonNull
    private Boolean execute()
    {
        networkConnected = NetworkUtils.isNetworkConnected(application);
        if (!networkConnected)
            return Boolean.FALSE;

        //Don't change the order

        if (MODE_UPDATE_ACTIONS == (mode & MODE_UPDATE_ACTIONS))
        {
            updateActions();
        }

        if (MODE_UPDATE_EVENTS == (mode & MODE_UPDATE_EVENTS))
        {
            updateEvents();
        }

        if (MODE_UPDATE_NEWS == (mode & MODE_UPDATE_NEWS))
        {
            updateNews();
        }

        if (MODE_UPDATE_ORGANIZATIONS == (mode & MODE_UPDATE_ORGANIZATIONS))
        {
            updateOrganizations();
        }

        if (MODE_UPDATE_CATEGORY_ORGANIZATIONS == (mode & MODE_UPDATE_CATEGORY_ORGANIZATIONS))
        {
            updateCategoryOrganizations();
        }

        if (MODE_UPDATE_CATEGORIES == (mode & MODE_UPDATE_CATEGORIES))
        {
            updateCategories();
        }

        if (MODE_UPDATE_BUTTONS == (mode & MODE_UPDATE_BUTTONS))
        {
            updateButtons();
        }

        if (MODE_UPDATE_OPTIONS == (mode & MODE_UPDATE_OPTIONS))
        {
            updateOptions();
        }

        if (MODE_UPDATE_ABOUT == (mode & MODE_UPDATE_ABOUT))
        {
            updateAbout();
        }
        return Boolean.TRUE;
    }

    protected void onPostExecute(Boolean result)
    {
        if (null != resultCallback)
            resultCallback.onFinished(rc, null);
        //TODO Notify application to refresh current page
    }

    private void updateNews()
    {
        long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbNewsHelper.TABLE_NAME);

        //DbDataHelper.clearTable(application.getDbHelper().getWritableDatabase(), maxUpdatedAt, DbNewsHelper.TABLE_NAME, true, 4);
        if (loadNews(maxUpdatedAt, 60) > 0)
        {
            ArrayList<News> news = DbNewsHelper.get(application.getDbHelper().getReadableDatabase(), false, DbNewsHelper.PAGE_SIZE, 0);
            if (null == news) news = new ArrayList<>();
            application._rootData.news = news;
            //application._rootData.featuredNews = DbNewsHelper.get(application.getDbHelper().getReadableDatabase(), true, 0, 0);
            application._rootData.topNews = DbNewsHelper.getTopNews(application.getDbHelper().getReadableDatabase());
        }
    }

    private void updateButtons()
    {
        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbButtonsHelper.TABLE_NAME);

        if (loadButtons(maxUpdatedAt) > 0)
        {
            ArrayList<Button> buttons = DbButtonsHelper.getButtons(application.getDbHelper().getReadableDatabase());
            if (null != buttons && !buttons.isEmpty())

            //readAsset(this.buttons, new ButtonsParser());

            if (null == buttons)
                buttons = new ArrayList<>();

            application._rootData.buttons = buttons;
        }
    }

    private void updateOrganizations()
    {
        final long maxUpdatedAt =
                DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(),
                        DbOrganizationsHelper.TABLE);
        loadOrganizations(maxUpdatedAt, Integer.MAX_VALUE);
    }

    private void updateCategoryOrganizations()
    {
        final long maxUpdatedAt =
                DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(),
                        DbOrganizationCategoriesHelper.TABLE_NAME);
        loadOrganizationsCategories(maxUpdatedAt, Integer.MAX_VALUE);
    }

    private void updateActions()
    {
        long maxUpdatedAt  = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbActionsHelper.TABLE_NAME);
        if (loadActions(maxUpdatedAt) > 0)
        {
            ArrayList<Action> actions = DbActionsHelper.getActions(application.getDbHelper().getReadableDatabase(), true);
            if (null == actions)
                actions = new ArrayList<>();
            application._rootData.actions = actions;
        }
    }

    private void updateCategories()
    {
        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(),
                DbCategoriesHelper.TABLE_NAME);
        if (loadCategories(maxUpdatedAt) > 0)
            application._rootData.categories = readCategories();
    }

    private void updateOptions()
    {
        loadOptions();
    }

    private void updateAbout()
    {
        loadAbout();
    }

    private void updateEvents()
    {
        long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), DbEventsHelper.TABLE_NAME);
        if (loadEvents(maxUpdatedAt) > 0)
        {
            ArrayList<Event> events = DbEventsHelper.getEvents(application.getDbHelper().getReadableDatabase(), true, true);
            if (null == events)
                events = new ArrayList<>();
            application._rootData.events = events;
        }
    }

}
