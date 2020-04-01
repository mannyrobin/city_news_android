package ru.mycity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import ru.mycity.R;
import ru.mycity.data.News;
import ru.mycity.utils.DateFormatter;
import ru.mycity.utils.PictureUtils;
import ru.utils.Density;

public class NewsAdapter extends ListAdapter<ViewObject>
{
    public final static int OBJECT_TYPE_OBJECT  	  = 0;
    public final static int OBJECT_TYPE_DATE_HEADER   = 1;
    private ImageSize targetSize;
    private final DateFormatter dateFormatter;
    private ArrayList<News> items;
    private long _prevClearDate = 0;

    public NewsAdapter(final LayoutInflater inflater, final ArrayList<News> news)
    {
        super(inflater, inflater.getContext().getResources().getDimensionPixelSize(R.dimen.list_image_size), null);
        mObjects = convertData(news);
        this.items = news;
        targetSize = new ImageSize(Density.getIntValue(mContext, 100), Density.getIntValue(mContext, 100));
        this.dateFormatter = new DateFormatter(mContext);
    }

    private ArrayList<ViewObject> convertData(ArrayList<News> src)
    {
        if (null == src)
        {
            return new ArrayList<>();
        }

        final long now = System.currentTimeMillis();
        ArrayList<ViewObject> list = new ArrayList<>(src.size() * 3 /2);
        long prevClearDate = this._prevClearDate;
        for (News _news : src)
        {
            NewsObject news = new NewsObject();
            news.type = OBJECT_TYPE_OBJECT;
            news.news = _news;

            long date = _news.date;
            if (date > now)
            {
                date = now;
                news.date = new Date(now);
            }
            long clearDate  = date  / DateUtils.DAY_IN_MILLIS;
            if (clearDate != prevClearDate)
            {
                DateHeaderObject header = new DateHeaderObject();
                header.date = date;
                header.type = OBJECT_TYPE_DATE_HEADER;
                list.add(header);
                prevClearDate = clearDate;
            }
            list.add(news);
        }
        this._prevClearDate = prevClearDate;
        return list;
    }

    @Override
    public void doFilter(String prefix, List<ViewObject> values, ArrayList<ViewObject> newValues)
    {
        final Matcher m = createMatcher(prefix);
        for (ViewObject v : values)
        {
            if (v.type != OBJECT_TYPE_OBJECT)
                continue;
            NewsObject obj = (NewsObject) v;
            News item = obj.news;
            String title = item.title;
            m.reset(title);
            if (m.find())
            {
                newValues.add(v);
            }

            CharSequence date = obj.dateString;
            if (null == date)
            {
                date = dateFormatter.formatDateTime(item.date);
                obj.dateString = date;
            }

            if (null != date)
            {
                m.reset(date);
                if (m.find())
                {
                    newValues.add(v);
                }
            }
        }
        filterSet = true;
    }

    @Override
    public int getItemViewType(int position)
    {
        final ViewObject v = getItem(position);

        if (v.type == OBJECT_TYPE_DATE_HEADER)
            return 0;;

        News item = ((NewsObject) v).news;

        if (0 == item.promoted)
        {
            return item.highlight ? 2 : 1;
        }
        return item.highlight ? 3 : 4;
    }

    @Override
    public int getViewTypeCount()
    {
        return 5;
    }

    private View initObjectView(NewsObject obj, ViewGroup parent)
    {
        News item = obj.news;
        View result = inflater.inflate(R.layout.news_list_item, parent, false);
        TextView tvText = (TextView) result.findViewById(R.id.item_text);

        if (item.highlight)
        {
            result.setBackgroundResource(R.drawable.li_frame_highlight);
        }
        else
        {
            result.setBackgroundResource(R.drawable.li_frame);
        }

        if (item.promoted > 0)
        {
            final Context context = result.getContext();
            ImageView pin = new ImageView(context);
            pin.setAdjustViewBounds(true);
            pin.setScaleType(ImageView.ScaleType.CENTER_CROP);
            pin.setId(android.R.id.icon2);

            pin.setImageResource(R.drawable.sponsored);
            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvText.getLayoutParams();
            //params.addRule(RelativeLayout.LEFT_O  F, android.R.id.icon2);
            //params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.item_text);

            //params.addRule(RelativeLayout.LEFT_OF, R.id.item_image);
            //params.alignWithParent = true;

            int w = context.getResources().getDimensionPixelOffset(R.dimen.pin_width);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, w);
            //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.item_text);

            params.leftMargin = Density.getIntValue(context, 6);
            params.rightMargin = Density.getIntValue(context, 6);
            params.topMargin = Density.getIntValue(context, 2);

            ((ViewGroup) result).addView(pin, params);
        }

