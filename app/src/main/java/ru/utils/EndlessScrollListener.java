package ru.utils;


import android.widget.AbsListView;
import android.widget.BaseAdapter;


public class  EndlessScrollListener implements AbsListView.OnScrollListener
{
    private final BaseAdapter adapter;
    private boolean isLoading;

    private boolean isEnabled;
    protected final EndlessListener listener;
    //private int lastFirstVisibleItem = -1;

    public EndlessScrollListener(BaseAdapter adapter, EndlessListener listener)
    {
        this.adapter = adapter;
        isEnabled = true;
        this.listener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount)
    {

        if (!isEnabled)
            return;

        if (adapter == null)
            return;

        if (adapter.getCount() == 0)
            return;

        /*
        if (lastFirstVisibleItem == firstVisibleItem)
        {
            //Log.d("&6&", "&6& firstVisibleItem = " + firstVisibleItem +  " visibleItemCount=" + visibleItemCount + " totalItemCount =" + totalItemCount + " RETURN ");
            return;
        }

        lastFirstVisibleItem = firstVisibleItem;
        */

         //if (0 == firstVisibleItem && totalItemCount == visibleItemCount)
         //{
         //    return;
        // }

        //if (totalItemCount == visibleItemCount)
        //{
        //    Log.d("&6&", "&6& firstVisibleItem = " + firstVisibleItem +  " visibleItemCount=" + visibleItemCount + " totalItemCount =" + totalItemCount + " return ");
        //    return;
        //}

        final int l = visibleItemCount + firstVisibleItem;


        if (l >= totalItemCount && !isLoading)
        {
            // It is time to add new data. We call the listener
            isLoading = true;
            //Log.d("&6&", "&6& firstVisibleItem = " + firstVisibleItem +  " visibleItemCount=" + visibleItemCount + " totalItemCount =" + totalItemCount + " LOAD ");
            //this.addFooterView(footer);
            listener.loadData();
        }
        else
        {
           //Log.d("&6&", "&6& firstVisibleItem = " + firstVisibleItem +  " visibleItemCount=" + visibleItemCount + " totalItemCount =" + totalItemCount + " not load ");
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        //Log.d("&6&", "&6& scrollState = " + scrollState);
    }


    public void setLoading(boolean loading)
    {
        isLoading = loading;
    }

    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    public interface EndlessListener
    {
        void loadData() ;
    }


    public boolean isEnabled()
    {
        return isEnabled;
    }

    public boolean isLoading()
    {
        return isLoading;
    }


}
