package ru.mycity.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.concurrent.atomic.AtomicInteger;

import ru.mycity.IPermissionCodes;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Common;
import ru.mycity.data.Event;
import ru.mycity.data.Organization;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.mycity.utils.DateFormatter;
import ru.mycity.utils.PictureUtils;
import ru.mycity.utils.UrlReplacer;
import ru.utils.CustomLinkMovementMethod;
import ru.utils.Density;
import ru.utils.ExpandableTextView;
import ru.utils.IOnLinkClick;
import ru.utils.PermissionsUtils;
import ru.utils.PhoneUtils;

public class EventFragment extends BaseFragment implements View.OnClickListener, IOnLinkClick, ImageLoadingListener
{
    public static final String NAME = "EventFragment";
    private CharSequence title;
    private Event item;
    private int photosCount;
    private AtomicInteger photosIndex;
    private View photosParentView;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayOptions;
    private String tempPhone;
    private boolean isPromotion;
    private CustomLinkMovementMethod linkMovementMethod;

    public EventFragment()
    {
        setHasOptionsMenu(true);
    }

    public void setData(Event item, CharSequence title, boolean isPromotion, String searchWord)
    {
        this.item  = item;
        this.title = title;
        this.isPromotion = isPromotion;
        this.searchContext = searchWord;
    }

