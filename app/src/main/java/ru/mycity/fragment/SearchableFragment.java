package ru.mycity.fragment;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import ru.mycity.R;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.KeyboardHelper;

abstract class SearchableFragment extends BaseFragment implements SearchView.OnQueryTextListener
{
    protected MenuItem searchMenuItem;
    protected String searchQuery;

    protected abstract String getTrackerLabel();

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        searchMenuItem = menu.findItem(R.id.action_search);
        if (null == searchMenuItem)
        {
            inflater.inflate(getMenuResourceId(), menu);
            searchMenuItem = menu.findItem(R.id.action_search);
        }
        if (null != searchMenuItem)
        {
            if (!searchMenuItem.isVisible())
                searchMenuItem.setVisible(true);

            SearchView searchView = getSearchView();
            searchView.setOnQueryTextListener(this);
            setOnCloseListener(searchMenuItem, searchView);
            //searchView.setOnCloseListener(this);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setOnCloseListener(MenuItem menuItem, final SearchView searchView)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener()
            {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item)
                {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item)
                {
                    onSearchViewClose();
                    return true;
                }
            });
        }
        else
        {
            searchView.setOnCloseListener(new SearchView.OnCloseListener()
            {
                @Override
                public boolean onClose()
                {
                    onSearchViewClose();
                    return true;
                }
            });
        }
    }

    public boolean onSearchViewClose()
    {
        KeyboardHelper.hideSoftKeyboard(getActivity());
        return true;
    }

    protected int getMenuResourceId()
    {
        return R.menu.search;
    }

    protected SearchView getSearchView()
    {
        return (null != searchMenuItem) ? (SearchView) MenuItemCompat.getActionView(searchMenuItem) : null;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        MenuItemCompat.collapseActionView(searchMenuItem);
        //if (null != query && 0 != query.length() && null != adapter)
        //    adapter.getFilter().filter(query);

        //Tracker tracker = ((_Application) this.getContext().getApplicationContext()).getTracker();
        //if (null != tracker)
        //{
        //    sendEventStatistics(tracker, ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_SEARCH, query);
        //}

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query)
    {
        if (null != searchQuery && 3 <= searchQuery.length() && (null == query || 0 == query.length()))
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(this.getContext()), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_SEARCH, getTrackerLabel(), null, TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_QUERY, searchQuery)));
        }
        searchQuery = query;
        return true;
    }


}
