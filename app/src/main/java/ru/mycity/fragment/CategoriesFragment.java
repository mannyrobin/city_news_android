package ru.mycity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
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
import ru.mycity.adapter.CategoriesAdapterEx;
import ru.mycity.adapter.OrganizationsAdapter;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.database.DbCategoriesHelper;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.EndlessScrollListener;
import ru.utils.ExpandableHeightListView;

public class CategoriesFragment extends OrganizationsFragment
{
    public static final String NAME = "CategoriesFragment";
    private static final int MSG_SELECT_CATEGORY = 3;
    private ArrayList<Category> categories;
    private ListView listView;
    private CharSequence title;
    private OrganizationsAdapter organizationsAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        long val = (null == category) ? 0 : category.id;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_LIST, ITrackerEvents.ACTION_OPEN, getTrackerLabel(), null, TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_ENTITY_ID, String.valueOf(val)), TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_TITLE, (null != title) ? title.toString(): "")), val));
    }

    public CategoriesFragment()
    {
        hasOptionsMenu();
    }


    protected ArrayList<Category> getSubCategories()
    {
        return categories;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        if (null != menuItem)
        {
            menuItem.setVisible(null == category);
            menuItem.setEnabled(null == category);
        }

        SearchView searchView = getSearchView();
        if (null != searchView)
        {
            searchView.setQueryHint(getText(R.string.s8));
        }
    }




    @Override
    protected int getMenuResourceId()
    {
        return R.menu.categories;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //Organization organization = (Organization) parent.getAdapter().getItem(position);
        Organization organization = organizationsAdapter.getItem(position-1);
        openCategory(view, organization);
    }


    void onCategorySelect(AdapterView<?> parent, View view, int position, long id)
    {
        searchContext = searchQuery;
        if (null != searchQuery && 0 != searchQuery.length())
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view.getContext()), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_SEARCH, getTrackerLabel(), null, TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_QUERY, searchQuery)));
        }
        searchQuery = null;

        listView.setEnabled(false);
        Handler handler = getHandler();
        Message msg = Message.obtain(handler, MSG_SELECT_CATEGORY, parent.getAdapter().getItem(position));
        handler.sendMessage(msg);
    }


    @Override
    void _handleMessage(int what, Object obj)
    {
        if (what == MSG_SELECT_CATEGORY)
        {
            onSelectCategory((Category) obj);
            return;
        }
        super._handleMessage(what, obj);
    }

    protected OrganizationsAdapter getOrganizationsAdapter()
    {
        return organizationsAdapter;
    }

    void showMap(ArrayList<Organization> organizations, Category category)
    {
        showMap(organizations, category, "map-category");
    }





    @Override
    protected CharSequence getTitle()
    {
        return title;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (R.id.action_add == id)
        {
            MainActivity activity = (MainActivity) getActivity();
            if (null != activity)
            {
                activity.handleMenuItem(R.id.menu_item_add_organization, null, false);
            }
            return true;
        }
        if (R.id.action_map == id)
        {
            item.setEnabled(false);

            /*
            View progress = null;
            if (null == category)
            {
                progress = getView().findViewById(R.id.progress);
                progress.setVisibility(View.VISIBLE);
            }
            */

            //Handler handler = getHandler();
            //handler.sendMessage(Message.obtain(handler, MSG_SHOW_MAP, item));
            getMap(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.categories_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);

        if (null == categories)
        {
            categories = new ArrayList<>(1);
        }

        ArrayList<Organization> organizations = (null != category && null != category.organizations) ? category.organizations : new ArrayList<Organization>(1);
        organizationsAdapter = new OrganizationsAdapter (getActivity().getLayoutInflater(), organizations, (null != category) ? category.id : 0, this);

        adapter = new CategoriesAdapterEx(inflater, categories, organizationsAdapter, category);


        View header = inflater.inflate(R.layout.organizations_list_header, null);
        ExpandableHeightListView lv = (ExpandableHeightListView) header.findViewById(R.id.list);
        View empty = header.findViewById(android.R.id.text1);
        lv.setEmptyView(empty);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //CategoriesFragment.super.onItemClick(parent, view, position, id);
                onCategorySelect(parent, view, position, id);
            }
        });

        lv.setExpanded(true);
        //setListViewHeightBasedOnChildren(lv);

        //ViewGroup.LayoutParams listViewParams = (ViewGroup.LayoutParams)lv.getLayoutParams();
        //listViewParams.height = 400;

        listView.addHeaderView(header);
        listView.setOnItemClickListener(this);

        listView.setAdapter(organizationsAdapter);


        //organizations_list_header
        //onCategorySelect(AdapterView<?> parent, View view, int position, long id)
        //listView.setOnItemClickListener(this);

        еndLessScrollListener = new EndlessScrollListener(organizationsAdapter, this);
        listView.setOnScrollListener(еndLessScrollListener);

        return rootView;
    }


    @Override
    protected String getTrackerLabel()
    {
        return (null == category) ?
                TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_CATEGORIES)
                :
                TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_PAGE, ITrackerEvents.LABEL_TARGET_CATEGORY);
    }

    void onSelectCategory(Category category)
    {
        openCategory(category, (MainActivity) getActivity());
        listView.setEnabled(true);
    }


    public static boolean openCategory(Category category, MainActivity activity)
    {
        if (null == activity) return false;
        _Application application = (_Application) activity.getApplication();

        ArrayList<Category> categories = category.subCategories;
        if (null == categories)
        {
            categories = DbCategoriesHelper.getCategories(application.getDbHelper().getReadableDatabase(), category);
            if (null == categories)
                categories = new ArrayList<>(1);
            category.subCategories = categories;
        }
        ArrayList<Organization> organizations = category.organizations;

        if (null == organizations)
        {
            organizations = DbOrganizationsHelper.getOrganizations(application.getDbHelper().getReadableDatabase(),
                    category.id, DbOrganizationsHelper.PAGE_SIZE, 0);
            if (null == organizations)
                organizations = new ArrayList<>(1);
            category.organizations = organizations;
        }

        if (null != categories && !categories.isEmpty())
        {
            CategoriesFragment f = new CategoriesFragment();
            f.setData(categories, category, category.name);
            activity.openFragment(activity, f, CategoriesFragment.NAME, R.id.content_frame);
        }
        else
        {
            OrganizationsFragment organizationsFragment = new OrganizationsFragment();
            organizationsFragment.setData(category, null);
            activity.openFragment(activity, organizationsFragment, OrganizationsFragment.NAME,
                    //R.id.content_details
                    R.id.content_frame);
        }
        /*
        if (0 == category.parent_id)
        {
            ArrayList<Category> cats = (null != category.subCategories) ? category.subCategories :
            DbDataHelper.getCategories(application.getDbHelper().getReadableDatabase(), category);
            if (null != cats)
            {
                openCategories(category, activity, cats);
            }
        }
        else
        {
            OrganizationsFragment organizationsFragment = new OrganizationsFragment();
            if (null == category.organizations) category.organizations = DbDataHelper.getOrganizations(application.getDbHelper().getReadableDatabase(), category.id);
            organizationsFragment.setData(category, null);

            activity.openFragment(activity, organizationsFragment, OrganizationsFragment.NAME,
                    //R.id.content_details
                    R.id.content_frame);
        }
        */

        //        sendEventStatistics(
        //                new TrackerEvent(
        //                        getTracker(activity),
        //                        ITrackerEvents.CATEGORY_LIST,
        //                        ITrackerEvents.ACTION_OPEN,
        //                        ITrackerEvents.LABEL_ACTION_SECTION + "-" + "title", // TODO change 'title' to real title
        //                        (0 == category.parent_id) ? ITrackerEvents.LABEL_TARGET_CATEGORY : ITrackerEvents.LABEL_TARGET_ORGANIZATION,
        //                        null,
        //                        1
        //                )
        //        );

        return true;
    }

    public void setData(ArrayList<Category> categories, Category parent, CharSequence title)
    {
        this.categories = categories;
        this.category = parent;
        this.title = title;
    }

    /*
    @Override
    public boolean onClose()
    {
        if (null != adapter)
        {
            adapter.resetFilter();
        }
        return true;
    }*/

    @Override
    public boolean onQueryTextChange(String query)
    {
        if (null != adapter)
        {
            adapter.getFilter().filter(query);
        }
        return true;
    }

    @Override
    protected String getTrackerCategory()
    {
        return (null == category) ? ITrackerEvents.CATEGORY_SECTION : ITrackerEvents.CATEGORY_PAGES;
    }

    @Override
    protected long getTrackerValue()
    {
        return (null == category) ? super.getTrackerValue() : category.id;
    }


    protected ArrayList<Organization> getOrganizationsForMap(Category parent)
    {
        Activity activity = getActivity();
        if (null == activity)
            return null;

        if (activity.isFinishing()) return null;

        //final OrganizationsAdapter organizationsAdapter = getOrganizationsAdapter();
        //if (null != organizationsAdapter)
        //{
        //    if (!еndLessScrollListener.isEnabled() && !еndLessScrollListener.isLoading())
        //        return organizationsAdapter.getItems();
        //}

        ArrayList<Organization> organizations = DbOrganizationsHelper.getOrganizationsByParentCategoryWithLocation(((_Application) activity.getApplication()).getDbHelper().getReadableDatabase(), (null != parent) ? parent.subCategories : null, (null != organizationsAdapter) ? organizationsAdapter.getFilterString() : null, (null != parent) ? parent.id : 0);
        return organizations;
    }


    // Курск. Одежда
}



//http://stackoverflow.com/questions/12242983/how-to-find-end-of-scroll-value-for-horizontalscrollview
//http://stackoverflow.com/questions/19144961/detect-end-of-fling-on-scrollview