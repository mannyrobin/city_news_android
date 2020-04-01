package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;


public class ActionParser extends ActionBaseParser
{
    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        return readAction(reader);
    }
}
