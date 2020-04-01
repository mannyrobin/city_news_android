package ru.mycity.maps;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;

import ru.mycity.R;
import ru.mycity.data.Organization;

class GoogleMapsControllerClustering extends GoogleMapsControllerBase implements GoogleMap.OnInfoWindowClickListener, ClusterManager.OnClusterClickListener<MapClusterItem>
{
    private ClusterManager<MapClusterItem> mClusterManager;
    private ClusterInfoWindowAdapter clusterInfoWindowAdapter;
    _CustomInfoWindowAdapter infoWindowAdapter;

    public GoogleMapsControllerClustering(IMapClient client)
    {
        super(client);
    }

    @Override
    public Object addMarker(Organization organization, boolean visible)
    {
        MapClusterItem item = new MapClusterItem(organization);
        mClusterManager.addItem(item);
        return item;
    }


    public Organization find(Object item)
    {
        return null;
        //return ((MapClusterItem) item).organization;
    }

    private ArrayList<MapClusterItem> allItems;
    public boolean addMarkers(ArrayList<Organization> organizations, boolean isWorking)
    {
        if (null == mClusterManager)
            return false;

        final int len;
        if (null != organizations && 0 != (len = organizations.size()))
        {
            if (null == allItems)
               allItems = new ArrayList<>(len);

            final ArrayList<MapClusterItem> markers = new ArrayList<>(len);
            for (Organization o : organizations)
            {
                MapClusterItem item = new MapClusterItem(o);
                allItems.add(item);

                if (isWorking && !o.isWorkingNow())
                    continue;

                markers.add(item);
            }
            mClusterManager.addItems(markers);
            //mClusterManager.cluster();
            //MarkerManager.Collection collection = mClusterManager.getClusterMarkerCollection();
            //mClusterManager.getMarkerManager().getCollection()
        }
        return true;
    }

    @Override
    public boolean switchMarkers(boolean isWorking)
    {
         if (null == allItems)
            return false;
         final int len = allItems.size();

         if (0 == len)
             return true;

         if (isWorking)
         {
             ArrayList<MapClusterItem> markers = new ArrayList<>(len);
             mClusterManager.clearItems();
             for (MapClusterItem m : allItems)
             {
                 if (m.organization.isWorkingNow())
                     markers.add(m);
             }
             mClusterManager.addItems(markers);
         }
        else
        {
             mClusterManager.clearItems();
             mClusterManager.addItems(allItems);
        }
        mClusterManager.cluster();
        return true;
    }


    public void clear()
    {
        if (null != mClusterManager)
            mClusterManager.clearItems();
    }

    @Override
    public void setMarkerVisible(Object marker, boolean visible)
    {
        //http://www.we-edit.de/stackoverflow/question/how-update-markers-on-map-when-using-clustermanager-32727453.html
    }

    @Override
    public boolean isChangeMarkerVisibilitySupported()
    {
        return false;
    }

    protected void initGoogleMap(GoogleMap googleMap)
    {
        super.initGoogleMap(googleMap);

        mClusterManager = new ClusterManager<MapClusterItem>(mMapView.getContext(), googleMap);
        mClusterManager.setRenderer(new ItemRenderer(mMapView.getContext(), googleMap, mClusterManager, getMarkerDrawable()));
        //mGoogleMap.setOnCameraChangeListener(mClusterManager);
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);

        googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        LayoutInflater layoutInflater = LayoutInflater.from(mMapView.getContext());
        infoWindowAdapter =  new _CustomInfoWindowAdapter(layoutInflater, m_client);
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(infoWindowAdapter);
        googleMap.setOnMarkerClickListener(mClusterManager);

        //TODO
        //clusterInfoWindowAdapter = new ClusterInfoWindowAdapter(layoutInflater, m_client);
        //mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(clusterInfoWindowAdapter);

