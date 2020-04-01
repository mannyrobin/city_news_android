package ru.utils;

import android.content.Context;
import android.view.View;
/*
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import android.view.ViewParent;


import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;

public class OSMMapView extends MapView
{
    public OSMMapView(Context context)
    {
        super(context);
    }

    public OSMMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OSMMapView(final Context context, final ResourceProxy resourceProxy) {
        super(context, resourceProxy);
    }



    //public _MapView(Context context, AttributeSet attrs, int defStyleAttr) {
    //    this(context, attrs, defStyleAttr, 0);
    //}

    //@TargetApi(21)
    //public _MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    //{
    //    super(context, attrs, defStyleAttr, defStyleRes);
    //}

    //    @Override
    //    public boolean onTouchEvent(MotionEvent ev)
    //    {
    //        int action = ev.getAction();
    //        switch (action) {
    //            case MotionEvent.ACTION_DOWN:
    //                // Disallow ScrollView to intercept touch events.
    //                this.getParent().requestDisallowInterceptTouchEvent(true);
    //                break;
    //
    //            case MotionEvent.ACTION_UP:
    //                // Allow ScrollView to intercept touch events.
    //                this.getParent().requestDisallowInterceptTouchEvent(false);
    //                break;
    //        }
    //
    //        // Handle MapView's touch events.
    //        super.onTouchEvent(ev);
    //        return true;
    //    }


    private ViewParent mViewParent;

    public void setViewParent(@Nullable final ViewParent viewParent) { //any ViewGroup
        mViewParent = viewParent;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null == mViewParent) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    mViewParent.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (null == mViewParent) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    mViewParent.requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(event);
    }
}
*/

// just stub
public class OSMMapView extends View
{
    public OSMMapView(Context context)
    {
        super(context);
    }
}