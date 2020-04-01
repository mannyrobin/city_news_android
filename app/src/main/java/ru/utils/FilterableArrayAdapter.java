package ru.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Sort, add, delete methods is not supported with filter
public abstract class FilterableArrayAdapter<T>  extends BaseAdapter implements Filterable//extends ArrayAdapter<T>
{
    protected final Object mLock = new Object();
    protected final LayoutInflater    mInflater;
    protected final Context           mContext;
    protected ArrayFilter mFilter;
    //protected int mDropDownResource;
    //protected int mResource;
    protected int mFieldId;

    protected ArrayList<T> mObjects;
    protected ArrayList<T> mOriginalValues;
    protected boolean filterSet;
    private boolean mNotifyOnChange = true;

    public FilterableArrayAdapter(Context context, /*int resource,*/ int textViewResourcId, ArrayList<T> objects)
    {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mObjects = (null != objects) ? objects : new ArrayList<T>();
        this.mContext = context;
        this.mFieldId = textViewResourcId;
        //mResource = mDropDownResource = resource;
    }

    public void setObjects(ArrayList<T> objects)
    {
        this.mObjects = (null != objects) ? objects : new ArrayList<T>();
        notifyDataSetChanged();
    }


    /**
     * Inserts the specified object at the specified index in the array.
     */
//    public void insert(T object, int index) {
//        synchronized (mLock) {
//            if (mOriginalValues != null) {
//                mOriginalValues.add(index, object);
//            } else {
//                mObjects.add(index, object);
//            }
//        }
//        if (mNotifyOnChange) notifyDataSetChanged();
//    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    protected void add(T object)
    {
        synchronized (mLock)
        {
            if (mOriginalValues != null)
            {
                mOriginalValues.add(object);
            }
            if (null != mObjects)
            {
                //else {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange)
        {
            notifyDataSetChanged();
        }
    }
    
    /**
     * Removes the specified object from the array.
     *
     */
    //public void remove(T object) {
    //    remove(object, mNotifyOnChange);
    //}

    protected Object createFilterSpan()
    {
        return new BackgroundColorSpan(0xFFf6f3c5);
    }

    /**
     * Remove all elements from the list.
     */
    /*
    public void clear() 
    {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } //else {
            if (null != mObjects) {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }*/
    protected void remove(T object, boolean notifyOnChange)
    {

        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            }
            if (null != mObjects)
            //else
            {
                mObjects.remove(object);
            }
        }
        if (notifyOnChange && mNotifyOnChange)
        {
            notifyDataSetChanged();
        }
    }

    /*
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }*/

    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * {@inheritDoc}
     */
    public int getCount() {
        return (null != mObjects) ?  mObjects.size() : 0;
    }


    public T getItem(int position)
    {
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     *
     * @return The position of the specified item.
     */
    //public int getPosition(T item) {
    //    return mObjects.indexOf(item);
    //}

    /**
     * {@inheritDoc}
     */
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }


    /*
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
            int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        T item = getItem(position);
        if (item instanceof CharSequence) {
            text.setText((CharSequence)item);
        } else {
            text.setText(item.toString());
        }

        return view;
    }
    

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }
    */

    /**
     * Sorts the content of this adapter using the specified comparator.
     */
    /*
    public void sort(Comparator<? super T> comparator)
    {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    protected void doSort(Comparator<? super T> comparator)
    {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
    }*/
    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    /**
     * {@inheritDoc}
     */
    public Filter getFilter()
    {
        if (mFilter == null)
        {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    protected void onClearFilter()
    {
        filterSet = false;
    }

    /*protected*/
    public ArrayList<T> resetFilter()
    {
        ArrayList<T> list = null;

        if (null != mOriginalValues)
        {
            synchronized (mLock)
            {
                //list = new ArrayList<T>(mOriginalValues);
                list = mOriginalValues;
            }
        }
        return list;
    }

    @NonNull
    public ArrayList<T> applyFilter(CharSequence prefix)
    {
        final List<T> values;
        synchronized (mLock)
        {
            values = new ArrayList<T>(mOriginalValues);
        }

        final ArrayList<T> newValues = new ArrayList<T>(values.size());
        doFilter(prefix.toString(), values, newValues);
        return newValues;
    }

    protected void doFilter(final String prefix, final List<T> values, final ArrayList<T> newValues)
    {
        final Matcher m = createMatcher(prefix);
        for (T t : values)
        {
            final String value = t.toString();
            m.reset(value);
            if (m.find())
            {
                newValues.add(t);
            }
        }
        filterSet = true;
    }

    protected Matcher createMatcher(final String filter)
    {
        return Pattern.compile
                (filter,
                 Pattern.CASE_INSENSITIVE | Pattern.LITERAL
                ).matcher("");
    }

    protected class ArrayFilter extends Filter
     {
        @Override
        protected FilterResults performFiltering(CharSequence prefix)
        {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null)
            {
                synchronized (mLock)
                {
                    mOriginalValues = mObjects; //new ArrayList<T>(mObjects);
                }
            }

            if (prefix == null || 0 == prefix.length())
            {
                ArrayList<T> list = resetFilter();
                results.values = list;
                results.count = list.size();
                onClearFilter();
            }
            else
            {
                final ArrayList<T> newValues = applyFilter(prefix);
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            //noinspection unchecked
            mObjects = (ArrayList<T>) results.values;
            if (results.count > 0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }
    }

    public boolean isFilterSet()
    {
        return filterSet;
    }


}