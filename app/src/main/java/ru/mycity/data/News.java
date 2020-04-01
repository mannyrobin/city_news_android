package ru.mycity.data;

public class News
{
    public long id;
    public String title;
    public String pic;
    public String origin_title;
    public String origin_link;
    public String body;
    public boolean highlight;
    public boolean featured;
    public int promoted;
    //public String date;
    public long date;
    public  boolean deleted;
    public boolean published;
    public long updated_at;

    public String video;
    public boolean is_video_hidden;


    //TODO remove this field after NewsAdapter migrate
    public CharSequence dateString;

    @Override
    public String toString()
    {
        return title;
    }

}
