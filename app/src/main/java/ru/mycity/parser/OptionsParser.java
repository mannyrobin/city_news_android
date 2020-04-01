package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.Option;


public class OptionsParser extends JsonStreamParser
{
    private static final String OPTION_ID    = "option_id";
    private static final String KEY          = "key";
    private static final String VALUE        = "value";

    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();
        final ArrayList<Option> list = new ArrayList<>();

        while (reader.hasNext())
        {
            reader.beginObject();
            Option option = new Option();
            while (reader.hasNext())
            {
                final String name = reader.nextName();
                if (JsonToken.NULL == reader.peek())
                {
                    reader.skipValue();
                    continue;
                }
                final int len = name.length();

                if (equals(OPTION_ID, name, len))
                {
                    option.id = reader.nextLong();
                    continue;
                }

                if (null == option.key && equals(KEY, name, len))
                {
                    option.key = reader.nextString();
                    continue;
                }

                if (null == option.type && equals(CommonNames.TYPE, name, len))
                {
                    option.type = reader.nextString();
                    continue;
                }

                if (null == option.value && equals(VALUE, name, len))
                {
                    option.value = reader.nextString();
                    continue;
                }

                if (equals(CommonNames.IS_DELETED, name, len))
                {
                    option.deleted = 1 == reader.nextInt();
                    continue;
                }

                if (equals(CommonNames.UPDATED_AT, name, len))
                {
                    option.updated_at = reader.nextLong();
                    continue;
                }

                reader.skipValue();
            }
            reader.endObject();

            if (option.id != 0 && null != option.key)
                list.add(option);
        }
        reader.endArray();
        return list;
    }
}



