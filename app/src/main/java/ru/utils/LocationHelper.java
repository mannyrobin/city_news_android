package ru.utils;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;

public class LocationHelper
{
    public final static class LocationEnabledStatus
    {
        public boolean gpsEnabled;
        public boolean netEnabled;
    }

    public static LocationEnabledStatus getLocationStatus(final Context context)
    {
        final LocationEnabledStatus status = new LocationEnabledStatus();
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean net = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (false == gps || false == net)
        {
            try
            {
                final String providers = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (null != providers)
                {
                    if (false == gps)
                    {
                        gps = providers.contains(LocationManager.GPS_PROVIDER);
                    }
                    if (false == net)
                    {
                        net = providers.contains(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
            catch (Throwable e)
            {
            }
        }
        status.gpsEnabled = gps;
        status.netEnabled = net;
        return status;
    }

}
