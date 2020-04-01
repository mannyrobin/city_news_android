package ru.mycity.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;

import ru.mycity.IPermissionCodes;
import ru.mycity.R;
import ru.mycity.data.Category;
import ru.mycity.data.Organization;
import ru.mycity.maps.IMapClient;
import ru.mycity.maps.IMapController;
import ru.utils.IOnLinkClick;
import ru.utils.PermissionsUtils;

public abstract class MapFragment extends BaseFragment implements IOnLinkClick, IMapClient
{
    protected Category category;
    protected boolean myLocationEnabled;
    protected IMapController m_mapController;

    public MapFragment()
    {
    }

    public int getMapMarkerResourceId()
    {
        return R.drawable.map_marker;
    }

    @Override
    public void onLinkClick(URLSpan link, View widget)
    {
        link.onClick(widget);
    }

    abstract protected boolean requestPermissionIfNeededOnCreateMap();

    abstract protected String getName();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            switch (requestCode)
            {
                case IPermissionCodes.PERMISSION_CHECK_LOCATION_RC:
                {
                    if (PermissionsUtils.isAllPermissionsGranted(permissions, grantResults))
                    {
                        enableMyLocation();
                    }
                }
                break;
            }
        }
    }

    public void requestMapPermissions()
    {
        //ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
        //        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CHECK_LOCATION_RC);

        requestPermissions(getName(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, IPermissionCodes.PERMISSION_CHECK_LOCATION_RC);
    }

    public boolean isMapPermissionsEnabled()
    {
        return PermissionsUtils.checkPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void enableMyLocation() throws SecurityException
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            myLocationEnabled = true;
            m_mapController.enableClientLocation(true, true, true);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (null != m_mapController)
            m_mapController.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (null != m_mapController)
            m_mapController.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (null != m_mapController)
            m_mapController.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        if (null != m_mapController)
            m_mapController.onLowMemory();
    }

    public Spannable getDescription(String spannableText, Organization organization, boolean bFull)
    {
        if (null == organization)
        {
            return new SpannableString(spannableText);
        }

        String address = organization.address;
        if (false == bFull)
        {
            if (null != address && 0 != address.length())
            {
                return new SpannableString(address);
            }
        }
        String info = organization.info;
        if (false == bFull)
        {
            if (null != info && 0 != info.length())
            {
                return new SpannableString(info);
            }
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        //StringBuilder sb = new StringBuilder(128);
        if (null != address && 0 != address.length())
        {
            sb.append(address);
            sb.setSpan(new StyleSpan(Typeface.ITALIC), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append('\n');
            //sb.append('\n');
        }
        if (null != info && 0 != info.length())
        {
            sb.append(info);
        }
        return sb;
    }
}
