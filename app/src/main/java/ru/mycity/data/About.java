package ru.mycity.data;

import java.util.ArrayList;

public class About
{
    public long id;

    public ArrayList<Owner> owners;
    public String phone;
    public String about_project;

    public long updated_at;

    public final static class Owner
    {
        public String name;
        public String pic_link;
        public String desc;
    }

}


