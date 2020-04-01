package ru.mycity.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.database.DbOptionsHelper;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.maps.MapControllerFactory;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.mycity.utils.MapUtils;
import ru.utils.Density;

public class OrganizationsMapFragment extends MapFragment implements RadioGroup.OnCheckedChangeListener
{
    public static final String NAME = "OrganizationsMapFragment";
    protected CharSequence title;
    private ArrayList<Organization> organizations;
    private String trackerLabel;
    private SegmentedGroup mapSwitch;
    private MapUtils.MapRect mapRect;
    //private JSONArray settings;

    private SegmentedGroup segmentedGroup;

    public OrganizationsMapFragment()
    {
    }

    public void setData(CharSequence title, Category category, ArrayList<Organization> organizations,
                        String trackerLabel,
                        MapUtils.MapRect rect)
    {
        boolean clustering = false;
        if (null != organizations && organizations.size() > 25)
            clustering = true;
        this.m_mapController = MapControllerFactory.newInstance(this, clustering);
        this.title = title;
        this.organizations = (null != organizations) ? organizations : new ArrayList<Organization>();
        this.trackerLabel = trackerLabel;
        this.category = category;
        this.mapRect = rect;
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.organizations_map, container, false);
        initMap(inflater, rootView, savedInstanceState);
        return rootView;
    }


    private void initMap(LayoutInflater inflater, View rootView, Bundle savedInstanceState)
    {
        m_mapController.init(savedInstanceState, inflater.getContext(), (ViewGroup) rootView.findViewById(R.id.scrollView), (ViewGroup) rootView);
    }

    @Override
    public void onMapReady()
    {
        if (null != m_mapController.getView())
        {
            m_mapController.addMarkers(organizations, true);
        }

        if (null != segmentedGroup)
        {
            RadioButton button = (RadioButton) segmentedGroup.getChildAt(0);
            segmentedGroup.setOnCheckedChangeListener(null);
            button.setChecked(true);
            segmentedGroup.setOnCheckedChangeListener(this);
        }

        setPosition((null != mapRect) ? mapRect.count : 1);
    }

    private void setPosition(int count)
    {
        if (1 == count)
        {
            if (null != organizations && !organizations.isEmpty())
            {
                for (Organization o : organizations)
                {
                    if (o.hasCoordinates)
                    {
                        m_mapController.setPosition(o.latitude, o.longitude, 15);
                        return;
                    }
                }
            }

            Activity activity = getActivity();
            _Application application = (null != activity) ? (_Application) activity.getApplication() : null;
            if (null != application)
            {
                //double lat = OptionsUtils.getDouble(settings, "center_lat", 0);
                SQLiteDatabase db = application.getDbHelper().getReadableDatabase();
                double lat = DbOptionsHelper.getDouble(db, "center_lat", 0);
                //double lon = OptionsUtils.getDouble(settings, "center_lon", 0);
                double lon = DbOptionsHelper.getDouble(db, "center_lon", 0);

                int latitude  = (int) (lat * 1E6);
                int longitude = (int) (lon * 1E6);
                m_mapController.setPosition(latitude, longitude, 13.2f);
            }
            return;
        }

        m_mapController.setMapRect(mapRect.minLat, mapRect.minLon, mapRect.maxLat, mapRect.maxLon, 50);
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(getLatLng(mapRect.minLat, mapRect.minLon), getLatLng(mapRect.maxLat, mapRect.maxLon)), 50));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_MAPS, ITrackerEvents.ACTION_OPEN, null, null, trackerLabel));
    }

    @Override
    protected CharSequence getTitle()
    {
        return title;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        boolean working = R.id.working == checkedId;
        m_mapController.switchMarkers(working);
    }


    @Override
    public void onLinkClick(URLSpan link, View widget)
    {
        super.onLinkClick(link, widget);
        long val = (null != category) ? category.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(widget), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_SITE_CLICK, ITrackerEvents.LABEL_ACTION_OPEN_LINK, ITrackerEvents.LABEL_TARGET_CATEGORY, "entity_id=" + val + ",url=" + link.getURL(), val));
    }

    private void removeMapSwitch()
    {
        if (null != mapSwitch)
        {
            ViewGroup v = (ViewGroup) mapSwitch.getParent();
            if (null != v)
            {
                v.removeView(mapSwitch);
            }
        }
    }

    private void showMapSwitch(boolean visible)
    {
        if (null != mapSwitch)
        {
            final int state = visible ? View.VISIBLE : View.GONE;
            if (state != mapSwitch.getVisibility())
            {
                mapSwitch.setVisibility(state);
            }
        }
    }


    @Override
    public void onDetach()
    {
        removeMapSwitch();
        super.onDetach();
    }

    public Spannable getSpannableDescription(String text, Object context)
    {
        Organization o = m_mapController.find(context);
        return getDescription(text, o, true);
    }

    @Override
    public void onAttach(Activity activity)
    {
        /*
        android.support.v7.app.ActionBar appbar = ((AppCompatActivity) activity).getSupportActionBar();
        if (null != appbar)
        {
            View v = appbar.getCustomView();
            if (null != v)
            {
                v = v.findViewById(R.id.appbar_layout);
            }
        }*/
        View v = activity.getWindow().findViewById(R.id.appbar_layout);
        if (null != v)
        {
            //title = "test";
            segmentedGroup = (SegmentedGroup) activity.getLayoutInflater().inflate(R.layout.map_segment_group, null);
            /*
            segmentedGroup.setTintColor(
                    Color.WHITE,
                    segmentedGroup.getContext().getResources().getColor(R.color.radio_button_selected_color)
                    //Color.parseColor("#FF0000"),
                    //Color.parseColor("#00FF00")
            );
            */

            //http://www.kamonway.com/%E0%B8%97%E0%B8%B3segmented%E0%B9%83%E0%B8%99android/
            ((ViewGroup) v).addView(mapSwitch = segmentedGroup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) segmentedGroup.getLayoutParams();
            if (null != params)
            {
                //title = "test";
                Resources r = getResources();
                params.leftMargin = Density.getIntValue(r, 18);
                params.rightMargin = params.leftMargin;
                params.topMargin = Density.getIntValue(r, 4);
                params.bottomMargin = Density.getIntValue(r, 6);
            }
        }
        super.onAttach(activity);
    }


    @Override
    public void onResume()
    {
        showMapSwitch(true);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        showMapSwitch(false);
        super.onPause();
    }


    public boolean onOrganizationClick(Organization o)
    {
        //marker.hideInfoWindow();

        //removeMapSwitch();
        //showMapSwitch(false);
        /*
        if (null != mapSwitch)
        {
            mapSwitch.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    OrganizationFragment organizationFragment = new OrganizationFragment();
                    organizationFragment.setData(o, category, gpsEnabled);
                    openDetailFragment(organizationFragment, OrganizationFragment.NAME);
                }
            }, 100);
        }
        */
        FragmentActivity activity = getActivity();
        if (null == activity)
            return false;


        if (o.phones_count > 0 && null == o.phones)
        {
            resolveOrganizationPhones(o);
        }

        OrganizationFragment organizationFragment = new OrganizationFragment();
        organizationFragment.setData(o, category, null);
        //openDetailFragment(organizationFragment, OrganizationFragment.NAME);
        saveView = true;
        openMasterFragment(organizationFragment, OrganizationFragment.NAME);
        return true;
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

    @Override
    protected String getName()
    {
        return NAME;
    }

    @Override
    protected boolean requestPermissionIfNeededOnCreateMap()
    {
        return true;
    }

}