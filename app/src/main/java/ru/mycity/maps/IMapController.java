package ru.mycity.maps;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

import ru.mycity.data.Organization;

public interface IMapController
{
    void init(Bundle savedInstanceState, Context applicationContext, ViewParent parent, ViewGroup group);

    Object addMarker(Organization organization, boolean visible);

    void setPosition(int latitude, int longitude, float zoom);

    void enableClientLocation(boolean locationEnable, boolean compassEnable, boolean locationButtonEnable) throws SecurityException;

    void setMapRect(int minLat, int minLon, int maxLat, int maxLon, int padding);

    void setMarkerVisible(Object marker, boolean visible);
    boolean isChangeMarkerVisibilitySupported();

    Organization find(Object item);
    boolean addMarkers(ArrayList<Organization> organizations, boolean isWorking);
    boolean switchMarkers(boolean isWorking);

    void clear();

    View getView();

    void onResume();

    void onPause();

    void onDestroy();

    void onLowMemory();
}