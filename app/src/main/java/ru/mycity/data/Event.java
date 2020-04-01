package ru.mycity.data;

public class Event
{
    public long id;
    public String title;
    public String address;
    public String photos;
    public String avatar;
    //public String date;
    public long date;
    public String type;
    public String price;
    public String phones;
    public String info;
    public String site;
    public String site_title;
    public long organization_id;
    public boolean highlight;
    public int promoted;
    public long updated_at;
    public boolean deleted;
    public boolean published;
    public CharSequence dateString;
    public String video;
    public boolean is_video_hidden;

    @Override
    public String toString()
    {
        return title;
    }
}

