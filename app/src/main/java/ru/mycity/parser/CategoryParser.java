package ru.mycity.parser;


import com.google.gson.stream.JsonReader;

import java.io.IOException;

public class CategoryParser extends CategoriesBaseParser
{

    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        return readCategory(reader);
    }
}
