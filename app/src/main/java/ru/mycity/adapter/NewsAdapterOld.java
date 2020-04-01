package ru.mycity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import ru.mycity.R;
import ru.mycity.data.News;
import ru.mycity.utils.DateFormatter;
import ru.utils.Density;

public class NewsAdapterOld extends ListAdapter<News>
{
    private final DateFormatter dateFormatter;

    public NewsAdapterOld(final LayoutInflater inflater, final ArrayList<News> items)
    {
        super(inflater, inflater.getContext().getResources().getDimensionPixelSize(R.dimen.list_image_size), items);
        this.dateFormatter = new DateFormatter(mContext);
    }

    @Override
    public void doFilter(String prefix, List<News> values, ArrayList<News> newValues)
    {
        final Matcher m = createMatcher(prefix);
        for (News item : values)
        {
            String title = item.title;
            m.reset(title);
            if (m.find())
            {
                newValues.add(item);
            }

            CharSequence date = item.dateString;
            if (null == date)
            {
                date = dateFormatter.formatDateTime(item.date);
                item.dateString = date;
            }

            if (null != date)
            {
                m.reset(date);
                if (m.find())
                {
                    newValues.add(item);
                }
            }
        }
        filterSet = true;
    }

    @Override
    public int getItemViewType(int position)
    {
        final News item = getItem(position);
        if (0 == item.promoted)
        {
            return item.highlight ? 1 : 0;
        }

        return item.highlight ? 2 : 3;
    }

    @Override
    public int getViewTypeCount()
    {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        final View result;

        final News item = getItem(position);

        if (null == convertView)
        {
            result = inflater.inflate(R.layout.news_list_item_old, parent, false);
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
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvText.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, android.R.id.icon2);

                int w = context.getResources().getDimensionPixelOffset(R.dimen.pin_width);
                params = new RelativeLayout.LayoutParams(w, w);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.leftMargin = Density.getIntValue(context, 6);
                params.rightMargin = Density.getIntValue(context, 6);
                params.topMargin = Density.getIntValue(context, 6);

                ((ViewGroup) result).addView(pin, params);
            }

            holder = new ViewHolder(tvText, (TextView) result.findViewById(R.id.item_date), (ImageView) result.findViewById(R.id.item_image));
            result.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        CharSequence dateString = item.dateString;
        if (null == dateString)
        {
            dateString = dateFormatter.formatDateTime(item.date);
            item.dateString = dateString;
        }

        if (null != dateString)
        {
            holder.date.setText(dateString);
        }

        final String titleText = item.title;
        holder.title.setText(titleText);

        displayImage(holder.icon, item.pic);
        return result;
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

    public News getItemAtPosition(int position)
    {
        return mObjects.get(position);
    }

}