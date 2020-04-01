package ru.mycity.adapter;


import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;

public class CategoryOrganizationsAdapter extends OrganizationsAdapter
{
    private final static String drawable = "drawable://";
    private final static String http = "http";
    private final static String menu_book = "menu_book_";
    private final boolean isRootCategory;
    private final List<Category> originalCategories;
    private final int orgViewTypeCount;
    private List<Category> categories;
    private DisplayImageOptions displayOptions;
    private ImageLoader imageLoader;
    private String packageName;
    private Resources resources;

    public CategoryOrganizationsAdapter(LayoutInflater inflater, final ArrayList<Category> categories, boolean isRootCategory, ArrayList<Organization> organizations, IPhoneClick onPhoneClick)
    {
        super(inflater, (null != organizations) ? organizations : new ArrayList<Organization>(), 0, onPhoneClick);
        this.isRootCategory = isRootCategory;
        this.categories = (null != categories) ? categories : new ArrayList<Category>();
        this.originalCategories = this.categories;
        orgViewTypeCount = super.getViewTypeCount();
    }

    public int getCount()
    {
        return categories.size() + mObjects.size();
    }

    @Override
    public ArrayList<Organization> resetFilter()
    {
        categories = originalCategories;
        return super.resetFilter();
    }

    public Object getItemByPosition(int position)
    {
        int size = categories.size();
        return (position < size) ? categories.get(position) : mObjects.get(position - size);
    }

    /*
    public int getCategoriesCount()
    {
        return categories.size();
    }
    public int getOrganizationsCount()
    {
        return mObjects.size();
    }
    */

    @Override
    public int getItemViewType(int position)
    {
        int size = categories.size();
        if (position < size)
        {
            return orgViewTypeCount;
        }
        return super.getItemViewType(position - size);
    }

    @Override
    public int getViewTypeCount()
    {
        return orgViewTypeCount + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int size = categories.size();
        if (position < size)
        {
            return getCategoriesView(position, convertView, parent);
        }
        return super.getView(position - size, convertView, parent);
    }

    @NonNull
    private View getCategoriesView(int position, View convertView, ViewGroup parent)
    {
        CategoryViewHolder holder;
        final View result;

        if (null == convertView)
        {
            result = mInflater.inflate(isRootCategory ? R.layout.category_list_item : R.layout.sub_category_list_item, parent, false);
            holder = new CategoryViewHolder((TextView) result.findViewById(R.id.item_text), result.findViewById(isRootCategory ? R.id.item_image : R.id.item_count));
            result.setTag(holder);
        }
        else
        {
            holder = (CategoryViewHolder) convertView.getTag();
            result = convertView;
        }

        Category category = categories.get(position);
        String titleText = category.name;
        holder.title.setText(titleText);
        if (isRootCategory)
        {
            if (null == displayOptions)
            {
                initImageLoader();
            }
            //"drawable://" + R.drawable.img
            String url = category.picture;
            if (null != url)
            {
                if (url.startsWith(drawable) || url.startsWith(http))
                {
                    ;
                }
                else
                {
                    if (url.length() > 0)
                    {
                        if (Character.isDigit(url.charAt(0)))
                        {
                            url = menu_book + url;
                        }
                    }
                    //.getResources().getIdentifier("nameOfDrawable", "drawable", this.getPackageName());
                    url = drawable + resources.getIdentifier(url, "drawable", packageName);

                    category.picture = url;
                }
                imageLoader.displayImage(url, (ImageView) holder.subView, displayOptions);
            }
        }
        else
        {
            String s = category.childCountString;
            if (null == s)
            {
                if (0 != category.childCount)
                    s = Integer.toString(category.childCount);
                else
                    s = empty;
                category.childCountString = s;
            }
            ((TextView) holder.subView).setText(s);
        }
        return result;
    }

    private final static String empty = "";
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
        public final View subView;

        public CategoryViewHolder(TextView title, View subView)
        {
            this.title = title;
            this.subView = subView;
        }
    }
}