    @Override
    protected CharSequence getTitle()
    {
        return title;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            switch (requestCode)
            {
                case IPermissionCodes.PERMISSION_CALL_PHONE_RC:
                {
                    if (PermissionsUtils.isAllPermissionsGranted(permissions, grantResults))
                    {
                        makePhoneCall(tempPhone, false);
                    }
                }
                break;
            }
        }
    }

    private void showPhotosViews(View rootView, boolean visible)
    {
        final int mode = visible ? View.VISIBLE : View.GONE;
        rootView.findViewById(R.id.panelDivider2).setVisibility(mode);
        rootView.findViewById(R.id.photos_title).setVisibility(mode);
        rootView.findViewById(R.id.horzScrollView).setVisibility(mode);
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.event_fragment, container, false);
        if (null == item)
            return rootView;
        String titleText = item.title;
        TextView tvTitle = (TextView) rootView.findViewById(R.id.item_title);
        if (null != titleText)
        {
            tvTitle.setText(titleText);
        }

        CharSequence dateString = item.dateString;
        if (null == dateString)
        {
            DateFormatter dateFormatter = new DateFormatter(inflater.getContext());
            dateString = dateFormatter.formatDateTime(item.date);
            item.dateString = dateString;
        }

        TextView tvDate = (TextView) rootView.findViewById(R.id.item_date);
        if (null != dateString && 0 != dateString.length())
            tvDate.setText(dateString);
        else
        {
            tvDate.setVisibility(View.GONE);
            View divider = rootView.findViewById(R.id.panelDivider);
            ((RelativeLayout.LayoutParams) divider.getLayoutParams()).addRule(RelativeLayout.BELOW, tvTitle.getId());
        }

        String link = item.site;

        //if (null != link && 0 != link.length())
        //    ;
        //else
        //    link = "http:\\ya.ru";

        String title = item.site_title;
        TextView tvSource = (TextView) rootView.findViewById(R.id.source);

        setLink(link, title, tvSource);
        String photos = item.photos;

        boolean showPhotos = false;
        if (null != photos && 0 != photos.length())
        {
            String[] ph = photos.split("\\" + Common.LIST_DELIMITER);
            if (null != ph && ph.length > 0)
            {
                showPhotos = true;
                addPhotos(ph, rootView);
            }
        }

        if (!showPhotos)
        {
            showPhotosViews(rootView, false);
        }

        ExpandableTextView tvText = (ExpandableTextView) rootView.findViewById(android.R.id.text1);
        //View visibleId = tvText;
        String info = item.info;
        if (null != info && 0 != info.length())
        {
            tvText.setLinkColor(getResources().getColor(R.color.text_link));
            tvText.setLess(getText(R.string.extv_less));
            tvText.setMore(getText(R.string.extv_more));

            //SpannableString text = SpannableString.valueOf(info);
            //if (Linkify.addLinks(text, Linkify.WEB_URLS))


            //CharSequence text = info;

            UrlReplacer replacer = new UrlReplacer(info);
            CharSequence text = replacer.doReplace();
            if (replacer.foundUrls())
            {
                if (null == linkMovementMethod)
                {
                    linkMovementMethod = new CustomLinkMovementMethod(this);
                }
                tvText.setLinksClickable(true);
                tvText.setMovementMethod(linkMovementMethod);
            }

            tvText.setText(text);
            tvText.makeExpandable(5);

        }
        else
        {
            tvText.setVisibility(View.GONE);
        }

        String price = item.price;
        String phones = item.phones;

        if (null != phones)
            phones = phones.trim();

        TextView tvPrice = (TextView) rootView.findViewById(R.id.price);
        //TextView tvPhones = (TextView) rootView.findViewById(R.id.phones);
        if (null != price && 0 != price.length() || null != phones && 0 != phones.length())
        {
            if (null != price && 0 != price.length())
            {
                tvPrice.setText(price);
            }
            else
            {
                rootView.findViewById(R.id.price_title).setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);
            }

            if (null != phones && 0 != phones.length())
            {
                View phoneTitle = rootView.findViewById(R.id.phones_title);
                ViewGroup parent = (ViewGroup) phoneTitle.getParent();
                addPhones(phones, parent, phoneTitle);
            }
            else
            {
                rootView.findViewById(R.id.phones_title).setVisibility(View.GONE);
                //tvPhones.setVisibility(View.GONE);
            }
        }
        else
        {
            rootView.findViewById(R.id.price_title).setVisibility(View.GONE);
            tvPrice.setVisibility(View.GONE);
            rootView.findViewById(R.id.phones_title).setVisibility(View.GONE);
            //tvPhones.setVisibility(View.GONE);
            rootView.findViewById(R.id.panelDivider3).setVisibility(View.GONE);
        }

        String partialUrl = item.avatar;
        if (null != partialUrl && 0 != partialUrl.length())
            displayIcon((ImageView) rootView.findViewById(R.id.item_image), partialUrl);

        tvDate = (TextView) rootView.findViewById(R.id.item_date2);
        tvSource = (TextView) rootView.findViewById(R.id.source2);
        if ((null != dateString && 0 != dateString.length()) ||
            (null != link && 0 != link.length()  || (null != title && 0 != title.length()))
            )
        {
            if (null != dateString && 0 != dateString.length())
                tvDate.setText(dateString);
            else
                tvDate.setVisibility(View.GONE);

            setLink(link, title, tvSource);
        }
        else
        {
            View v = (View) tvDate.getParent();
            v.setVisibility(View.GONE);
        }
        return rootView;
    }



    private void setLink(String link, String title, TextView tvSource)
    {
        if (null != link && 0 != link.length())
        {
            SpannableString sb = new SpannableString((null != title && 0 != title.length()) ? title : link);
            //sb.setSpan(new _URLSpan(link, this), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new URLSpan(link), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvSource.setLinksClickable(true);
            //tvSource.setMovementMethod(LinkMovementMethod.getInstance());

            if (null == linkMovementMethod)
            {
                linkMovementMethod = new CustomLinkMovementMethod(this);
            }

            tvSource.setMovementMethod(linkMovementMethod);
            tvSource.setText(sb);
            return;
        }

        if (0 != item.organization_id)
        {
            final _Application application = (_Application) tvSource.getContext().getApplicationContext();
            final Organization organization = DbOrganizationsHelper.getOrganizationById(application.getDbHelper().getReadableDatabase(), item.organization_id);
            if (null != organization)
            {
                SpannableString sb = new SpannableString(organization.title);
                sb.setSpan(new ClickableSpan()
                {
                    @Override
                    public void onClick(View view)
                    {
                        openOrganization(organization);
                    }
                }
                , 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //tvSource.setLinksClickable(true);

                tvSource.setMovementMethod(LinkMovementMethod.getInstance());
                tvSource.setText(sb);
            }
        }

        if (null != title)
            tvSource.setText(title);
    }


    private void addPhotos(String[] ph, View rootView)
    {
        showPhotosViews(rootView, false);
        photosCount = ph.length;
        photosIndex = new AtomicInteger(0);
        photosParentView = rootView;
        final Context context = rootView.getContext().getApplicationContext();
        final int size = getResources().getDimensionPixelSize(R.dimen.list_image_size);

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);

        //layout.setVisibility(View.GONE);
        getImageLoader();
        //imageLoader.clearDiskCache();
        //imageLoader.clearMemoryCache();

        final String prefix = PictureUtils.generatePrefix(PictureUtils.getPictureValue(context, size));
        //for (int i = 0; i < 2; i++)
        for (String p : ph)
        {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.rightMargin = Density.getIntValue(context, 6);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final String url = PictureUtils.prepareUrl(p, prefix);
            imageView.setTag(p);
            imageView.setOnClickListener(this);
            layout.addView(imageView);

            imageLoader.displayImage(url, imageView, displayOptions, this);
        }
    }

    void makePhoneCall(String phone, boolean checkPermissions)
    {
        Activity activity = getActivity();
        if (null == activity)
            return;
        if (checkPermissions)
        {
            if (!PermissionsUtils.checkPermission(activity, android.Manifest.permission.CALL_PHONE))
            {
                tempPhone = phone;
                requestPermissions(NAME, new String[]{android.Manifest.permission.CALL_PHONE}, IPermissionCodes.PERMISSION_CALL_PHONE_RC);
                return;
            }
        }
        if (true == PhoneUtils.makeCall(activity, phone))
        {
            long val = (null != item) ? item.id : 0;
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(activity), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_CALL, ITrackerEvents.LABEL_ACTION_CALL, isPromotion ? ITrackerEvents.LABEL_TARGET_ACTION : ITrackerEvents.LABEL_TARGET_EVENT, "entity_id=" + val + ",phone=" + phone, val));
        }
    }

    private void displayIcon(ImageView imageView, String partialUrl)
    {
        final Context context = imageView.getContext();

        final int pictureSizeInPixels = getResources().getDimensionPixelSize(R.dimen.list_image_size);
        final String prefix = PictureUtils.generatePrefix(PictureUtils.getPictureValue(context, pictureSizeInPixels));

        final String url = PictureUtils.prepareUrl(partialUrl, prefix);
        getImageLoader().displayImage(url, imageView, displayOptions);
    }

    private ImageLoader getImageLoader()
    {
        if (null == imageLoader)
        {
            _Application application = (_Application) getContext().getApplicationContext();
            imageLoader = application.getImageLoader();

            displayOptions = application.generateDefaultImageOptionsBuilder().build();
        }
        return imageLoader;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        long val = (null != item) ? item.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_OPEN, ITrackerEvents.LABEL_ACTION_PAGE, isPromotion ? ITrackerEvents.LABEL_TARGET_ACTION : ITrackerEvents.LABEL_TARGET_EVENT, TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_ENTITY_ID, String.valueOf(val)), TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_CONTEXT, searchContext), TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_TITLE, item.title))), val));
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        //inflater.inflate(R.menu.main, menu);
        menu.clear();
        /*
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (null != searchMenuItem)
            searchMenuItem.setVisible(false);
        */
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v)
    {
        String url = v.getTag().toString();
        ImagePreview fragment = new ImagePreview();
        fragment.setData(url, item.title);
        this.openDetailFragment(fragment, ImagePreview.FRAGMENT_TAG);
        //https://github.com/chrisbanes/PhotoView
    }

    @Override
    public void onLinkClick(URLSpan link, View widget)
    {
        link.onClick(widget);
        long val = (null != item) ? item.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(widget), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_SITE_CLICK, ITrackerEvents.LABEL_ACTION_OPEN_LINK, ITrackerEvents.LABEL_TARGET_EVENT, "entity_id=" + val + ",url=" + link.getURL(), val));
    }

    @Override
    public void onLoadingStarted(String imageUri, View view)
    {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
    {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
    {
        if (photosIndex.incrementAndGet() >= photosCount)
        {
            showPhotosViews(photosParentView, true);
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view)
    {

    }


    private void addPhones(String src, ViewGroup parent, View title)
    {
        String phones = src;

       //String phones = "111\r\n222,333|444";



            /*
            <TextView
                            android:id="@+id/phones"
                            tools:text = "phones"
                            android:textSize="16sp"
                            android:textColor="@color/text_label_color"
                            android:layout_marginTop="2dp"
                            android:layout_width="match_parent"
                            android:drawableLeft="@drawable/call"
                            android:drawablePadding="6dp"
                            android:layout_height="wrap_content"/>
             */
        /*
        String[] ph = phones.split("\\" + Common.LIST_DELIMITER);
        if (null != ph && ph.length > 0)
        {
            final String phone = ph[0];
            tvPhones.setText(phone);
            tvPhones.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    makePhoneCall(phone, true);
                }
            });
        }*/


        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                makePhoneCall(v.getTag().toString(), true);
            }
        };

        String[] ph = phones.split(",|\\r\\n|\\|");

        int index = parent.indexOfChild(title);
        int ndx = index + 1;
        int len;
        if (null != ph && (len = ph.length) > 1)
        {
            addPhones(parent, (TextView) title, listener, ph, ndx);
        }
        else
        {
            if (phones.length() > 15)
            {
                ph = phones.split(" ");
                if (null != ph && (len = ph.length) > 1)
                {
                    int i = 0;
                    for (i = 0; i < len; i++)
                    {
                        if (ph[i].length() < 6)
                            break;
                    }
                    if (i == len)
                    {
                        addPhones(parent, (TextView) title, listener, ph, ndx);
                        return;
                    }
                }
            }
            addPhoneTextView(parent, ndx++, phones, Density.getIntValue(parent.getContext(), 2), listener);
        }
    }

    private void addPhones(ViewGroup parent, TextView title, View.OnClickListener listener, String[] ph, int ndx)
    {
        title.setText(R.string.phones_title);
        final int len  = ph.length;
        for (int i = 0; i < len; i++)
        {
            int topMargin = Density.getIntValue(parent.getContext(), (i == 0) ? 6 : 8);
            String phone = ph[i].trim();
            if (phone.length() > 0)
                addPhoneTextView(parent, ndx++, phone, topMargin, listener);
        }
    }

    private TextView addPhoneTextView(ViewGroup vg, int index, String phone, int topMargin, View.OnClickListener listener)
    {

        final Context context = vg.getContext();
        TextView tv = new TextView(context);
        tv.setTextColor(getResources().getColor(R.color.text_label_color));
        tv.setTextSize(18);
        tv.setCompoundDrawablePadding(Density.getIntValue(context, 8));
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.call, 0, 0, 0);
        tv.setText(phone);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp.topMargin = topMargin;
        //lp.leftMargin = Density.getIntValue(context, 12);
        //lp.rightMargin = lp.leftMargin;
        vg.addView(tv, index, lp);
        tv.setTag(phone);
        tv.setOnClickListener(listener);
        return tv;
    }


    void openOrganization(Organization o)
    {
        OrganizationFragment organizationFragment = new OrganizationFragment();
        organizationFragment.setData(o, null, null);
        openDetailFragment(organizationFragment, OrganizationFragment.NAME);

    }

}

