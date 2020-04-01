package ru.mycity.maps;

import android.view.LayoutInflater;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ru.mycity.data.Organization;

class GoogleMapsController extends GoogleMapsControllerBase implements GoogleMap.OnInfoWindowClickListener
{
    public GoogleMapsController(IMapClient client)
    {
        super(client);
    }

    private HashSet<LatLng> positions;
    private HashMap<Marker, Organization> organizationsMap;

    public Object addMarker(Organization organization, boolean visible)
    {
        return _addMarker(organization, visible);
    }

    private Marker _addMarker(Organization organization, boolean visible)
    {
        int latitude = organization.latitude;
        int longitude = organization.longitude;

        LatLng position = getLatLng(latitude, longitude);
        if (null == positions)
            positions = new HashSet<>();

        while (positions.contains(position))
        {
            //int newLat = (int) (latitude + (Math.random() -.5) / 1500);// * (Math.random() * (max - min) + min);
            //int newLng = (int) (longitude + (Math.random() -.5) / 1500);// * (Math.random() * (max - min) + min)

            Random random = new Random();
            int r1 = random.nextInt(220);
            int r2 = random.nextInt(220);
            int newLat = latitude  + ((0 == (r1 %2)) ? - r1 : r1);
            int newLng = longitude + ((0 == (r2 %2)) ? - r2 : r2);
            position = getLatLng(newLat, newLng);
        }
        //Калуга ВТБ 24 Гурьянова 12
        positions.add(position);
        //http://stackoverflow.com/questions/20490654/more-than-one-marker-on-same-place-markerclusterer
        //TODO Clustering
        ////https://developers.google.com/maps/documentation/android-api/utility/marker-clustering#custom

        MarkerOptions mo = new MarkerOptions()
                .position(position)
                .title(organization.title)
                .icon(getMarkerDrawable())
                .visible(visible);
        return mGoogleMap.addMarker(mo);
    }


    public void setMarkerVisible(Object marker, boolean visible)
    {
        ((Marker) marker).setVisible(visible);
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        final Organization o = find(marker);
        if (null != o)
        {
            if (m_client.onOrganizationClick(o))
                marker.hideInfoWindow();
        }
    }

    @Override
    public boolean isChangeMarkerVisibilitySupported()
    {
        return true;
    }

    protected void initGoogleMap(GoogleMap googleMap)
    {
        super.initGoogleMap(googleMap);

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(mMapView.getContext()), m_client));
        googleMap.setOnInfoWindowClickListener(this);
    }

    public void clear()
    {
        if (null != mGoogleMap)
            mGoogleMap.clear();

        if (null != positions)
            positions.clear();

        if (null != organizationsMap)
            organizationsMap.clear();
    }


    public Organization find(Object item)
    {
        if (null != organizationsMap)
        {
            return organizationsMap.get(item);
        }
        return null;
    }

    public boolean addMarkers(ArrayList<Organization> organizations, boolean isWorking)
    {
        if (null == organizationsMap)
            organizationsMap = new HashMap<>(organizations.size());
        for (Organization o : organizations)
        {
            if (o.hasCoordinates)
            {
                boolean isVisible = isWorking ? o.isWorkingNow() : true;
                Marker marker = _addMarker(o, isVisible);
                organizationsMap.put(marker, o);
            }
        }
        return true;
    }

    public boolean switchMarkers(boolean isWorking)
    {
        if (null != organizationsMap)
        {
            if (isWorking)
            {
                Set<Map.Entry<Marker, Organization>> set = organizationsMap.entrySet();
                for (Map.Entry<Marker, Organization> e : set)
                {
                    Organization o = e.getValue();
                    Marker marker = e.getKey();
                    marker.setVisible(o.isWorkingNow());
                }
            }
            else
            {
                Set<Marker> keys = organizationsMap.keySet();
                for (Marker marker : keys)
                {
                    marker.setVisible(true);
                }
            }
        }
        return true;
    }
}

