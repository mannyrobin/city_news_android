package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;


public class EventParser extends EventsBaseParser
{
    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        return readEvent(reader);
    }
}
