package ru.mycity;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

class ImageLoaderFileNameGenerator implements FileNameGenerator
{
    private final static String drawable = "drawable://";

    @Override
    public String generate(String imageUri)
    {
        if (imageUri.startsWith(drawable))
        {
            //Log.d("#9#", "#9# WWWWWWWW " + imageUri.substring(drawable.length()));
            return imageUri.substring(drawable.length());
        }
        //Log.d("#9#", "#9# YYYYYY " + String.valueOf(imageUri.hashCode()));
        return String.valueOf(imageUri.hashCode());
    }
}
