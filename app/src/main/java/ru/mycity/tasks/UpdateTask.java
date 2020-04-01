package ru.mycity.tasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;

import ru.mycity.AppUrls;
import ru.mycity.Config;
import ru.mycity._Application;
import ru.mycity.data.About;
import ru.mycity.data.Action;
import ru.mycity.data.Button;
import ru.mycity.data.Category;
import ru.mycity.data.Event;
import ru.mycity.data.News;
import ru.mycity.data.Option;
import ru.mycity.data.Organization;
import ru.mycity.data.OrganizationCategory;
import ru.mycity.database.DbAboutHelper;
import ru.mycity.database.DbActionsHelper;
import ru.mycity.database.DbButtonsHelper;
import ru.mycity.database.DbCategoriesHelper;
import ru.mycity.database.DbDataHelper;
import ru.mycity.database.DbEventsHelper;
import ru.mycity.database.DbNewsHelper;
import ru.mycity.database.DbOptionsHelper;
import ru.mycity.database.DbOrganizationCategoriesHelper;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.network.HttpClient;
import ru.mycity.parser.AboutParser;
import ru.mycity.parser.ActionParser;
import ru.mycity.parser.ActionsParser;
import ru.mycity.parser.ButtonsParser;
import ru.mycity.parser.CategoriesParser;
import ru.mycity.parser.CategoryParser;
import ru.mycity.parser.EventParser;
import ru.mycity.parser.EventsParser;
import ru.mycity.parser.IParser;
import ru.mycity.parser.NewsParser;
import ru.mycity.parser.OneNewsParser;
import ru.mycity.parser.OptionsParser;
import ru.mycity.parser.OrganizationParser;
import ru.mycity.parser.OrganizationsCategoriesParser;
import ru.mycity.parser.OrganizationsParser;

public abstract class UpdateTask extends AsyncTask<Void, Void, Boolean>
{
    protected final _Application application;
    final int rc;
    protected final IResultCallback resultCallback;
    protected boolean networkConnected;

    private final static String read_organizations_operation_key = "roo";
    private final static String read_organizations_operation_category_key = "rсoo";

    public UpdateTask(_Application application, int rc, IResultCallback resultCallback)
    {
        this.application = application;
        this.rc = rc;
        this.resultCallback = resultCallback;
    }

    protected HttpClient.Result executeGet(String _url, IParser parser)
    {
        return executeGet(_url, HttpClient.READ_TIMEOUT, false, parser);
    }

    protected HttpClient.Result executeGet(String _url, int readTimeout, boolean getPageCount, IParser parser)
    {
        HttpClient.Result result = HttpClient.executeGet(_url, readTimeout, getPageCount, parser);
        if (null != result && HttpClient.Result.NETWORK_ERROR == result.rc)
            networkConnected = false;
        return result;
    }

    protected int loadButtons(long maxUpdatedAt)
    {
        String url = AppUrls.API_URL + "buttons?per-page=-1&sort=updated_at&updated_at=gt:" + maxUpdatedAt;

        HttpClient.Result result = executeGet(url, new ButtonsParser());
        if (null != result && HttpClient.Result.SUCCESS == result.rc)
        {
            ArrayList<Button> buttons = (ArrayList<Button>) result.parseData;
            int count = 0;
            if (null != buttons && ( (count = buttons.size()) > 0))
                DbButtonsHelper.insertButtons(application.getDbHelper().getWritableDatabase(), buttons);
            return count;
        }
        return -1;
    }

    protected int loadCategories(long maxUpdatedAt)
    {
        //maxUpdatedAt = 0;
        final String url = AppUrls.API_URL + "categories?sort=updated_at&recursive=true&updated_at=gt:" + maxUpdatedAt;
        return loadCategories(url);
    }


