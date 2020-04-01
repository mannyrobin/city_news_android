package ru.mycity.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import ru.mycity.R;
import uk.co.senab.photoview.PhotoView;


public class ImagePreview extends BaseFragment
{
    private String        imageUrl;
    private CharSequence  title;

    public final static String  FRAGMENT_TAG = "ImagePreview";

    public ImagePreview()
    {
        setHasOptionsMenu(true);
    }

    public void setData(String imageUrl, CharSequence title)
    {
        this.imageUrl  = imageUrl;
        this.title     = title;
    }


   @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.image_preview, container,false);
        PhotoView imageView = (PhotoView) rootView.findViewById(R.id.preview);
        
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        //ProgressBarUtils.colorize(progressBar);
        imageView.setImageUrl(this.imageUrl, progressBar);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater)
    {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    protected CharSequence getTitle()
    {
        return title;
    }
    
   
}