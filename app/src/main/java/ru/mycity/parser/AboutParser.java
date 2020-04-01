package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.About;

public class AboutParser extends JsonStreamParser
{
    private static final String PIC_LINK      = "pic_link";
    private static final String DESC          = "desc";
    private static final String ABOUT_PROJECT = "about_project";
    private static final String OWNERS        = "owners";

    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginObject();
        About about = new About();
        while (reader.hasNext())
        {
            final String name = reader.nextName();
            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }
            final int len = name.length();

            if (null == about.phone && equals(CommonNames.PHONE, name, len))
            {
                about.phone = reader.nextString();
                continue;
            }

            if (null == about.about_project && equals(ABOUT_PROJECT, name, len))
            {
                about.about_project = reader.nextString();
                continue;
            }

            if (equals(OWNERS, name, len))
            {
                about.owners = readOwners(reader);
                continue;
            }

            if (equals(CommonNames.UPDATED_AT, name, len))
            {
                about.updated_at = reader.nextLong();
                continue;
            }

            reader.skipValue();
        }
        reader.endObject();
        return about;
    }

    private ArrayList<About.Owner> readOwners(JsonReader reader) throws IOException
    {
        reader.beginArray();

        ArrayList<About.Owner> list = null;

        while (reader.hasNext())
        {
            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }
            reader.beginObject();
            About.Owner owner = new About.Owner();
            while (reader.hasNext())
            {
                final String name = reader.nextName();
                if (JsonToken.NULL == reader.peek())
                {
                    reader.skipValue();
                    continue;
                }
                final int len = name.length();

                if (null == owner.pic_link && equals(PIC_LINK, name, len))
                {
                    owner.pic_link = reader.nextString();
                    continue;
                }
                if (null == owner.desc && equals(DESC, name, len))
                {
                    owner.desc = reader.nextString();
                    continue;
                }

                if (null == owner.name && equals(CommonNames.NAME, name, len))
                {
                    owner.name = reader.nextString();
                    continue;
                }
                reader.skipValue();
            }
            if (null == owner.name)
                owner.name="";

            //if (null != owner.name)
            {
                if (null == list)
                    list = new ArrayList<>();
                list.add(owner);
            }
            reader.endObject();
        }
        reader.endArray();
        return list;
    }


}
