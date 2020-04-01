package ru.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

public final class NetworkUtils
{
    public static boolean isNetworkConnected(final Context context)
    {
        if (null == context)
            return false;

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Network[] networks = connectivityManager.getAllNetworks();
            if (null != networks)
            {
                for (Network network : networks)
                {
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                    if ( (null != networkInfo) && (NetworkInfo.State.CONNECTED == networkInfo.getState()))
                        return true;
                }
            }
        }
        else
        {
            /*
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (NetworkInfo anInfo : info)
                {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
            */
            final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;

    }
    
    @SuppressWarnings("deprecation")
    public static boolean isAirplaneModeOn(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(), 
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;          
        } else {
            return Settings.Global.getInt(context.getContentResolver(), 
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }   
     }
}