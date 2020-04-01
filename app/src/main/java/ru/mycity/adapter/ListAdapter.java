package ru.mycity.adapter;

import android.view.LayoutInflater;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import ru.mycity._Application;
import ru.mycity.utils.PictureUtils;
import ru.utils.FilterableArrayAdapter;


abstract class ListAdapter<T> extends FilterableArrayAdapter<T>
{
    protected final LayoutInflater inflater;

    protected ImageLoader imageLoader;
    protected String prefix;
    protected DisplayImageOptions displayOptions;
    private int pictureSizeInPixels;

    public ListAdapter(LayoutInflater inflater, int pictureSizeInPixels, ArrayList<T> objects)
    {
        super(inflater.getContext().getApplicationContext(), 0, objects);
        this.inflater = inflater;
        this.pictureSizeInPixels = pictureSizeInPixels;
    }

    protected void displayImage(ImageView imageView, String partialUrl)
    {
        if (null == imageLoader)
            initImageLoader();
        String url = PictureUtils.prepareUrl(partialUrl, prefix);
        imageLoader.displayImage(url, imageView, displayOptions);
    }


    protected void initImageLoader()
    {
        _Application application = (_Application) mContext.getApplicationContext();
        imageLoader = application.getImageLoader();
        prefix = PictureUtils.generatePrefix(PictureUtils.getPictureValue(mContext, pictureSizeInPixels));

        displayOptions = createDisplayImageOptions(application);
    }

    protected DisplayImageOptions createDisplayImageOptions(_Application application)
    {
        return application.generateDefaultImageOptionsBuilder().build();
    }

}
