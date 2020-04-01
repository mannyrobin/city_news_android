package ru.mycity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ru.mycity.adapter.EventsAdapter;
import ru.mycity.data.Action;
import ru.mycity.tasks.GetActionsTask;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;

public class ActionsFragment extends SearchableListFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, IResultCallback
{
    public static final String NAME = "ActionsFragment";
    protected CharSequence title;
    private ArrayList<Action> events;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ActionsFragment()
    {
        hasOptionsMenu();
    }

    public void setData(ArrayList<Action> events, CharSequence title)
    {
        this.events = events;
        this.title  = title;
    }

    @Override
    protected CharSequence getTitle()
    {
        if (null == title)
        {
            title = getText(R.string.s6);
        }
        return title;
    }

    @Override
    protected String getTrackerLabel()
    {
        return TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_ACTIONS);
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.events_fragment, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color1, R.color.swipe_color2, R.color.swipe_color3, R.color.swipe_color4);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ListView) swipeRefreshLayout.findViewById(R.id.list);

        if (null != events && !events.isEmpty())
        {
            listView.setAdapter(adapter = new EventsAdapter<Action>(inflater, events));
            listView.setOnItemClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String sq = searchQuery;
        if (null != searchQuery && 0 != searchQuery.length())
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view.getContext()), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_SEARCH, getTrackerLabel(), null, TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_QUERY, searchQuery)));
        }
        searchQuery = null;

        EventsAdapter<Action> adapter = (EventsAdapter<Action>) parent.getAdapter();
        Action item = adapter.getItem(position);
        EventFragment f = new EventFragment();
        f.setData(item, item.title, true, sq);
        openDetailFragment(f, EventFragment.NAME);
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
        _Application app = (_Application) activity.getApplicationContext();
        app.getAsyncTaskExecutor().execute(new GetActionsTask(app, 0, this));
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
        onGetData((GetActionsTask.Result) result);
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

    private void onGetData(GetActionsTask.Result result)
    {
        if (null == result)
            return;
        if (result.hasNewData)
        {
            ArrayList<Action> events = result.actions;
            if (null == events)
            {
                events = new ArrayList<>();
            }

            if (null != adapter) ((EventsAdapter<Action>) adapter).setObjects(events);
        }
    }

}
