package ru.utils;

public final class NameValueItem
{
    public final String name;
    public final String value;
    
    public NameValueItem(String name, String value)
    {
        this.name   = name;
        this.value  = value;
    }
    @Override
    public String toString()
    {

        return name;
    }
}
