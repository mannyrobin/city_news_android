package ru.mycity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Button;

public class ButtonsAdapter extends BaseAdapter
{
    private final List<Button> items;
    private final LayoutInflater inflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayOptions;

    private final String empty = "";


    public ButtonsAdapter(final LayoutInflater inflater, final List<Button> items)
    {
        this.inflater = inflater;
        //this.context = context;
        //width = ContactsListForm.getThumbWidth(context);
        this.items   = items;


        //TODO 1
        viewLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
    }


    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int index)
    {
        return items.get(index);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private final static class ViewHolder
    {
        public final TextView title;
        public final ImageView icon;

        public ViewHolder(TextView title, ImageView icon)
        {
            this.title = title;
            this.icon = icon;
        }
    }

    private String getPictureUrl(Button button)
    {
        String pictureUrl = button.icon;
        if (null != pictureUrl && 0 != pictureUrl.length() && pictureUrl.startsWith("http"))
        {
            return pictureUrl;
        }
        pictureUrl = button.image;
        if (null != pictureUrl && 0 != pictureUrl.length() && pictureUrl.startsWith("http"))
        {
            return pictureUrl;
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        final View result;

        if (null == convertView)
        {
            //result = inflater.inflate(R.layout.button_item,null);
            result = inflater.inflate(R.layout.button_item, parent, false);
            //TODO 2
            result.setLayoutParams(viewLayoutParams);

            holder = new ViewHolder((TextView) result.findViewById(R.id.item_text),
                                    (ImageView) result.findViewById(R.id.item_image));

            result.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //TODO 3
        if (result.getLayoutParams().height != mItemHeight)
        {
            result.setLayoutParams(viewLayoutParams);
        }



        Button button = items.get(position);
        String titleText = button.title;
        //if (position == 0)
        //    titleText = "Экстренные службы";
        if (null != titleText && 0 != titleText.length())
        {
            holder.title.setText(titleText.toUpperCase()); // + '\n');
        }
        else holder.title.setText(empty);

        String pictureUrl = button.pictureUrl;
        if (null == pictureUrl)
        {
            pictureUrl = getPictureUrl(button);
            button.pictureUrl = pictureUrl;
        }

        if (null == imageLoader)
            initImageLoader();

        imageLoader.displayImage(pictureUrl, holder.icon, displayOptions);
        return result;
    }


    private void initImageLoader()
    {
        _Application application = (_Application) inflater.getContext().getApplicationContext();
        imageLoader = application.getImageLoader();
        displayOptions = application.generateDefaultImageOptionsBuilder().build();
    }

    //TODO  4

    private GridView.LayoutParams viewLayoutParams;
    private int mNumColumns = 0;
    private int mItemHeight = 0;

    // set numcols
    public void setNumColumns(int numColumns)
    {
        mNumColumns = numColumns;
    }

    public int getNumColumns()
    {
        return mNumColumns;
    }

    public void setItemHeight(int height)
    {
        if (height == mItemHeight)
        {
            return;
        }
        mItemHeight = height;
        viewLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }
}


