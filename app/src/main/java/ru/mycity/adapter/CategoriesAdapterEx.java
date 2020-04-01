package ru.mycity.adapter;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.Filter;

import java.util.ArrayList;

import ru.mycity._Application;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.database.DbCategoriesHelper;
import ru.mycity.database.DbOrganizationsHelper;

public class CategoriesAdapterEx extends CategoriesAdapter
{
    final OrganizationsAdapter organizationsAdapter;
    private final _Application application;
    private final Category parent;
    private ArrayList<Organization> organizations;

    public CategoriesAdapterEx(LayoutInflater inflater, ArrayList<Category> items, OrganizationsAdapter organizationsAdapter, Category parent)
    {
        super(inflater, items);
        mOriginalValues = items;
        this.organizationsAdapter = organizationsAdapter;
        this.parent = parent;
        application = (_Application) inflater.getContext().getApplicationContext();
    }


    @Override
    public ArrayList<Category> resetFilter()
    {
        organizations = organizationsAdapter.resetFilter();
        return super.resetFilter();
    }

    @NonNull
    @Override
    public ArrayList<Category> applyFilter(CharSequence prefix)
    {
        if (prefix.length() < 3)
        {
            return resetFilter();
        }

        ArrayList<Category> list;
        if (null == parent) //root
        {
            list = DbCategoriesHelper.findCategories(application.getDbHelper().getReadableDatabase(), prefix.toString());
        }
        else
        {
            list = super.applyFilter(prefix);
        }

        organizations = DbOrganizationsHelper.findOrganizations(application.getDbHelper().getReadableDatabase(), prefix.toString(),
                //list
                //null,
                mOriginalValues, 0,
                DbOrganizationsHelper.PAGE_SIZE, 0);

        if (null == list)
        {
            list = new ArrayList<>(1);
        }
        organizationsAdapter.setFilterString(prefix);
        return list;
    }

    /*
    @NonNull
    @Override
    protected ArrayList<Category> applyFilter(CharSequence prefix)
    {
        if (prefix.length() < 4)
            return resetFilter();
        return super.applyFilter(prefix);
    }
    */


    @Override
    public Filter getFilter()
    {
        if (mFilter == null)
        {
            mFilter = new _ArrayFilter();
        }
        return mFilter;
    }



    protected class _ArrayFilter extends ArrayFilter
    {
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            super.publishResults(constraint, results);
            organizationsAdapter.setObjects(organizations);
        }
    }
}
