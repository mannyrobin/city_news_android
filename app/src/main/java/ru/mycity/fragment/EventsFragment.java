package ru.mycity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.mycity.MainActivity;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.adapter.EventsAdapter;
import ru.mycity.data.Event;
import ru.mycity.tasks.GetEventsTask;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;

public class EventsFragment extends SearchableListFragment implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, IResultCallback
{
    public static final String NAME = "EventsFragment";
    protected CharSequence title;
    private ArrayList<Event> events;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public EventsFragment()
    {
        hasOptionsMenu();
    }

    public void setData(ArrayList<Event> events, CharSequence title)
    {
        this.events = events;
        this.title  = title;
    }

    @Override
    protected CharSequence getTitle()
    {
        return title;
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
            listView.setAdapter(adapter = new EventsAdapter<Event>(inflater, events));
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
        
        EventsAdapter<Event> adapter = (EventsAdapter<Event>) parent.getAdapter();
        Event item = adapter.getItem(position);
        EventFragment f = new EventFragment();
        f.setData(item, item.title, false, sq);
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
        app.getAsyncTaskExecutor().execute(new GetEventsTask(app, 0, this));
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
    public void onFinished(int rc, IResultCallback.Result result)
    {
        stopSwipeRefreshing();
        onGetData((GetEventsTask.Result) result);
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

    private void onGetData(GetEventsTask.Result result)
    {
        if (null == result)
            return;
        if (result.hasNewData)
        {
            ArrayList<Event> events = result.events;
            if (null == events)
            {
                events = new ArrayList<>();
            }

            if (null != adapter) ((EventsAdapter<Event>) adapter).setObjects(events);
        }
    }

    @Override
    protected int getMenuResourceId()
    {
        return R.menu.search_with_add;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (R.id.action_add == item.getItemId())
        {
            MainActivity activity = (MainActivity) getActivity();
            if (null != activity)
                activity.handleMenuItem(R.id.menu_item_add_event, null, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected String getTrackerLabel()
    {
        return TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_EVENTS);
    }
}