    private int loadCategories(String url)
    {
        HttpClient.Result result = executeGet(url, 9 * 1000, false,  new CategoriesParser());
        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            ArrayList<Category> categories = (ArrayList<Category>) result.parseData;
            int count =  0;
            if (null != categories && (0 != (count = categories.size())))
            {
                DbCategoriesHelper.insertCategories(application.getDbHelper().getWritableDatabase(), categories, null, false);
            }
            return count;
        }
        return -1;
    }


    protected boolean loadOrganizations(long maxUpdatedAt, long maxExecuteTime)
    {
        /*
        final HttpClient.Result result = executeGet(AppUrls.API_URL +
                //addInfiniteRead("organizations?updated_at=gt:" + maxUpdatedAt)
                //"organizations?sort=updated_at&per-page=-1&updated_at=gt:" + maxUpdatedAt
                "organizations?per-page=-1&updated_at=gt:" + maxUpdatedAt
                , 30 * 1000, false, new OrganizationsParser());
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
        */

        //TODO Temp Debug
        //maxUpdatedAt = 0;

        long startTime = System.currentTimeMillis();
        int page = 1;
        int pages_count = page;
        boolean bRead = false;

        final SharedPreferences prefs = Config.getSharedPreferences(application);
        String operation = prefs.getString(read_organizations_operation_key, "gt");

        //for unexpected exit
        Config.putString(prefs, read_organizations_operation_key, "gte");


        int page_size = 100;
        StringBuilder sb = new StringBuilder(100);
        sb.append(AppUrls.API_URL).append("organizations?sort=updated_at").append("&per-page=").append(page_size)
                .append("&updated_at=").append(operation).append(':').append(maxUpdatedAt).append("&page=");

        String prefix = sb.toString();

        while (true)
        {
            //if (BuildConfig.DEBUG)
            //    Log.d("0c0", "0c0 " + " organizations page = " + page);
            String url = prefix + page;
            OrganizationsParser parser = new OrganizationsParser();
            if (page < pages_count) parser.setDefaultObjectCount(page_size);

            //if (page < (pages_count-1))
            //    parser.setSkipDeleted(true);

            final HttpClient.Result result = executeGet(url, 5 * 1000, true, parser);
            if (null != result && result.rc == HttpClient.Result.SUCCESS)
            {
                pages_count = result.pageCount;
                final OrganizationsParser.Result res = (OrganizationsParser.Result) result.parseData;
                if (null != res)
                {
                    bRead = true;
                    final ArrayList<Organization> list = res.organizations;

                    if (null != list && !list.isEmpty())
                    {
                        DbOrganizationsHelper.insertOrganizations(application.getDbHelper().getWritableDatabase(), list
                                //, res.organizationCategories
                                , true, true);
                    }
                }
            }
            else
            {
                if (page > 1) pages_count = page + 1;
                bRead = false;
                break;
            }

            if (page >= pages_count) break;

            if ((System.currentTimeMillis() - startTime) > maxExecuteTime)
            {
                bRead = false;
                break;
            }
            page++;
        }

        operation = "gte"; // see below "for unexpected exit"

        String new_operation = page < pages_count ? "gte" : "gt";
        int len = new_operation.length();
        if (len == operation.length() && operation.regionMatches(0, new_operation, 0, len)) ;
        else Config.putString(prefs, read_organizations_operation_key, new_operation);

        return bRead;
    }


    protected boolean loadOrganizationsCategories(long maxUpdatedAt, long maxExecuteTime)
    {
        long startTime = System.currentTimeMillis();
        int page = 1;
        int pages_count = page;

        boolean bRead;

        final SharedPreferences prefs = Config.getSharedPreferences(application);
        String operation = prefs.getString(read_organizations_operation_category_key, "gt");

        //for unexpected exit
        Config.putString(prefs, read_organizations_operation_category_key, "gte");

        int page_size = 200;
        StringBuilder sb = new StringBuilder(130);
        sb.append(AppUrls.API_URL).append("category-organization?sort=updated_at").append("&per-page=").append(page_size).append("&updated_at=").append(operation).append(':').append(maxUpdatedAt).append("&page=");

        String prefix = sb.toString();
        while (true)
        {
            String url = prefix + page;
            OrganizationsCategoriesParser parser = new OrganizationsCategoriesParser();
            if (page < pages_count) parser.setDefaultObjectCount(page_size);

            //if (BuildConfig.DEBUG)
            //    Log.d("0c0", "0c0 " + " organization category page = " + page);


            final HttpClient.Result result = executeGet(url, 7 * 1000, true, parser);
            if (null != result && result.rc == HttpClient.Result.SUCCESS)
            {
                pages_count = result.pageCount;
                bRead = true;
                final ArrayList<OrganizationCategory> list = (ArrayList<OrganizationCategory>) result.parseData;
                if (null != list && !list.isEmpty())
                {
                    DbOrganizationCategoriesHelper.insertOrganizationCategories(application.getDbHelper().getWritableDatabase(), list, true);
                }
            }
            else
            {
                if (page > 1)
                    pages_count = page + 1;
                bRead = false;
                break;
            }
            if (page >= pages_count)
                break;

            if ((System.currentTimeMillis() - startTime) > maxExecuteTime)
            {
                bRead = false;
                break;
            }
            page++;
        }

        operation = "gte"; // see below "for unexpected exit"

        String new_operation = page < pages_count ? "gte" : "gt";
        int len = new_operation.length();
        if (len == operation.length() && operation.regionMatches(0, new_operation, 0, len))
            ;
        else
            Config.putString(prefs, read_organizations_operation_category_key, new_operation);

        return bRead;
    }

    protected boolean loadAbout()
    {
        String url = AppUrls.API_URL + "about";

        HttpClient.Result result = executeGet(url, new AboutParser());
        if (null != result && HttpClient.Result.SUCCESS == result.rc)
        {
            About about = (About) result.parseData;
            if (null != about)
                DbAboutHelper.insert(application.getDbHelper().getWritableDatabase(), about);
            return true;
        }
        return false;
    }

    protected boolean loadOptions()
    {
        final long maxUpdatedAt = DbDataHelper.getMaxUpdatedAt(application.getDbHelper().getReadableDatabase(), "OPTIONS", -1);
        final String url = AppUrls.API_URL + "options?per-page=-1&updated_at=gt:" + maxUpdatedAt;
        final HttpClient.Result result = executeGet(url, new OptionsParser());

        if (null != result && HttpClient.Result.SUCCESS == result.rc)
        {
            ArrayList<Option> options = (ArrayList<Option>) result.parseData;
            if (null != options && !options.isEmpty())
                DbOptionsHelper.insert(application.getDbHelper().getWritableDatabase(), options);
            return true;
        }
        return false;
    }

    protected int loadEvents(long maxUpdatedAt)
    {
        StringBuilder sb = new StringBuilder(120);
        sb.append(AppUrls.API_URL);

        final String urlPart =
                //addInfiniteRead("events?actual=true&sort=date")
                //"events?actual=true&sort=date"
                //"events?actual=true&per-page=50"
                "events?per-page=-1"; //actual=true&

        sb.append(urlPart);

        if (maxUpdatedAt > 0)
        {
            sb.append("&sort=updated_at&updated_at=gt:");
            sb.append(maxUpdatedAt);
        }

        String url = sb.toString();
        HttpClient.Result result = executeGet(url, 7 * 1000, false, new EventsParser(true));
        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            ArrayList<Event> events = (ArrayList<Event>) result.parseData;
            int count = 0;
            if (null != events && (0 != (count = events.size())))
                DbEventsHelper.insertEvents(application.getDbHelper().getWritableDatabase(), events);
            return count;
        }
        return -1;
    }


    protected int loadActions(long maxUpdatedAt)
    {
        StringBuilder sb = new StringBuilder(120);
        sb.append(AppUrls.API_URL);

        final String urlPart = "actions?per-page=-1"; //&actual=true";
        sb.append(urlPart);

        if (maxUpdatedAt > 0)
        {
            sb.append("&sort=updated_at&updated_at=gt:");
            sb.append(maxUpdatedAt);
        }

        String url = sb.toString();
        HttpClient.Result result = executeGet(url, 7 * 1000, false, new ActionsParser());
        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            ArrayList<Action> actions = (ArrayList<Action>) result.parseData;
            int count = 0;
            if (null != actions && 0 != (count = actions.size()))
            {
                DbActionsHelper.insertActions(application.getDbHelper().getWritableDatabase(), actions);
            }
            return count;
        }
        return -1;
    }


    public int loadNews(long maxUpdatedAt, int page_count)
    {
        HttpClient.Result result = executeGet(AppUrls.API_URL +
                        //"news?actual=true&sort=-date&updated_at=gt:" + maxUpdatedAt
                        //"news?sort=-date&updated_at=gt:" + maxUpdatedAt
                        "news?sort=-date&per-page="+page_count+"&updated_at=gt:" + maxUpdatedAt
                , 7 * 1000, false, new NewsParser().setDefaultObjectCount(30));

        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            int count = 0;
            ArrayList<News> news = (ArrayList<News>) result.parseData;
            if (null != news && (0 != (count = news.size())))
                DbNewsHelper.insert(application.getDbHelper().getWritableDatabase(), news);
            return count;
        }
        return -1;
    }

    protected boolean loadCategory(long id)
    {
        String url = AppUrls.API_URL + "categories/" + id;
        HttpClient.Result result = executeGet(url, 3 * 1000, false,  new CategoryParser());
        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            Category category =  (Category) result.parseData;
            boolean bResult = false;
            if (null != category)
            {
                ArrayList<Category> categories = new ArrayList<>(1);
                categories.add(category);
                bResult = DbCategoriesHelper.insertCategories(application.getDbHelper().getWritableDatabase(), categories, null, false);
                //"/categories?recursive=true&per-page=-1"
                bResult = (loadCategories(AppUrls.API_URL + "categories/" + id + "/categories") >= 0);

                result = executeGet(AppUrls.API_URL + "categories/" + id + "/organizations?recursive=true&per-page=-1", 5 * 1000, true, new OrganizationsParser());
                if (null != result && result.rc == HttpClient.Result.SUCCESS)
                {
                    final OrganizationsParser.Result res = (OrganizationsParser.Result) result.parseData;
                    if (null != res)
                    {
                        final ArrayList<Organization> list = res.organizations;
                        if (null != list && !list.isEmpty())
                            DbOrganizationsHelper.insertOrganizations(application.getDbHelper().getWritableDatabase(), list, true, true);
                    }
                }
                else
                {
                    bResult = false;
                }
                //TODO category-organizations
            }
            return bResult;
        }
        return false;
    }

    protected ArrayList<Category> readCategories()
    {
        ArrayList<Category> categories = DbCategoriesHelper.getCategories(application.getDbHelper().getReadableDatabase(), null);
        if (null == categories)
            categories = new ArrayList<>();
        return categories;
    }


    protected Action loadAction(long id)
    {
        HttpClient.Result result = executeGet(AppUrls.API_URL +
                        "actions/" + id
                , 3 * 1000, false, new ActionParser());

        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            Action action = (Action) result.parseData;
            if (null != action)
            {
                ArrayList<Action> list= new ArrayList<>(1);
                list.add(action);
                DbActionsHelper.insertActions(application.getDbHelper().getWritableDatabase(), list);
            }
            return action;
        }
        return null;
    }

    protected Organization loadOrganization(long id)
    {
        final String url = AppUrls.API_URL + "organizations/" + id;

        HttpClient.Result result = executeGet(url, new OrganizationParser());
        if (null != result && HttpClient.Result.SUCCESS == result.rc)
        {
            Organization o = (Organization) result.parseData;
            if (null != o)
            {
                ArrayList<Organization> list = new ArrayList(1);
                list.add(o);
                DbOrganizationsHelper.insertOrganizations(application.getDbHelper().getWritableDatabase(), list
                        , false, true);
            }
            return o;
        }
        return null;
    }


    protected News loadNew(long id)
    {
        HttpClient.Result result = executeGet(AppUrls.API_URL +
                        "news/" + id
                , 3 * 1000, false, new OneNewsParser());

        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            News news =  (News) result.parseData;
            if (null != news)
            {
                ArrayList<News> list = new ArrayList<>(1);
                list.add(news);
                DbNewsHelper.insert(application.getDbHelper().getWritableDatabase(), list);
                return news;
            }
        }
        return null;
    }

    protected Event loadEvent(long id)
    {
        HttpClient.Result result = executeGet(AppUrls.API_URL +
                        "events/" + id
                , 3 * 1000, false, new EventParser());

        if (null != result && result.rc == HttpClient.Result.SUCCESS)
        {
            Event event = (Event) result.parseData;
            if (null != event)
            {
                ArrayList<Event> events = new ArrayList<>(1);
                events.add(event);
                DbEventsHelper.insertEvents(application.getDbHelper().getWritableDatabase(), events);
            }
            return event;
        }
        return null;
    }
}
