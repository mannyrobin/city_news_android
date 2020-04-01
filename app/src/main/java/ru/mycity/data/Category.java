package ru.mycity.data;


import java.util.ArrayList;

public class Category
{
    public long id;
    public String name;
    public long parent_id;
    public int position;
    public String picture;
    public String image;
    public int childCount;
    public String childCountString;
    public boolean deleted;
    public boolean published;
    public Category parent;
    public long updated_at;

    public ArrayList<Category> subCategories;
    public ArrayList<Organization> organizations;

    public int resId;

    @Override
    public String toString()
    {
        return name;
    }
}


