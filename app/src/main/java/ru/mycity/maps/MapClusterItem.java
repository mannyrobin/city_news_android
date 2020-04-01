package ru.mycity.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import ru.mycity.data.Organization;


class MapClusterItem implements ClusterItem
{
    private final LatLng mPosition;
    final Organization organization;

    public MapClusterItem(Organization organization)
    {
        mPosition = new LatLng((double) organization.latitude / 1e6d, (double) organization.longitude / 1e6d);
        this.organization = organization;
    }

    @Override
    public LatLng getPosition()
    {
        return mPosition;
    }
}
