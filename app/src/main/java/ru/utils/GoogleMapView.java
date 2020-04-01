package ru.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import com.google.android.gms.maps.MapView;


public class GoogleMapView extends MapView
{

    public GoogleMapView(Context context)
    {
        super(context);
    }

    public GoogleMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    //public GoogleMapView(Context context, AttributeSet attrs, int defStyleAttr) {
    //    this(context, attrs, defStyleAttr, 0);
    //}

    //@TargetApi(21)
    //public GoogleMapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    //{
    //    sup
    // er(context, attrs, defStyleAttr, defStyleRes);
    //}

    /*
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle MapView's touch events.
        super.onTouchEvent(ev);
        return true;
    }
    */


    private ViewParent mViewParent;

    public void setViewParent(@Nullable final ViewParent viewParent)
    { //any ViewGroup
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


