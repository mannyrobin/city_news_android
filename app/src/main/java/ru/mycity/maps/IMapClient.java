package ru.mycity.maps;

import android.text.Spannable;

import ru.mycity.data.Organization;
import ru.utils.IOnLinkClick;

public interface IMapClient extends IOnLinkClick
{
    int getMapMarkerResourceId();
    boolean isMapPermissionsEnabled();
    void requestMapPermissions();
    void enableMyLocation();
    Spannable getSpannableDescription(String text, Object context);
    Spannable getDescription(String text, Organization organization, boolean bFull);
    boolean onOrganizationClick(Organization organization);
    void onMapReady();
}
