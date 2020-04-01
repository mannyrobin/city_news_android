package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;

public class OneNewsParser extends NewsBaseParser
{
    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        return readNew(reader);
    }

}
