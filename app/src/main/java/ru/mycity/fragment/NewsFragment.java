package ru.mycity.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.adapter.NewsAdapter;
import ru.mycity.data.News;
import ru.mycity.data.RootData;
import ru.mycity.database.DbNewsHelper;
import ru.mycity.tasks.GetNewsTask;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.EndlessScrollListener;

public class NewsFragment extends SearchableListFragment implements AdapterView.OnItemClickListener,
            SwipeRefreshLayout.OnRefreshListener, IResultCallback, EndlessScrollListener.EndlessListener, ILoadData
{
    public static final String NAME = "NewsFragment";


    protected ListView listView;
    private CharSequence title;
    protected RootData rootData = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private View footer;
    private EndlessScrollListener еndLessScrollListener;

    public NewsFragment()
    {
        hasOptionsMenu();
    }

    public void setData(RootData rootData, CharSequence title)
    {
        this.rootData = rootData;
        this.title = title;
    }

    /*
    private void addFooterView()
    {
        if (null == listView)
            return;

        //if (null == footer)
        {
            LayoutInflater inflater = (LayoutInflater) listView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            footer = inflater.inflate(R.layout.loading_layout, null);
        }
        listView.addFooterView(footer);
    }*/

    @Override
    protected CharSequence getTitle()
    {
        return title;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.news_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color1, R.color.swipe_color2, R.color.swipe_color3, R.color.swipe_color4);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ListView) swipeRefreshLayout.findViewById(R.id.list);
        initListView(inflater);
        return rootView;
    }

    protected void initListView(LayoutInflater inflater)
    {
        ArrayList<News> news = (null != rootData) ? rootData.news : null;
        if (null == news)
        {
            news = new ArrayList<>();
        }

        listView.setAdapter(adapter = new NewsAdapter(inflater, news));
        listView.setOnItemClickListener(this);
        еndLessScrollListener = new EndlessScrollListener(adapter, this);
        listView.setOnScrollListener(еndLessScrollListener);
        if (null != news  && !news.isEmpty())
            ;
        else
        {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        final String sq = searchQuery;
        if (null != searchQuery && 0 != searchQuery.length())
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view.getContext()), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_SEARCH, getTrackerLabel(), null, TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_QUERY, searchQuery)));
        }
        searchQuery = null;

        final News item = ((NewsAdapter) adapter).getItemAtPosition(position);
        final NewFragment f = new NewFragment();
        f.setData(item, item.title, sq);
        openDetailFragment(f, NewFragment.NAME);
    }

    @Override
    public void onRefresh()
    {
        if (!refresh())
           stopSwipeRefreshing();
    }

    private boolean refresh()
    {
        FragmentActivity activity = getActivity();
        if (null == activity)
            return false;
        _Application app =  (_Application) activity.getApplicationContext();
        app.getAsyncTaskExecutor().execute(new GetNewsTask(app, 0, this));
        return true;
    }

    private void stopSwipeRefreshing()
    {
        SwipeRefreshLayout layout = swipeRefreshLayout;
        if (null != layout && layout.isRefreshing())  // some bugs
        {
            layout.setRefreshing(false);
        }
    }

    @Override
    public void onFinished(int rc, Result result)
    {
        stopSwipeRefreshing();
        onGetData((GetNewsTask.Result) result);
    }

    @Override
    public void onFailed(int rc, Throwable error)
    {
        stopSwipeRefreshing();
        showErrorToast(R.string.s7, false);
    }

    @Override
    public void onFailed(int rc, String description)
    {
        stopSwipeRefreshing();
        showErrorToast(R.string.s7, false);
    }

    private void onGetData(GetNewsTask.Result result)
    {
        if (null == result || null == rootData)
            return;

        if (false == result.bSuccess)
            return;

        ArrayList<News> news = result.news;
        if (null == news)
            news = new ArrayList<>();

        rootData.news = news;

        /*
        ArrayList<News> featured = result.featuredNews;
        if (null == featured)
        {
            featured = new ArrayList<>();
        }

        rootData.featuredNews = featured;
        */
        rootData.topNews = result.topNews;

        if (null != adapter && null != news)
            ((NewsAdapter) adapter).setNews(news);

        if (null != еndLessScrollListener)
            еndLessScrollListener.setEnabled(true);
    }

    @Override
    protected String getTrackerLabel()
    {
        return TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_NEWS);
    }

    @Override
    public void loadData()
    {
        FragmentActivity activity = getActivity();
        if (null == activity)
            return;
        _Application app =  (_Application) activity.getApplicationContext();

        //addFooterView();
        //int count = adapter.getCount();
        //long allCount = DbDataHelper.getCount(app.getDbHelper().getReadableDatabase(), DbNewsHelper.TABLE_NAME);
        //if (0 == allCount)
        //    return;
        int count = (((NewsAdapter) adapter)).getItemsCount();
        app.getAsyncTaskExecutor().execute(new LoadDataAsyncTask(app, this), count);
    }


    @Override
    public void onLoad(ArrayList<?> objects)
    {
        ArrayList<News> news = (ArrayList<News>) objects;
        //listView.removeFooterView(footer);

        if (null != news && !news.isEmpty())
        {
            ((NewsAdapter) adapter).addNews(news);
            еndLessScrollListener.setLoading(false);
        }
        else
        {
            еndLessScrollListener.setLoading(false);
            еndLessScrollListener.setEnabled(false);
        }
    }

    private final static class LoadDataAsyncTask extends AsyncTask<Integer, Void, ArrayList<News>>
    {
        private final _Application application;
        private final ILoadData callback;

        public LoadDataAsyncTask(_Application application, ILoadData callback)
        {
            this.application = application;
            this.callback = callback;
        }

        @Override
        protected ArrayList<News> doInBackground(Integer... params)
        {
            int count = params[0];
            return DbNewsHelper.get(application.getDbHelper().getReadableDatabase(), false, DbNewsHelper.PAGE_SIZE, count);
            //TODO if no data in local db - load data from network and use footer view
        }

        @Override
        protected void onPostExecute(ArrayList<News> result)
        {
            if (null != callback) callback.onLoad(result);
        }
    }
}


