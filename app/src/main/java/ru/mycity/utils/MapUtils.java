package ru.mycity.utils;

import java.util.List;

import ru.mycity.data.Organization;

public class MapUtils
{
    public final static class MapRect
    {
        public int minLat ;
        public int maxLat ;
        public int minLon ;
        public int maxLon ;
        public int count;

    }
    public static MapRect calculateMapRect(List<Organization> organizations)
    {
        if (null == organizations)
            return null;
        if (organizations.isEmpty())
            return null;

        int minLat = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int minLon = Integer.MAX_VALUE;
        int maxLon = Integer.MIN_VALUE;
        int count = 0;
        for (Organization o : organizations)
        {
            if (o.hasCoordinates)
            {
                int lat = o.latitude;
                int lon = o.longitude;

                maxLat = Math.max(lat, maxLat);
                minLat = Math.min(lat, minLat);
                maxLon = Math.max(lon, maxLon);
                minLon = Math.min(lon, minLon);
                count++;
            }
        }
        if (0 == count)
            return null;
        MapRect m = new MapRect();
        m.minLat = minLat;
        m.maxLat = maxLat;
        m.minLon = minLon;
        m.maxLon = maxLon;
        m.count  = count;
        return m;
    }
}
