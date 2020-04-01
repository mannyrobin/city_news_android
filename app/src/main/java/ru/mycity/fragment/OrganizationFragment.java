package ru.mycity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import ru.mycity.IPermissionCodes;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.data.OrganizationPhone;
import ru.mycity.maps.MapControllerFactory;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.Density;
import ru.utils.PermissionsUtils;


public class OrganizationFragment extends MapFragment implements RadioGroup.OnCheckedChangeListener//, View.OnClickListener//, ViewPager.OnPageChangeListener
{
    public static final String NAME = "OrganizationFragment";
    private final String empty = " -";
    protected ViewGroup mapContainer;
    private Organization organization;
    private Category category;

    //private ViewPager infoViewPager;
    private SegmentedGroup segmentedGroup;
    private ViewGroup segmentedDataGroup;
    private String tempPhone;
    //private int checkedPosition;
    private int[] segmentedDataGroups = {R.id.contacts, R.id.info, R.id.working_time};
    private boolean zoomed = false;
    private boolean doSendMapOpenStat = true;
    private Object marker;

    @Override
    public void onLinkClick(URLSpan link, View widget)
    {
        super.onLinkClick(link, widget);
        long val = (null != organization) ? organization.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(widget), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_SITE_CLICK, ITrackerEvents.LABEL_ACTION_OPEN_LINK, ITrackerEvents.LABEL_TARGET_ORGANIZATION, "entity_id=" + val + ",url=" + link.getURL(), val));
    }

    @Override
    protected boolean requestPermissionIfNeededOnCreateMap()
    {
        return false;
    }

    @Override
    protected String getName()
    {
        return NAME;
    }

    private MenuItem zoomItem;

    public OrganizationFragment()
    {
        setHasOptionsMenu(true);
    }

    public void setData(Organization item, Category category, String searchWord)
    {
        this.m_mapController = MapControllerFactory.newInstance(this, false);
        this.organization = item;
        this.category = category;
        this.searchContext = searchWord;

    }

    @Override
    protected CharSequence getTitle()
    {
        return (null != organization) ? organization.title : null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (R.id.action_zoom == item.getItemId())
        {
            onZoom();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onZoom()
    {
        zoomed = !zoomed;
        if (null != zoomItem)
        {
            if (!zoomed) 
            {   
               zoomItem.setIcon(R.drawable.arrow_expand);
            }
            else 
            {
                if (null != m_mapController && !myLocationEnabled) //!mGoogleMap.getUiSettings().isMyLocationButtonEnabled())
                {                     
                    if (isMapPermissionsEnabled())
                    {
                        enableMyLocation();
                    }
                    else
                    {
                        requestMapPermissions();
                    }
                }
                zoomItem.setIcon(R.drawable.arrow_collapse);
            }
        }

        if (null != mapContainer)
        {
            if (m_mapController.isChangeMarkerVisibilitySupported())
            {
                if (null != marker) m_mapController.setMarkerVisible(marker, false);

                //switchMap();
                m_mapController.getView().postDelayed(new Runnable()
                                                      {
                                                          @Override
                                                          public void run()
                                                          {
                                                              switchMap();
                                                              //marker.setVisible(true);
                                                          }
                                                      },
                        //40
                        80);
            }
            else
            {
                switchMap();
            }
        }
        if (true == zoomed && true == doSendMapOpenStat)
        {
            long val = (null != organization) ? organization.id : 0;
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(this.getContext()), ITrackerEvents.CATEGORY_MAPS, ITrackerEvents.ACTION_OPEN, ITrackerEvents.LABEL_ACTION_MAP, ITrackerEvents.LABEL_TARGET_ORGANIZATION, "entity_id=" + val, val));
            doSendMapOpenStat = false;
        }
    }

    void switchMap()
    {
        if (null == mapContainer)
        {
            return;
        }
        ViewGroup view = (ViewGroup) mapContainer.getParent();
        int count = view.getChildCount();

        for (int i = 0; i < count; i++)
        {
            View v = view.getChildAt(i);
            if (v == mapContainer)
            {
                v.getLayoutParams().height = zoomed ? ViewGroup.LayoutParams.MATCH_PARENT : getMapHeight();
            }
            else
            {
                v.setVisibility(zoomed ? View.GONE : View.VISIBLE);
            }
        }

        if (null != marker)
            m_mapController.setMarkerVisible(marker, true);
    }

    private int getMapHeight()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels / 4;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        final int len = segmentedDataGroups.length;
        for (int i = 0; i < len; i++)
        {
            int id = segmentedDataGroups[i];
            View v = segmentedDataGroup.findViewById(id);
            //TODO, uncomment, preview fault
            v.setSaveEnabled(false);

            if (id == checkedId)
            {
                Object tag = v.getTag();
                if (null == tag)
                {
                    initPage(id, v);
                    v.setTag(Boolean.TRUE);
                }
                v.setVisibility(View.VISIBLE);
            }
            else
            {
                v.setVisibility(View.GONE);
            }
        }
        /*
        int pos = -1;
        switch (checkedId)
        {
            case R.id.contacts:
                pos = 0;
                break;

            case R.id.info:
                 pos = 1;
                 break;

            case R.id.working_time:
                 pos = 2;
                 break;
        }
        checkedPosition = pos;
        if (null != infoViewPager && pos != infoViewPager.getCurrentItem())
            infoViewPager.setCurrentItem(pos, false);
        */
    }

    private void initPage(int id, View v)
    {
        switch (id)
        {
            case R.id.contacts:
                initContactsPage(v, organization);
                break;

            case R.id.info:
                initInfoPage(v, organization);
                break;

            case R.id.working_time:
                initWorkingTimePage(v, organization);
                break;
        }
    }

    private void initContactsPage(View rootView, Organization organization)
    {
        String address = organization.address;
        TextView tv = (TextView) rootView.findViewById(R.id.address);
        if (null != address && 0 != address.length())
        {
            tv.setText(address);
        }
        else
        {
            tv.setText(empty);
        }

        ViewGroup vg = (ViewGroup) rootView;
        int count = vg.getChildCount();
        int index = 0;
        for (; index < count; index++)
        {
            if (vg.getChildAt(index).getId() == R.id.phone_title)
            {
                break;
            }
        }

        index++;
        List<OrganizationPhone> phones = organization.phones;
        if (null != phones && !phones.isEmpty())
        {
            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    makePhoneCall(v.getTag().toString(), true);
                }
            };
            boolean isWorkingNow = organization.isWorkingNow();
            //int iconId = isWorkingNow ? R.drawable.call : R.drawable.call_inactive;
            int iconId = isWorkingNow ? R.drawable.call : 0;

            for (OrganizationPhone op : phones)
            {
                tv = addPhoneTextView(vg, index++, iconId, op);
                tv.setTag(op.phone);
                tv.setOnClickListener(listener);
            }
        }
        else
        {
            addPhoneTextView(vg, index, 0, null);
        }

        String site = organization.site;
        tv = (TextView) rootView.findViewById(R.id.site);
        if (null != site && 0 != site.length())
        {
            tv.setText(site);
        }
        else
        {
            tv.setText(empty);
        }
    }

    private void initInfoPage(View rootView, Organization organization)
    {
        String s;
        if (null != organization && null != (s = organization.info))
        {
            TextView tv = (TextView) rootView.findViewById(android.R.id.text1);
            tv.setText(s);
        }
    }

    private void initWorkingTimePage(View rootView, Organization organization)
    {
        TextView tv = (TextView) rootView.findViewById(android.R.id.text1);

        if (organization.work_always)
        {
            tv.setText(getText(R.string.round_the_clock));
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            final String[] values = {organization.work_monday, organization.work_tuesday, organization.work_wednesday, organization.work_thursday, organization.work_friday, organization.work_saturday, organization.work_sunday};
            final String[] days = getContext().getResources().getStringArray(R.array.days);
            final int len = values.length;
            for (int i = 0; i < len; i++)
            {
                String val = values[i];
                if (null != val && 0 != val.length())
                {
                    sb.append(days[i]).append(':').append(' ').append(val).append('\n');
                }
            }
            tv.setText(sb);
        }
    }

    void makePhoneCall(final String phone, boolean checkPermissions)
    {
        if (null == phone)
        {
            return;
        }
        if (0 == phone.length())
        {
            return;
        }
        tempPhone = phone;
        Activity activity = getActivity();
        if (null == activity)
        {
            return;
        }

        if (checkPermissions)
        {
            if (!PermissionsUtils.checkPermission(activity, android.Manifest.permission.CALL_PHONE))
            {
                requestPermissions(NAME, new String[]{android.Manifest.permission.CALL_PHONE}, IPermissionCodes.PERMISSION_CALL_PHONE_RC);
                return;
            }
        }
        if (true == ru.utils.PhoneUtils.makeCall(activity, phone))
        {
            long val = (null != organization) ? organization.id : 0;
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(activity), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_CALL, ITrackerEvents.LABEL_ACTION_CALL, ITrackerEvents.LABEL_TARGET_ORGANIZATION, "entity_id=" + val + ",phone=" + phone, val));
        }
    }

    private TextView addPhoneTextView(ViewGroup vg, int index, int iconId, OrganizationPhone phoneInfo)
    {
        final Context context = vg.getContext();
        TextView tv = new TextView(context);

        final int topIndent;
        if (null == phoneInfo)
        {
            tv.setTextSize(16);
            tv.setTextColor(Color.BLACK);
            tv.setText(empty);
            topIndent = Density.getIntValue(context, 4);
        }
        else
        {
            topIndent = Density.getIntValue(context, 4);

            //tv.setTextSize(20);
            //tv.setTextColor(getResources().getColor(R.color.text_label_color));
            SpannableStringBuilder sb = new SpannableStringBuilder();
            if (iconId != 0)
            {
                sb.append(' ');
                sb.setSpan(new ImageSpan(context, iconId), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.append(' ');
            }
            sb.append(phoneInfo.phone);
            //new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(new AbsoluteSizeSpan(20, true), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_label_color)), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String desc = phoneInfo.description;

            /*
            if (null == desc)
                desc = "Cтолики";
            if (0 == desc.length())
                desc = "Cтолики";*/

            if (null != desc && 0 != desc.length())
            {
                int len = sb.length();
                sb.append(' ').append(' ');
                sb.append('-');
                sb.append(' ');
                sb.append(desc);
                sb.setSpan(new AbsoluteSizeSpan(16, true), len, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.lt_gray_title_color)), len, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv.setText(sb);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = topIndent;
        lp.leftMargin = Density.getIntValue(context, 12);
        lp.rightMargin = lp.leftMargin;
        vg.addView(tv, index, lp);
        return tv;
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
                    if (granted)
                    {
                        makePhoneCall(tempPhone, false);
                    }
                }

                break;

                case IPermissionCodes.PERMISSION_CHECK_LOCATION_RC:
                {
                    if (PermissionsUtils.isAllPermissionsGranted(permissions, grantResults))
                    {
                        enableMyLocation();
                    }
                }
                break;
            }
        }
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.organization_fragment, container, false);

        initMap(inflater, rootView, savedInstanceState);
        //http://stackoverflow.com/questions/6546108/mapview-inside-a-scrollview

        CharSequence path = getPath();
        if (null != path)
        {
            TextView tvPath = (TextView) rootView.findViewById(R.id.text_path);
            tvPath.setText(path);
        }

        /*
        infoViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        if (null != infoViewPager)
        {
            final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), item);
            infoViewPager.setAdapter(adapter);
        }
        */

        /*
        TextView tv = (TextView) rootView.findViewById(android.R.id.text2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < 16; i++)
        {
            sb.append(i).append(' ').append("1111111111\n");
        }
        tv.setText(sb); */

        segmentedDataGroup = (ViewGroup) rootView.findViewById(R.id.segmented_data);
        segmentedGroup = (SegmentedGroup) rootView.findViewById(R.id.segment_group);
        RadioButton button = (RadioButton) segmentedGroup.getChildAt(0);

        //button.setChecked(true);
        //segmentedGroup.setOnCheckedChangeListener(this);

        segmentedGroup.setOnCheckedChangeListener(this);
        button.setChecked(true);

        //if (null != infoViewPager)
        //    infoViewPager.addOnPageChangeListener(this);
        return rootView;
    }

    private void initMap(LayoutInflater inflater, View rootView, Bundle savedInstanceState)
    {
        //point = (null != organization && organization.hasCoordinates) ? getLatLng(organization.latitude, organization.longitude) : null;
        boolean pointable = (null != organization && organization.hasCoordinates);
        mapContainer = (ViewGroup) rootView.findViewById(R.id.map_container);
        mapContainer.getLayoutParams().height = getMapHeight(); //Density.getIntValue(inflater.getContext(), 200);
        //if (null != container)//TEMP DEBUG
        if (false == pointable)
        {
            //container.setVisibility(View.GONE);
            ImageView imageView = new ImageView(inflater.getContext());
            setImage(imageView);

            mapContainer.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        else
        {
            m_mapController.init(savedInstanceState, inflater.getContext(), (ViewGroup) rootView.findViewById(R.id.scrollView), mapContainer);
        }
    }

    private void setImage(ImageView imageView)
    {
        if (null == organization)
            return;



        String imageUrl = getImageUrl();
        if (null != imageUrl && 0 != imageUrl.length())
        {
            DisplayImageOptions displayOptions;
            _Application application = (_Application) imageView.getContext().getApplicationContext();
            ImageLoader imageLoader = application.getImageLoader();
            displayOptions = application.generateDefaultImageOptionsBuilder()
                    .cacheInMemory(false)
                    //.showImageOnLoading(R.drawable.cover)
                    .showImageForEmptyUri(R.drawable.cover)
                    .showImageOnFail(R.drawable.cover)
                    .build();

            imageLoader.displayImage(imageUrl, imageView, displayOptions);
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onImageClick();
                }
            });
        }
        else
        {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.cover);
        }
    }

    private String getImageUrl()
    {
        String imageUrl = organization.image;
        if (null != imageUrl && 0 != imageUrl.length())
        {
            //TODO Temp remove in future!
            if ('/' == imageUrl.charAt(0))
                imageUrl = "http://52.28.179.65" + imageUrl;
        }
        return imageUrl;
    }


    void onImageClick()
    {
        ImagePreview fragment = new ImagePreview();
        fragment.setData(getImageUrl(), organization.title);
        openDetailFragment(fragment, ImagePreview.FRAGMENT_TAG);
    }


    private CharSequence getPath()
    {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(getText(R.string.menu_item_categories));
        //sb.setSpan(new UnderlineSpan(), 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(" | ");
        int len = sb.length();
        Category cat = category;
        while (null != cat)
        {
            String name = cat.name;
            if (null != name)
            {
                sb.insert(len, " | ");
                //int l = name.length();
                sb.insert(len, name);
                //sb.setSpan(new UnderlineSpan(), len, len + l, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            cat = cat.parent;
        }
        //len = sb.length();

        String name = (null != organization) ? organization.title : null;

        if (null != name)
        {
            sb.append(name);
            //sb.setSpan(new UnderlineSpan(), len, len + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sb;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        long val = (null != organization) ? organization.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_OPEN, ITrackerEvents.LABEL_ACTION_PAGE, ITrackerEvents.LABEL_TARGET_ORGANIZATION, TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_ENTITY_ID, String.valueOf(val)), TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_CONTEXT, searchContext), TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_TITLE, organization.title))), val));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        if (null != mapContainer && !zoomed)
        {
            mapContainer.getLayoutParams().height = getMapHeight(); //Density.getIntValue(getContext(), 100);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        /*
        MenuItem item = menu.findItem(R.id.action_search);
        if (null != item) item.setVisible(false);
        item = menu.findItem(R.id.action_map);
        if (null != item) item.setVisible(false);
        */
        menu.clear();
        if (null != organization && organization.hasCoordinates)
        {
            inflater.inflate(R.menu.organization, menu);
            zoomItem = menu.findItem(R.id.action_zoom);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public Spannable getSpannableDescription(String spannableText, Object context)
    {
        return getDescription(spannableText, organization, zoomed);
    }


//    @Override
//    public void onMapReady(GoogleMap googleMap)
//    {
//        initGoogleMap(googleMap);
//        MarkerOptions mo = new MarkerOptions().position(point).title(organization.title).icon(getMarkerDrawable());
//
//        marker = googleMap.addMarker(mo);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
//    }


    public boolean onOrganizationClick(Organization organization)
    {
        if (!zoomed)
        {
            onZoom();
            return false;
        }
        else
        {
            return false;
        }
    }


    @Override
    public void onMapReady()
    {
        marker = m_mapController.addMarker(organization,
                //organization.isWorkingNow()
                true
        );

        m_mapController.setPosition(organization.latitude, organization.longitude, 15);

    }
}