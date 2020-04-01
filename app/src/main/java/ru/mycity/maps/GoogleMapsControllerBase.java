package ru.mycity.maps;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.GoogleMapView;

abstract class GoogleMapsControllerBase implements IMapController, OnMapReadyCallback
{
    protected GoogleMapView mMapView;
              GoogleMap     mGoogleMap;
    protected IMapClient    m_client;

    public GoogleMapsControllerBase(IMapClient client)
    {
        this.m_client = client;
    }

    public void init(Bundle savedInstanceState, Context applicationContext, ViewParent parent, ViewGroup group)
    {
        mMapView = createView(applicationContext, parent, group);
        mMapView.onCreate(savedInstanceState);
        //mMapView.onResume();// needed to get the map to display immediately

        try
        {
            MapsInitializer.initialize(applicationContext);
        }
        catch (Exception e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(applicationContext), e, false);
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
    }

    public void onResume()
    {
        if (null != mMapView)
        {
            mMapView.onResume();
        }
    }

    public void onPause()
    {
        if (null != mMapView)
        {
            mMapView.onPause();
        }
    }

    public void onDestroy()
    {
        if (null != mMapView)
        {
            mMapView.onDestroy();
        }
    }

    public void onLowMemory()
    {
        if (null != mMapView)
        {
            mMapView.onLowMemory();
        }
    }

    @Override
    public View getView()
    {
        return mMapView;
    }

    protected GoogleMapView createView(Context context, ViewParent parent, ViewGroup group)
    {
        GoogleMapView view = new GoogleMapView(context);
        view.setViewParent(parent);
        MapView.LayoutParams mapParams = new MapView.LayoutParams(MapView.LayoutParams.MATCH_PARENT, MapView.LayoutParams.MATCH_PARENT);
        group.addView(view, mapParams);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        initGoogleMap(googleMap);
        m_client.onMapReady();
    }


    protected void initGoogleMap(GoogleMap googleMap)
    {
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap = googleMap;

        if (m_client.isMapPermissionsEnabled())
        {
            m_client.enableMyLocation();
        }
        else
        {
            m_client.requestMapPermissions();
        }

        //mMap.setMyLocationEnabled(isChecked(true);

        googleMap.setBuildingsEnabled(true);

        UiSettings mUiSettings = googleMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        //mUiSettings.setCompassEnabled(true);
        //mUiSettings.setScrollGesturesEnabled(true);

        //mUiSettings.setZoomGesturesEnabled(true);
        //mUiSettings.setTiltGesturesEnabled(true);
        //mUiSettings.setRotateGesturesEnabled(true);
    }

    protected LatLng getLatLng(int latitude, int longitude)
    {
        return new LatLng((double) latitude / 1e6d, (double) longitude / 1e6d);
    }

    public void setMapRect(int minLat, int minLon, int maxLat, int maxLon, int padding)
    {
        if (null != mGoogleMap)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(getLatLng(minLat, minLon), getLatLng(maxLat, maxLon)), 50));
    }

    public void setPosition(int latitude, int longitude, float zoom)
    {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(latitude, longitude), zoom));
    }


    public void enableClientLocation(boolean locationEnable, boolean compassEnable, boolean locationButtonEnable) throws SecurityException
    {
        if (null != mGoogleMap)
        {
            mGoogleMap.setMyLocationEnabled(locationEnable);
            UiSettings uiSettings = mGoogleMap.getUiSettings();
            uiSettings.setCompassEnabled(compassEnable);
            uiSettings.setMyLocationButtonEnabled(locationButtonEnable);
        }
    }

    protected BitmapDescriptor getMarkerDrawable()
    {
        //return getResources().getDrawable(R.drawable.icon_28);
        //return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        return BitmapDescriptorFactory.fromResource(m_client.getMapMarkerResourceId()); // R.drawable.map_marker
    }
}
