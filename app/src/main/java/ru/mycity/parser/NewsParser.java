package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.News;


public class NewsParser extends NewsBaseParser
{
    @Override
    //protected Object read(JsonReader reader, Object prevResult) throws IOException
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();
        final ArrayList<News> list = (_defaultObjectCount > 0)? new ArrayList<News>(_defaultObjectCount) :
                new ArrayList<News>();
        /*
        final ArrayList<News> list;
        if (null != prevResult)
            list = (ArrayList<News>) prevResult;
        else
            list = new ArrayList<>(INetworkRequest.PAGE_SIZE);
        */
        while (reader.hasNext())
        {
            News news = readNew(reader);
            if (news.id >= 0)
                list.add(news);
        }
        reader.endArray();
        return list;
    }
}
