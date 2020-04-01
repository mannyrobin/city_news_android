package ru.mycity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.News;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.mycity.utils.DateFormatter;
import ru.mycity.utils.PictureUtils;
import ru.mycity.utils.UrlReplacer;
import ru.utils.CustomLinkMovementMethod;
import ru.utils.IOnLinkClick;

public class NewFragment extends BaseFragment implements IOnLinkClick
{
    public static final String NAME = "NewFragment";
    private CharSequence title;
    private News item;

    public NewFragment()
    {
        setHasOptionsMenu(true);
    }

    public void setData(News item, CharSequence title, String searchWord)
    {
        this.item  = item;
        this.title = title;
        this.searchContext = searchWord;
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
        final View rootView = inflater.inflate(R.layout.new_fragment, container, false);
        if (null == item)
            return rootView;
        CharSequence dateString = item.dateString;
        if (null == dateString)
        {
            DateFormatter dateFormatter = new DateFormatter(inflater.getContext());
            dateString = dateFormatter.formatDateTime(item.date);
            item.dateString = dateString;
        }

        TextView tvDate = (TextView) rootView.findViewById(R.id.item_date);
        if (null != dateString)
        {
            tvDate.setText(dateString);
        }

        String titleText = item.title;
        TextView tvTitle = (TextView) rootView.findViewById(R.id.item_text);
        tvTitle.setText(titleText);

        LinkMovementMethod linkMovementMethod = null;
        String link = item.origin_link;
        if (null != link && 0 != link.length())
        {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(getText(R.string.source));
            sb.append(' ');
            String title = item.origin_title;
            int l = sb.length();
            if (null != title && 0 != title.length())
            {
                sb.append(title);
            }
            else
            {
                sb.append(link);
            }

            sb.setSpan(new URLSpan(link), l, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView tvSource = (TextView) rootView.findViewById(R.id.source);

            tvSource.setLinksClickable(true);
            //tvSource.setMovementMethod(LinkMovementMethod.getInstance());
            linkMovementMethod = new CustomLinkMovementMethod(this);
            tvSource.setMovementMethod(linkMovementMethod);
            tvSource.setText(sb);
        }

        String body = item.body;
        if (null != body && 0 != body.length())
        {
            TextView tvText = (TextView) rootView.findViewById(android.R.id.text1);

            /*
            SpannableString text = SpannableString.valueOf(body);
            if (Linkify.addLinks(text, Linkify.WEB_URLS))
            {
                if (null == linkMovementMethod)
                {
                    linkMovementMethod = new CustomLinkMovementMethod(this);
                }
                tvText.setLinksClickable(true);
                tvText.setMovementMethod(linkMovementMethod);
            }*/


            UrlReplacer replacer = new UrlReplacer(body);
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
        }
        String partialUrl = item.pic;
        if (null != partialUrl && 0 != partialUrl.length())
        {
            displayIcon((ImageView) rootView.findViewById(R.id.item_image), partialUrl);
        }
        return rootView;
    }

    private void displayIcon(ImageView imageView, String partialUrl)
    {
        final Context context = imageView.getContext();
        final int pictureSizeInPixels = context.getResources().getDimensionPixelSize(R.dimen.list_image_size);
        _Application application = (_Application) context.getApplicationContext();
        final ImageLoader imageLoader = application.getImageLoader();
        final String prefix = PictureUtils.generatePrefix(PictureUtils.getPictureValue(context, pictureSizeInPixels));
        final DisplayImageOptions displayOptions = application.generateDefaultImageOptionsBuilder().build();

        final String url = PictureUtils.prepareUrl(partialUrl, prefix);
        imageLoader.displayImage(url, imageView, displayOptions);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        long val = (null != item) ? item.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_OPEN, ITrackerEvents.LABEL_ACTION_PAGE, ITrackerEvents.LABEL_TARGET_NEWS, TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_ENTITY_ID, String.valueOf(val)), TrackerEventHelper.appendLabelParameter(TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_SEARCH_CONTEXT, searchContext), TrackerEventHelper.makeLabelParameter(ITrackerEvents.LABEL_PARAM_TITLE, item.title))), val));
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        //inflater.inflate(R.menu.main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (null != searchMenuItem)
        {
            searchMenuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onLinkClick(URLSpan link, View widget)
    {
        link.onClick(widget);
        long val = (null != item) ? item.id : 0;
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(widget), ITrackerEvents.CATEGORY_PAGES, ITrackerEvents.ACTION_SITE_CLICK, ITrackerEvents.LABEL_ACTION_OPEN_LINK, ITrackerEvents.LABEL_TARGET_EVENT, "entity_id=" + val + ",url=" + link.getURL(), val));
    }
}

