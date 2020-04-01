package ru.mycity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mycity.R;
import ru.mycity.data.Event;
import ru.mycity.utils.DateFormatter;
import ru.utils.Density;

public class EventsAdapter<EntityType> extends ListAdapter<EntityType>
{
    private final LayoutInflater inflater;

    private final DateFormatter dateFormatter;

    public EventsAdapter(final LayoutInflater inflater, final ArrayList<EntityType> items)
    {
        super(inflater, inflater.getContext().getResources().getDimensionPixelSize(R.dimen.list_image_size), items);
        this.inflater = inflater;
        this.dateFormatter = new DateFormatter(inflater.getContext());
    }

    @Override
    public int getItemViewType(int position)
    {
        final Event item = (Event) getItem(position); // castTo(getItem(position), Event.class);
        int i = (0 == item.date) ? 0 : 1;
        if (0 == item.promoted)
            return (item.highlight ? 2 : 0) + i;

        return (item.highlight ? 4 : 6) + i;
    }

    @Override
    public int getViewTypeCount()
    {
        return 8;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        final View result;
        final TextView tvDate;
        Event item = (Event) getItem(position); //castTo(getItem(position), Event.class);
        if (null == convertView)
        {
            result = inflater.inflate(R.layout.events_list_item, parent, false);
            TextView tvText = (TextView) result.findViewById(R.id.item_text);

            if (item.highlight)
                result.setBackgroundResource(R.drawable.li_frame_highlight);
            else
                result.setBackgroundResource(R.drawable.li_frame);

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
                params.topMargin = Density.getIntValue(context  , 6);

                ((ViewGroup) result).addView(pin, params);
            }

            holder = new ViewHolder(
                    tvText,
                    tvDate = (TextView) result.findViewById(R.id.item_date),
                    (TextView) result.findViewById(R.id.item_address),
                    (ImageView) result.findViewById(R.id.item_image));
            if (0 == item.date)
                tvDate.setVisibility(View.GONE);
            result.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            tvDate = holder.date;
            result = convertView;
        }

        holder.title.setText(item.title);
        holder.address.setText(item.address);
        CharSequence dateString = item.dateString;
        if (null == dateString)
        {
            dateString = dateFormatter.formatDateTime(item.date);
            item.dateString = dateString;
        }

        if (null != dateString && 0 != dateString.length())
            tvDate.setText(dateString);

        displayImage(holder.icon, item.avatar);
        return result;
    }

    /*
    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        Event item = getItem(position);
        return (0 == item.date) ? 0 : 1;
    }*/

    private final static class ViewHolder
    {
        public final TextView title;
        public final TextView date;
        public final TextView address;
        public final ImageView icon;

        public ViewHolder(TextView title, TextView date, TextView address, ImageView icon)
        {
            this.title = title;
            this.date = date;
            this.address = address;
            this.icon = icon;
        }
    }
}
