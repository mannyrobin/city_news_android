package ru.mycity.parser;


import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

import ru.mycity.data.Category;

abstract class CategoriesBaseParser extends JsonStreamParser
{
    private static final String CATEGORY_ID    = "category_id";
    private static final String PARENT_ID      = "parent_id";

    protected static Category readCategory(JsonReader reader) throws IOException
    {
        reader.beginObject();
        Category category = new Category();
        while (reader.hasNext())
        {
            final String name = reader.nextName();
            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }
            final int len = name.length();

            if (null == category.name && equals(CommonNames.NAME, name, len))
            {
                category.name = reader.nextString();
                continue;
            }

            if (equals(CATEGORY_ID, name, len))
            {
                category.id = reader.nextLong();
                continue;
            }

            if (equals(PARENT_ID, name, len) )
            {
                category.parent_id = reader.nextLong();
                continue;
            }

            if (equals(CommonNames.POS, name, len))
            {
                category.position = reader.nextInt();
                continue;
            }

            if (null == category.picture && equals(CommonNames.PIC, name, len))
            {
                category.picture = reader.nextString();
                continue;
            }

            if (null == category.image && equals(CommonNames.IMAGE, name, len))
            {
                category.image = reader.nextString();
                continue;
            }

            if (equals(CommonNames.IS_DELETED, name, len))
            {
                category.deleted = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.PUBLISHED, name, len))
            {
                category.published = 1 == reader.nextInt();
                continue;
            }

            if (equals(CommonNames.UPDATED_AT, name, len))
            {
                category.updated_at = reader.nextLong();
                continue;
            }

            reader.skipValue();
        }
        reader.endObject();
        return category;
    }
}
