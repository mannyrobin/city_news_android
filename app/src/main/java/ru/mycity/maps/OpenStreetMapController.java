package ru.mycity.maps;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

import ru.mycity.data.Organization;

class OpenStreetMapController implements IMapController
{
    /*
    @Override
    public boolean isBatchSupported()
    {
        return false;
    }
    */

    @Override
    public Organization find(Object item)
    {
        return null;
    }

    @Override
    public boolean addMarkers(ArrayList<Organization> organizations, boolean isWorking)
    {
        return false;
    }

    @Override
    public boolean switchMarkers(boolean isWorking)
    {
        return false;
    }

    private IMapClient m_client;

    public OpenStreetMapController(IMapClient client)
    {
        this.m_client = client;
    }

    @Override
    public void init(Bundle savedInstanceState, Context applicationContext, ViewParent parent, ViewGroup group)
    {

    }

    @Override
    public Object addMarker(Organization organization, boolean visible)
    {
        return null;
    }

    @Override
    public void setPosition(int latitude, int longitude, float zoom)
    {

    }

    @Override
    public void enableClientLocation(boolean locationEnable, boolean compassEnable, boolean locationButtonEnable) throws SecurityException
    {

    }

    @Override
    public void setMapRect(int minLat, int minLon, int maxLat, int maxLon, int padding)
    {
    }

    @Override
    public void setMarkerVisible(Object marker, boolean visible)
    {

    }

    @Override
    public boolean isChangeMarkerVisibilitySupported()
    {
        return false;
    }


    @Override
    public void clear()
    {

    }

    @Override
    public View getView()
    {
        return null;
    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onDestroy()
    {

    }

    @Override
    public void onLowMemory()
    {

    }
}
