package ru.mycity.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Category;
import ru.utils.FilterableArrayAdapter;

public class CategoriesAdapter extends FilterableArrayAdapter<Category>
{
    private final static String drawable = "drawable://";
    private final static String http = "http";
    private final static String menu_book = "menu_book_";
    private final LayoutInflater inflater;
    //private Context context;
    //private final int width;
    private DisplayImageOptions displayOptions;
    private ImageLoader imageLoader;
    private String packageName;
    private Resources resources;
    private final static String empty = "";

    public CategoriesAdapter(final LayoutInflater inflater, final ArrayList<Category> items)
    {
        super(inflater.getContext(), 0, items);
        this.inflater = inflater;
    }

    @Override
    public int getItemViewType(int position)
    {
        Category category = getItem(position);
        if (0 == category.parent_id)
            return 0;
        String pic = category.picture;
        if (null != pic && 0 != pic.length())
            return 2;
        pic = category.image;
        if (null != pic && 0 != pic.length())
            return 2;
        return 1;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        CategoryViewHolder holder;
        final View result;

        Category category = getItem(position);
        boolean isRootCategory = category.parent_id == 0;
        String picture = category.picture;
        if (null != picture && 0 != picture.length())
            ;
        else
            picture = category.image;

        if (null == convertView)
        {
            result = inflater.inflate(isRootCategory ?
                    R.layout.category_list_item :
                    (null != picture && 0 != picture.length()) ?
                    R.layout.sub_category_list_pict_item :
                    R.layout.sub_category_list_item,
                    parent, false);
            holder = new CategoryViewHolder(
                    (TextView) result.findViewById(R.id.item_text),
                    (ImageView) (isRootCategory || (null != picture && 0 != picture.length()) ? result.findViewById(R.id.item_image): null) ,
                    isRootCategory ? null : (TextView) result.findViewById(R.id.item_count));
            result.setTag(holder);
        }
        else
        {
            holder = (CategoryViewHolder) convertView.getTag();
            result = convertView;
        }

        String titleText = category.name;
        holder.title.setText(titleText);

        if (isRootCategory)
        {
            if (null == displayOptions)
                initImageLoader();

            if (null != picture && picture.length() > 0)
            {
                if (picture.startsWith(http))
                {
                    imageLoader.displayImage(picture, holder.image, displayOptions);
                }
                else
                {
                    if (0 == category.resId)
                    {
                        category.resId = resources.getIdentifier(
                                Character.isDigit(picture.charAt(0)) ? menu_book + picture: picture, "drawable", packageName);
                        picture =  drawable +  category.resId;
                        category.picture = picture; 

                    }
                    /*  
                    try
                    {
                        if (0 != category.resId)
                            holder.image.setImageResource(category.resId);
                    }
                    catch (Throwable e)
                    {
                    } */
                    if (0 != category.resId)
                      imageLoader.displayImage(picture, holder.image, displayOptions);
                       
                }
            }
            else
            {
                imageLoader.displayImage(null, holder.image, displayOptions);
            }
        }
        else
        {
            //Log.d("#4#", "#4# NON ROOT" + category.name + "  " + category.parent_id);
            String s = category.childCountString;
            if (null == s)
            {
                if (0 != category.childCount)
                    s = Integer.toString(category.childCount);
                else
                    s = empty;
                category.childCountString = s;
            }
            holder.counter.setText(s);
            //if (null != picture && 0 != picture.length())
            if (null != holder.image)
            {
                if (null == displayOptions)
                    initImageLoader();
                imageLoader.displayImage(picture, holder.image, displayOptions);
            }
        }
        return result;
    }

    private void initImageLoader()
    {
        packageName = mContext.getPackageName();
        resources = mContext.getResources();

        _Application application = (_Application) mContext.getApplicationContext();
        imageLoader = application.getImageLoader();
        displayOptions = application.generateDefaultImageOptionsBuilder().build();
    }

    private final static class CategoryViewHolder
    {
        public final TextView title;
        public final ImageView image;
        public final TextView  counter;

        public CategoryViewHolder(TextView title, ImageView image, TextView  counter)
        {
            this.title = title;
            this.image = image;
            this.counter = counter;
        }
    }
}
