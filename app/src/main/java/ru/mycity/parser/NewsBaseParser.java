package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.Date;

import ru.mycity.data.News;


abstract class NewsBaseParser extends JsonStreamParser
{
    private static final String NEWS_ID    = "news_id";
    private static final String ORIGIN_TITLE  = "origin_title";
    private static final String ORIGIN_LINK   = "origin_link";
    private static final String BODY          = "body";
    private static final String FEATURED = "featured";

    protected static News readNew(JsonReader reader) throws IOException
    {
        reader.beginObject();
        News news = new News();
        while (reader.hasNext())
        {
            final String name = reader.nextName();

            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }

            final int len = name.length();

            if (equals(NEWS_ID, name, len))
            {
                news.id = reader.nextLong();
                continue;
            }

            if (null == news.title && equals(CommonNames.TITLE, name, len))
            {
                news.title = reader.nextString();
                continue;
            }

            if (null == news.pic && equals(CommonNames.PIC, name, len))
            {
                news.pic = reader.nextString();
                continue;
            }

            if (null == news.origin_title && equals(ORIGIN_TITLE, name, len))
            {
                news.origin_title = reader.nextString();
                continue;
            }

            if (null == news.origin_link && equals(ORIGIN_LINK, name, len))
            {
                news.origin_link = reader.nextString();
                continue;
            }

            if (null == news.body && equals(BODY, name, len))
            {
                news.body = reader.nextString();
                continue;
            }

            if (equals(CommonNames.HIGHLIGHT, name, len))
            {
                news.highlight = 1 == reader.nextInt();
                continue;
            }

            if (equals(FEATURED, name, len))
            {
                news.featured = 1 == reader.nextInt();
                continue;
            }


            if (equals(CommonNames.PROMOTED, name, len))
            {
                news.promoted = reader.nextInt();
                continue;
            }

            if (equals(CommonNames.DATE, name, len))
            {
                Date date = DateParser.parse(reader.nextString());
                if (null != date)
                    news.date = date.getTime();
                continue;
            }

            if (equals(CommonNames.IS_DELETED, name, len))
            {
                news.deleted = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.UPDATED_AT, name, len))
            {
                news.updated_at = reader.nextLong();
                continue;
            }

            if (equals(CommonNames.PUBLISHED, name, len))
            {
                news.published = 1 == reader.nextInt();
                continue;
            }

            if (null == news.video && equals(CommonNames.VIDEO, name, len))
            {
                news.video = reader.nextString();
                continue;
            }

            if (equals(CommonNames.IS_VIDEO_HIDDEN, name, len))
            {
                news.is_video_hidden = 1 == reader.nextInt();
                continue;
            }

            reader.skipValue();
        }
        reader.endObject();
        return news;
    }

}
