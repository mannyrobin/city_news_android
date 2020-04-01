package ru.mycity.maps;

import android.text.Spannable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ru.mycity.R;
import ru.utils.CustomLinkMovementMethod;

class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    protected CustomLinkMovementMethod linkMovementMethod;

    private final View mWindow;
    protected IMapClient  m_client;

    protected CustomInfoWindowAdapter(LayoutInflater inflater, IMapClient m_client)
    {
        mWindow = inflater.inflate(R.layout.bubble, null);
        this.m_client = m_client;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        return null;
    }

    protected Spannable getDescription(final Marker marker)
    {
        return m_client.getSpannableDescription(marker.getTitle(), marker);
    }

    private void render(final Marker marker, View view)
    {
        final TextView header = (TextView) view.findViewById(R.id.title);
        header.setText(marker.getTitle());

        final TextView snippet = (TextView) view.findViewById(R.id.snippet);
        Spannable description = getDescription(marker);
        if (null != description)
        {
            if (Linkify.addLinks(description, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES))
            {
                if (null == linkMovementMethod)
                {
                    linkMovementMethod = new CustomLinkMovementMethod(m_client);
                }
                snippet.setLinksClickable(true);
                snippet.setMovementMethod(linkMovementMethod);
            }

            snippet.setText(description);
        }
            /*
            ImageButton bubbleClose = (ImageButton) view.findViewById(R.id.bubbleclose);
            bubbleClose.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    marker.hideInfoWindow();
                }
            });
            */
    }
}

//https://github.com/hrickards/openaccessbutton/blob/master/app/src/main/java/org/openaccessbutton/openaccessbutton/map/MapFragment.java