        mClusterManager.setOnClusterClickListener(this);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapClusterItem>() {
            @Override
            public boolean onClusterItemClick(MapClusterItem item) {
                infoWindowAdapter.setClickedClusterItem(item);
                return false;
            }
        });
        googleMap.setOnInfoWindowClickListener(this);
    }


    @Override
    public void onInfoWindowClick(Marker marker)
    {
        MapClusterItem clickedClusterItem = infoWindowAdapter.getClickedClusterItem();
        if (null != clickedClusterItem)
        {
            final Organization o = clickedClusterItem.organization;
            if (null != clickedClusterItem.organization)
            if (m_client.onOrganizationClick(o))
                marker.hideInfoWindow();
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MapClusterItem> cluster)
    {
        if (null != clusterInfoWindowAdapter)
            clusterInfoWindowAdapter.setClickedClusterItem(cluster);

        GoogleMap map = mGoogleMap;
        float currentZoomLevel = map.getCameraPosition().zoom;
        float newZoomLevel = currentZoomLevel + 1;
        final float minLevel, maxLevel;
        if (newZoomLevel < (minLevel = map.getMinZoomLevel()))
        {
            newZoomLevel = minLevel;
        }
        else if (newZoomLevel > (maxLevel = map.getMaxZoomLevel()))
        {
            newZoomLevel = maxLevel;
        }
        // TODO: Why does this not zoom when we use animateCamera()?
        if (newZoomLevel != currentZoomLevel)
        {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), newZoomLevel));
            return true;
        }
        //http://stackoverflow.com/questions/25395357/android-how-to-uncluster-on-single-tap-on-a-cluster-marker-maps-v2

        //TODO if zoomLevel is minimum and all items has the same coordinates show list
        //Collection<MapClusterItem> items = cluster.getItems();
        //return false;
        return false;
    }


    private final static class ItemRenderer extends DefaultClusterRenderer<MapClusterItem>
    {
        private final BitmapDescriptor iconBitmapDescriptor;
        public ItemRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager, BitmapDescriptor iconBitmapDescriptor)
        {
            super(context, map, clusterManager);
            this.iconBitmapDescriptor = iconBitmapDescriptor;
        }

        @Override
        protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions)
        {
            markerOptions
                    .icon(iconBitmapDescriptor)
                    .title(item.organization.title)
                //    .visible(visible)
                ;
        }
    }

    private final static class _CustomInfoWindowAdapter extends CustomInfoWindowAdapter
    {
        private MapClusterItem mClickedClusterItem;

        protected _CustomInfoWindowAdapter(LayoutInflater inflater, IMapClient m_client)
        {
            super(inflater, m_client);
        }

        public void setClickedClusterItem(MapClusterItem mClickedClusterItem)
        {
            this.mClickedClusterItem = mClickedClusterItem;
        }

        public MapClusterItem getClickedClusterItem()
        {
            return mClickedClusterItem;
        }


        protected Spannable getDescription(final Marker marker)
        {
            if (null != mClickedClusterItem)
            {
                final Organization o = mClickedClusterItem.organization;
                if (null != mClickedClusterItem.organization)
                    return m_client.getDescription(marker.getTitle(), o, true);
            }
            return new SpannableString(marker.getTitle());
        }
    }

    private final static class ClusterInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        private Cluster<MapClusterItem> cluster;
        private final IMapClient  m_client;

        private ClusterInfoWindowAdapter(LayoutInflater inflater, IMapClient  m_client)
        {
            mWindow = inflater.inflate(R.layout.bubble_cluster, null);
            this.m_client = m_client;
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            render(marker, mWindow);
            return mWindow;
        }

        private void render(Marker marker, View view)
        {
            if (cluster == null)
                return;
            StringBuilder sb  = new StringBuilder(32);

            for (MapClusterItem item : cluster.getItems())
            {
                sb.append(item.organization.title).append('\n');
            }

            final TextView header = (TextView) view.findViewById(R.id.title);
            header.setText(sb);
        }

        private final View mWindow;


        public void setClickedClusterItem(Cluster<MapClusterItem>  cluster)
        {
            this.cluster = cluster;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            return null;
        }
    }

}

//https://github.com/hrickards/openaccessbutton/blob/master/app/src/main/java/org/openaccessbutton/openaccessbutton/map/MapFragment.java