package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.Date;

import ru.mycity.data.Event;

abstract class EventsBaseParser extends JsonStreamParser
{
    private static final String EVENT_ID    = "event_id";
    protected static Event readEvent(JsonReader reader) throws IOException
    {
        reader.beginObject();
        Event event = new Event();
        while (reader.hasNext())
        {
            final String name = reader.nextName();

            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }

            final int len = name.length();

            if (equals(EVENT_ID, name, len))
            {
                event.id = reader.nextLong();
                continue;
            }

            if (null == event.title && equals(CommonNames.TITLE, name, len))
            {
                event.title = reader.nextString();
                continue;
            }

            if (null == event.address && equals(CommonNames.ADDRESS, name, len))
            {
                event.address = reader.nextString();
                continue;
            }

            if (null == event.photos && equals(CommonNames.FOTOS, name, len))
            {
                event.photos = readListAsString(reader);
                continue;
            }

            if (null == event.avatar && equals(CommonNames.AVATAR, name, len))
            {
                event.avatar = reader.nextString();
                continue;
            }

            if (equals(CommonNames.DATE, name, len))
            {
                Date date = DateParser.parse(reader.nextString());
                if (null != date)
                    event.date = date.getTime();
                continue;
            }

            if (null == event.type && equals(CommonNames.TYPE, name, len))
            {
                event.type = reader.nextString();
                continue;
            }

            if (null == event.price && equals(CommonNames.PRICE, name, len))
            {
                event.price = reader.nextString();
                continue;
            }

            if (null == event.phones && equals(CommonNames.PHONES, name, len))
            {
                event.phones = reader.nextString();
                continue;
            }

            if (null == event.info && equals(CommonNames.INFO, name, len))
            {
                event.info = reader.nextString();
                continue;
            }

            if (equals(CommonNames.SITE, name, len))
            {
                String s  = reader.nextString();
                if (null != s && 0 != s.length())
                    event.site = s;
                continue;
            }

            if (null == event.site_title && equals(CommonNames.SITE_TITLE, name, len))
            {
                event.site_title = reader.nextString();
                continue;
            }

            if (equals(CommonNames.ORGANIZATION_ID, name, len))
            {
                event.organization_id = reader.nextInt();
                continue;
            }

            if (equals(CommonNames.HIGHLIGHT, name, len))
            {
                event.highlight = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.PROMOTED, name, len))
            {
                event.promoted = reader.nextInt();
                continue;
            }

            if (equals(CommonNames.UPDATED_AT, name, len))
            {
                event.updated_at = reader.nextLong();
                continue;
            }

            if (equals(CommonNames.IS_DELETED, name, len))
            {
                event.deleted = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.PUBLISHED, name, len))
            {
                event.published = 1 == reader.nextInt();
                continue;
            }

            if (null == event.video && equals(CommonNames.VIDEO, name, len))
            {
                event.video = reader.nextString();
                continue;
            }

            if (equals(CommonNames.IS_VIDEO_HIDDEN, name, len))
            {
                event.is_video_hidden = 1 == reader.nextInt();
                continue;
            }

            reader.skipValue();
        }
        reader.endObject();
        return event;
    }
}
