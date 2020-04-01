package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.Date;

import ru.mycity.data.Action;

abstract class ActionBaseParser extends JsonStreamParser
{
    private static final String ACTION_ID    = "action_id";

    protected static Action readAction(JsonReader reader) throws IOException
    {
        reader.beginObject();
        Action action = new Action();
        while (reader.hasNext())
        {
            final String name = reader.nextName();

            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }

            final int len = name.length();

            if (equals(ACTION_ID, name, len))
            {
                action.id = reader.nextLong();
                continue;
            }

            //if (0 == action.id && equals("event_id", name, len))
            //{
            //    action.id = reader.nextLong();
            //    continue;
            //}

            if (null == action.title && equals(CommonNames.TITLE, name, len))
            {
                action.title = reader.nextString();
                continue;
            }

            if (null == action.address && equals(CommonNames.ADDRESS, name, len))
            {
                action.address = reader.nextString();
                continue;
            }

            if (null == action.photos && equals(CommonNames.FOTOS, name, len))
            {
                action.photos = readListAsString(reader);
                continue;
            }

            if (null == action.avatar && equals(CommonNames.AVATAR, name, len))
            {
                action.avatar = reader.nextString();
                continue;
            }

            if (equals(CommonNames.DATE_SINCE, name, len))
            {
                Date date = DateParser.parse(reader.nextString());
                if (null != date)
                    action.date = date.getTime();
                continue;
            }

            if (equals(CommonNames.DATE_BEFORE, name, len))
            {
                Date date = DateParser.parse(reader.nextString());
                if (null != date)
                    action.date_end = date.getTime();
                continue;
            }

            if (null == action.price && equals(CommonNames.PRICE, name, len))
            {
                action.price = reader.nextString();
                continue;
            }

            if (null == action.phones && equals(CommonNames.PHONES, name, len))
            {
                action.phones = reader.nextString();
                continue;
            }

            if (null == action.info && equals(CommonNames.INFO, name, len))
            {
                action.info = reader.nextString();
                continue;
            }

            if (equals(CommonNames.SITE, name, len))
            {
                String s  = reader.nextString();
                if (null != s && 0 != s.length())
                    action.site = s;
                continue;
            }

            if (null == action.site_title && equals(CommonNames.SITE_TITLE, name, len))
            {
                action.site_title = reader.nextString();
                continue;
            }

            if (equals(CommonNames.ORGANIZATION_ID, name, len))
            {
                action.organization_id = reader.nextInt();
                continue;
            }

            if (equals(CommonNames.HIGHLIGHT, name, len))
            {
                action.highlight = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.PROMOTED, name, len))
            {
                action.promoted = reader.nextInt();
                continue;
            }

            if (equals(CommonNames.UPDATED_AT, name, len))
            {
                action.updated_at = reader.nextLong();
                continue;
            }

            if (equals(CommonNames.IS_DELETED, name, len))
            {
                action.deleted = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.PUBLISHED, name, len))
            {
                action.published = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.TYPE, name, len))
            {
                action.life_time_type = reader.nextInt();
                continue;
            }

            if (null == action.video && equals(CommonNames.VIDEO, name, len))
            {
                action.video = reader.nextString();
                continue;
            }

            if (equals(CommonNames.IS_VIDEO_HIDDEN, name, len))
            {
                action.is_video_hidden = 1 == reader.nextInt();
                continue;
            }

            reader.skipValue();
        }
        reader.endObject();
        return action;
    }
}