        ViewHolder holder = new ViewHolder(tvText, (TextView) result.findViewById(R.id.item_date), (ImageView) result.findViewById(R.id.item_image));
        result.setTag(holder);
        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final View result;

        final ViewObject  obj = mObjects.get(position);
        //final News item = mObjects.get(position);

        if (null == convertView)
        {
           if (OBJECT_TYPE_OBJECT == obj.type)
               result = initObjectView((NewsObject) obj, parent);
            else
               result = initHeaderObject();
        }
        else
        {
            //holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if (OBJECT_TYPE_OBJECT == obj.type)
            setObjectView((NewsObject) obj, (ViewHolder) result.getTag(), position);
        else
            setDateHeaderView((DateHeaderObject) obj,(TextView) result);
        return result;
    }

    private View initHeaderObject()
    {
        final TextView tv;
        tv = new TextView(mContext);
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        //tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(Density.getIntValue(mContext, 12), 0, 0, 0);
        return tv;
    }

    private void setDateHeaderView(DateHeaderObject header, TextView tv)
    {
        CharSequence text = dateFormatter.formatDate(header.date);
        if (text.length() > 0)
           tv.setText(text);
    }

    private void setObjectView(NewsObject obj, ViewHolder holder, int position)
    {
        News item = obj.news;
        CharSequence dateString = obj.dateString;
        if (null == dateString)
        {
            //dateString = dateFormatter.formatDateTime(item.date);
            dateString = dateFormatter.formatTime(item.date);
            obj.dateString = dateString;
        }

        if (null != dateString)
        {
            holder.date.setText(dateString);
        }

        final String titleText = item.title;
        holder.title.setText(titleText);

        String url = item.pic;
        //if (position == 5) //TEMP DEBUG
        //    url+= "DFDF";
        displayImage(holder.icon, url);
    }

    protected void displayImage(ImageView imageView, String partialUrl)
    {
        if (null == imageLoader)
            initImageLoader();
        final String url = PictureUtils.prepareUrl(partialUrl, prefix);
        imageLoader.displayImage(url, new ImageViewAware(imageView), displayOptions, targetSize, null, null);
    }

    public void setNews(ArrayList<News> news)
    {
        this.items = news;
        this._prevClearDate = 0;
        setObjects(convertData(news));
    }

    public News getItemAtPosition(int position)
    {
        ViewObject object = mObjects.get(position);
        return ((NewsObject) object).news;
    }

    private final static class ViewHolder
    {
        public final TextView title;
        public final TextView date;
        public final ImageView icon;

        public ViewHolder(TextView title, TextView date, ImageView icon)
        {
            this.title = title;
            this.date = date;
            this.icon = icon;
        }
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public boolean isEnabled(int position)
    {
        final ViewObject obj = mObjects.get(position);
        return (OBJECT_TYPE_OBJECT == obj.type);
    }

    public int getItemsCount()
    {
        return (null != items) ? items.size() : 0;
    }

    public void addNews(ArrayList<News> objects)
    {
        if (null != objects && !objects.isEmpty())
        {
            ArrayList<ViewObject>  newList = convertData(objects);
            if (null != newList && !newList.isEmpty())
            {
                if (null != items)
                    items.addAll(objects);
                else
                    items = objects;
                if (null == this.mObjects)
                    mObjects = newList;
                else
                    mObjects.addAll(newList);
                notifyDataSetChanged();
            }
        }
    }

    private final static class NewsObject extends ViewObject
    {
        News news;
        Date date;
        CharSequence dateString;
    }

    private final static class DateHeaderObject extends ViewObject
    {
        long date;
    }
}