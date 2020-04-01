package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.Action;

/**
 * Created by Sergey Rumyantsev on 21.05.2016.
 */
public class ActionsParser extends ActionBaseParser
{
    @Override
    //protected Object read(JsonReader reader, Object prevResult) throws IOException
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();
        final ArrayList<Action> list = new ArrayList<>(32);
        /*
        if (null != prevResult)
            list = (ArrayList<Event>) prevResult;
        else
            list = new ArrayList<>(INetworkRequest.PAGE_SIZE);
        */
        while (reader.hasNext())
        {
            Action action = readAction(reader);

            if (action.id >= 0)
                list.add(action);
        }
        reader.endArray();
        return list;
    }
}


