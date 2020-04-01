package ru.utils;

import android.text.style.URLSpan;
import android.view.View;

public class _URLSpan extends URLSpan
{
    private final IOnLinkClick onLinkClick;

    public _URLSpan(String url, IOnLinkClick onLinkClick)
    {
        super(url);
        this.onLinkClick = onLinkClick;
    }

    @Override
    public void onClick(View widget)
    {
        if (null != onLinkClick)
        {
            onLinkClick.onLinkClick(this, widget);
        }
        super.onClick(widget);
    }


    public void handleClick(View widget)
    {
        super.onClick(widget);
    }
}