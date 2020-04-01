package ru.mycity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.mycity.utils.ListState;
import ru.utils.FilterableArrayAdapter;

abstract class SearchableListFragment extends SearchableFragment
{
    protected final long startTime;
    protected FilterableArrayAdapter<?> adapter;

    public SearchableListFragment()
    {
        this.startTime = System.currentTimeMillis();
    }


    /*
    public boolean onSearchViewClose()
    {
        //if (null != adapter)
        //    adapter.resetFilter();
        KeyboardHelper.hideSoftKeyboard(getActivity());
        return true;
    }*/

    @Override
    public boolean onQueryTextChange(String query)
    {
        super.onQueryTextChange(query);
        if (null != adapter)
        {
            //if (null != query && 0 != query.length())
            adapter.getFilter().filter(query);
            //else
            //    adapter.resetFilter();
        }
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        sendStatisticsOnOpen(view);
    }

    protected void sendStatisticsOnOpen(View view)
    {
        String trackerLabel = getTrackerLabel();
        if (null != trackerLabel)
        {
            long val = getTrackerValue();
            if (val > 0)
            {
                TrackerEventHelper.sendEventStatistics(TrackerHelper.getTracker(view), getTrackerCategory(), ITrackerEvents.ACTION_OPEN, trackerLabel, val);
            }
            else
            {
                TrackerEventHelper.sendEventStatistics(TrackerHelper.getTracker(view), getTrackerCategory(), ITrackerEvents.ACTION_OPEN, trackerLabel);
            }
        }
    }

    private ListState listState;

    //TODO need testing
    public boolean saveState(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (null == listAdapter)
            return false;

        if (adapter instanceof WrapperListAdapter) ///????
            listAdapter = ((WrapperListAdapter) adapter).getWrappedAdapter(); ///????

        if (listAdapter != null && listAdapter.getCount() > 0)
        {
            final ListState state = (null == listState) ? listState :  (listState = new ListState());
            final View v = listView.getChildAt(0);
            state.setIndex(listView.getFirstVisiblePosition());
            state.setTop(v == null ? 0 : v.getTop());
        }
        return true;
    }

    public boolean restoreState(ListView listView)
    {
        if (null == listState)
            return false;
        listView.setSelectionFromTop(listState.getIndex(), listState.getTop());
        return true;
    }

    protected String getTrackerCategory()
    {
        return ITrackerEvents.CATEGORY_SECTION;
    }

    protected long getTrackerValue()
    {
        return System.currentTimeMillis() - startTime;
    }



    @Override
    public boolean openDetailFragment(Fragment fragment, String tag)
    {
        if (null != adapter && adapter.isFilterSet())
        {
            saveView = true;
            //KeyboardHelper.hideSoftKeyboard(getActivity());
        }
        return super.openDetailFragment(fragment, tag);
    }

}