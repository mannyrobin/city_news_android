package ru.mycity.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.IPermissionCodes;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.adapter.OrganizationsAdapter;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.data.OrganizationPhone;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.mycity.utils.MapUtils;
import ru.utils.EndlessScrollListener;
import ru.utils.PermissionsUtils;

public class OrganizationsFragment extends SearchableListFragment
        implements AdapterView.OnItemClickListener, OrganizationsAdapter.IPhoneClick,
                             EndlessScrollListener.EndlessListener, ILoadData
{

    public final static String NAME = "Organizations";
    protected static final int MSG_SHOW_ORGANIZATION = 0;
    protected static final int MSG_CALL = 1;
    //protected static final int MSG_SHOW_MAP = 2;
    protected Category category;
    private Organization tempOrganization;
    private Handler _handler;
    protected EndlessScrollListener еndLessScrollListener;

    public OrganizationsFragment()
    {
        hasOptionsMenu();
    }

    public void setData(Category category, String searchContext)
    {
        this.category = category;
        this.searchContext = searchContext;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        //MenuItem menuItem = menu.findItem(R.id.action_map);
        //if (null != menuItem)
        //    menuItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected int getMenuResourceId()
    {
        return R.menu.organizations;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Organization organization = (Organization) parent.getAdapter().getItem(position);

        openCategory(view, organization);
    }

    protected void openCategory(View view, Organization organization)
    {
        String sq = searchQuery;
        if (null != searchQuery && 0 != searchQuery.length())
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_SEARCH, getTrackerLabel(), null, TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_QUERY, searchQuery), TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_ENTITY_ID, String.valueOf(organization.id)))));
        }
        searchQuery = null;

        //LocationHelper.getLocationStatus()
        //openOrganization(organization, true, true);
        if (organization.phones_count > 0 && null == organization.phones)
        {
            Handler handler = getHandler();
            Message msg = Message.obtain(handler, MSG_SHOW_ORGANIZATION, organization);
            handler.sendMessage(msg);
        }
        else
        {
            openOrganization(organization, sq);
        }
    }

    protected Handler getHandler()
    {
        if (null == _handler)
        {
            _handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    _handleMessage(msg.what, msg.obj);
                }
            };
        }
        return _handler;
    }

    protected void openOrganization(Organization organization, String searchQuery)
    {
        FragmentActivity activity = getActivity();
        if (null == activity)
            return;
        OrganizationFragment organizationFragment = new OrganizationFragment();

        //_Application app = (_Application) activity.getApplication();
        organizationFragment.setData(organization, category, searchQuery);
        openDetailFragment(organizationFragment, OrganizationFragment.NAME);
        //openMasterFragment(organizationFragment, OrganizationFragment.NAME);
    }

    void _handleMessage(int what, Object obj)
    {
        if (what == MSG_SHOW_ORGANIZATION)
        {
            Organization organization = (Organization) obj;
            if (null != organization)
            {
                if (organization.phones_count > 0 && null == organization.phones)
                {
                    resolveOrganizationPhones(organization);
                }
                openOrganization(organization, null);
            }
            return;
        }

        if (what == MSG_CALL)
        {
            Organization organization = (Organization) obj;
            if (null != organization)
            {
                resolveOrganizationPhones(organization);
                makePhoneCall(organization, true);
            }
            return;
        }
        //if (what == MSG_SHOW_MAP)
        //{
        //    showMap(obj);
        //    return;
        //}
    }

    private boolean resolveOrganizationPhones(Organization organization)
    {
        Activity activity = getActivity();
        if (null == activity)
        {
            return false;
        }
        _Application app = (_Application) activity.getApplication();
        organization.phones = DbOrganizationsHelper.getOrganizationPhones(app.getDbHelper().getReadableDatabase(), organization.id);
        return true;
    }

    private void makePhoneCall(Organization organization, boolean checkPermissions)
    {
        if (null == organization)
        {
            return;
        }
        if (checkPermissions)
        {
            Activity activity = getActivity();
            if (null == activity)
            {
                return;
            }
            if (!PermissionsUtils.checkPermission(activity, android.Manifest.permission.CALL_PHONE))
            {
                tempOrganization = organization;
                requestPermissions(NAME, new String[]{android.Manifest.permission.CALL_PHONE},
                        IPermissionCodes.PERMISSION_CALL_PHONE_RC);
                return;
            }
        }
        final String phone;
        List<OrganizationPhone> phones = organization.phones;
        if (null != phones && !phones.isEmpty())
        {
            phone = phones.get(0).phone;
        }
        else
        {
            phone = null;
        }

        if (null == phone)
        {
            return;
        }
        Activity activity = getActivity();
        if (null != activity)
        {
            if (true == ru.utils.PhoneUtils.makeCall(activity, phone))
            {
                TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(activity), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_CALL, ITrackerEvents.LABEL_ACTION_CALL, ITrackerEvents.LABEL_TARGET_ORGANIZATION, "entity_id=" + organization.id + ",phone=" + phone, organization.id));
            }
        }
    }


    protected ArrayList<Organization> getOrganizationsForMap(Category parent)
    {
        Activity activity = getActivity();
        if (null == activity)
        {
            return null;
        }
        OrganizationsAdapter organizationsAdapter = getOrganizationsAdapter();

        if (null != organizationsAdapter && allOrganizationsLoaded())
        {
            return organizationsAdapter.getItems();
        }
        else
        {
            return DbOrganizationsHelper.getOrganizationsByParentCategoryWithLocation(((_Application) activity.getApplication()).getDbHelper().getReadableDatabase(),
                    (null != parent) ? parent.subCategories : null,
                    (null != organizationsAdapter) ? organizationsAdapter.getFilterString() : null,
                    (null != parent) ? parent.id : 0);
        }
    }


    void showMap(ArrayList<Organization> organizations, Category category)
    {
        showMap(organizations, category, "map-organizations");
    }


    protected void showMap(ArrayList<Organization> organizations, Category category, String trackingLabel)
    {
        final MapUtils.MapRect mapRect;
        if (null != organizations && 0 != organizations.size())
        {
            //long t = System.currentTimeMillis();
            mapRect = MapUtils.calculateMapRect(organizations);
            //Log.d("#v#2", "#v#2 time=" + (System.currentTimeMillis() - t)/10007);
        }
        else
            mapRect = null;

        FragmentActivity activity = getActivity();
        //if (null != mapRect && null != activity)
        if (null != activity)
        {
            OrganizationsMapFragment organizationsMapFragment = new OrganizationsMapFragment();
            organizationsMapFragment.setData(getTitle(), category, organizations, trackingLabel, mapRect);
            //openDetailFragment(organizationsMapFragment, OrganizationsMapFragment.NAME);
            openMasterFragment(organizationsMapFragment, OrganizationsMapFragment.NAME);
        }
    }

    @Override
    protected CharSequence getTitle()
    {
        return (null != category) ? category.name : "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (R.id.action_map == id)
        {
            getMap(item);
            //Handler handler = getHandler();
            //handler.sendMessage(Message.obtain(handler, MSG_SHOW_MAP));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    protected void getMap(final Object obj)
    {
        /*
        View progress = null;
        if (null == category)
        {
            progress = getView().findViewById(R.id.progress);
            progress.setVisibility(View.VISIBLE);

            //FragmentActivity activity = getActivity();
            //if (null != activity)
            //    ((MainActivity) activity).showProgressBar(true);
        }
        */


        //long t = System.currentTimeMillis();
        //ArrayList<Organization> organizations = getOrganizationsForMap(category);

        FragmentActivity activity = getActivity();
        if (null == activity)
            return;
        _Application application = (_Application) activity.getApplication();

        AsyncTask getMapTask = new AsyncTask<Category, Void, ArrayList<Organization> >()
        {
            @Override
            protected void onPreExecute()
            {
                /*
                FragmentActivity activity = getActivity();
                if (null != activity && !activity.isFinishing())
                    ((MainActivity) activity).showProgressBar(true);
                */
            }

            @Override
            protected void onPostExecute(ArrayList<Organization> organizations)
            {
                /*
                FragmentActivity activity = getActivity();
                if (null != activity && !activity.isFinishing())
                    ((MainActivity) activity).showProgressBar(false);
                */

                final MenuItem item = (MenuItem) obj;
                if (null != obj)
                    item.setEnabled(true);

                if (null != organizations)
                    showMap(organizations, _category);
            }

            private Category _category;
            @Override
            protected ArrayList<Organization>  doInBackground(Category... categories)
            {
                _category= (null != categories) ? categories[0]: null;
                ArrayList<Organization> organizations = getOrganizationsForMap(_category);
                return organizations;
            }
        };
        application.getAsyncTaskExecutor().execute(getMapTask, category);


        //Log.d("#v#1", "#v#1 time=" + (System.currentTimeMillis() - t)/1000);

        /*
        if (null == category)
        {
            FragmentActivity activity = getActivity();
            if (null != activity)
                ((MainActivity) activity).showProgressBar(false);
        }*/
        //if (null != progress)
        //    progress.setVisibility(View.GONE);


        //if (null != organizations)
        //    showMap(organizations, category, "map-category");

    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            switch (requestCode)
            {
                case IPermissionCodes.PERMISSION_CALL_PHONE_RC:
                {
                    final boolean granted = PermissionsUtils.isAllPermissionsGranted(permissions, grantResults);
                    getView().post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onPermissionCheckResult(requestCode, granted);

                        }
                    });
                    //handlePermissions(permissions, grantResults);
                }
                break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (null != category)
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_LIST, ITrackerEvents.ACTION_OPEN, getTrackerLabel(), null, TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_ENTITY_ID, String.valueOf(category.id)), TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_NAME, category.name)), category.id));
        }
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.organizations_fragment, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        //listView.setSaveEnabled(false);

        //if (null == adapter)
        {
            ArrayList<Organization> organizations = (null != category) ? category.organizations : null;
            if (null != organizations)
            {
                listView.setAdapter(adapter = new OrganizationsAdapter(inflater, organizations,
                        (null != category) ? category.id: 0,  this));
                listView.setOnItemClickListener(this);
                еndLessScrollListener = new EndlessScrollListener(adapter, this);
                listView.setOnScrollListener(еndLessScrollListener);
            }
        }
        return rootView;
    }

    void onPermissionCheckResult(int requestCode, boolean granted)
    {
        switch (requestCode)
        {
            case IPermissionCodes.PERMISSION_CALL_PHONE_RC:
                if (granted)
                {
                    makePhoneCall(tempOrganization, false);
                }
                break;
        }
    }

    @Override
    public void onClick(Organization organization)
    {
        if (organization.phones_count > 0)
        {
            List<OrganizationPhone> phones = organization.phones;
            if (null != phones && !phones.isEmpty())
            {
                makePhoneCall(organization, true);
            }
            else
            {
                Handler handler = getHandler();
                Message msg = Message.obtain(handler, MSG_CALL, organization);
                handler.sendMessage(msg);
            }
        }
    }

    @Override
    protected String getTrackerLabel()
    {
        return TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_ORGANIZATION);
    }

    @Override
    public void loadData()
    {
        FragmentActivity activity = getActivity();
        if (null == activity)
            return;
        _Application app =  (_Application) activity.getApplicationContext();

        //a ddFooterView();
        //int count = adapter.getCount();
        //long allCount = DbDataHelper.getCount(app.getDbHelper().getReadableDatabase(), DbNewsHelper.TABLE_NAME);
        //if (0 == allCount)
        //    return;

        OrganizationsAdapter o = getOrganizationsAdapter();
        long count = o.getCount();
        String filter = o.getFilterString();
        long categoryId = (null != category) ? category.id : 0;

        app.getAsyncTaskExecutor().execute(new LoadDataAsyncTask(app, this, filter, getSubCategories(), categoryId, getOrganizationsPageSize(), count));
    }

    protected int getOrganizationsPageSize()
    {
        return DbOrganizationsHelper.PAGE_SIZE;
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

    protected OrganizationsAdapter getOrganizationsAdapter()
    {
        return (OrganizationsAdapter) adapter;
    }

    @Override
    public void onLoad(ArrayList<?> objects)
    {
        ArrayList<Organization> organizations = (ArrayList<Organization>) objects;
        //listView.removeFooterView(footer);

        if (null != organizations && !organizations.isEmpty())
        {
            getOrganizationsAdapter().addOrganizations(organizations);
            setLoadingOnScroll(false);
        }
        else
        {
            setLoadingOnScroll(false);
            setEnableLoadingOnScroll(false);
        }
    }

    protected ArrayList<Category> getSubCategories()
    {
        return null;
    }



    private final static class LoadDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Organization>>
    {
        private final _Application application;
        private final ILoadData callback;

        private final long categoryId;
        private final long count;
        private final String filter;
        private final int limit;
        private ArrayList<Category> subcategories;

        public LoadDataAsyncTask(_Application application, ILoadData callback, String filter, ArrayList<Category> subcategories, long categoryId, int limit, long count)
        {
            this.application = application;
            this.callback = callback;
            this.categoryId = categoryId;
            this.count      = count;
            this.limit      = limit;
            this.subcategories = subcategories;
            this.filter = filter;
        }

        @Override
        protected ArrayList<Organization> doInBackground(Void... params)
        {
            if (null != filter && 0 != filter.length())
                return DbOrganizationsHelper.findOrganizations(application.getDbHelper().getReadableDatabase(),
                        filter, subcategories, categoryId, limit, count);
            else
                return DbOrganizationsHelper.getOrganizations(application.getDbHelper().getReadableDatabase(), categoryId, limit, count);
        }

        @Override
        protected void onPostExecute(ArrayList<Organization> result)
        {
            if (null != callback)
                callback.onLoad(result);
        }
    }

    protected boolean allOrganizationsLoaded()
    {
        if (null == еndLessScrollListener) return  false;
        if (!еndLessScrollListener.isEnabled())
            return true;
        return false;
    }

    protected  void setLoadingOnScroll(boolean enable)
    {
        if (null != еndLessScrollListener)
            еndLessScrollListener.setLoading(enable);
    }

    protected  void setEnableLoadingOnScroll(boolean enable)
    {
        if (null != еndLessScrollListener)
            еndLessScrollListener.setEnabled(enable);
    }


}
