package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.Event;
import ru.utils._DateUtils;


public class EventsParser extends EventsBaseParser
{
    private final boolean actualOnly;

    public EventsParser(boolean actualOnly)
    {
        this.actualOnly = actualOnly;
    }

    @Override
    //protected Object read(JsonReader reader, Object prevResult) throws IOException
    protected Object read(JsonReader reader) throws IOException
    {
        long now = _DateUtils.getTruncatedCurrentTime();
        reader.beginArray();
        final ArrayList<Event> list = new ArrayList<>(32);
        /*
        if (null != prevResult)
            list = (ArrayList<Event>) prevResult;
        else
            list = new ArrayList<>(INetworkRequest.PAGE_SIZE);
        */
        while (reader.hasNext())
        {
            Event event = readEvent(reader);

            if (event.id >= 0 &&
                    //( (0 == event.date) || (event.date >= now) )
                    (actualOnly ?  event.date >= now : true)
                    )
                list.add(event);
        }
        reader.endArray();
        return list;
    }
}

