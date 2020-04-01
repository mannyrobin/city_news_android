package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.OrganizationCategory;

public class OrganizationsCategoriesParser extends JsonStreamParser
{
    private static final String CATEGORY_ID = "category_id";


    @Override
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();
        //"id":"146778","pos":"5504","category_id":"248","organization_id":"74668","updated_at":
        final ArrayList<OrganizationCategory> organizationCategories;
        if (_defaultObjectCount > 0)
            organizationCategories = new ArrayList<>(_defaultObjectCount);
        else
            organizationCategories = new ArrayList<>();

        while (reader.hasNext())
        {
            reader.beginObject();
            OrganizationCategory obj = new OrganizationCategory();

            while (reader.hasNext())
            {
                final String name = reader.nextName();
                /*
                if (JsonToken.NULL == reader.peek())
                {
                    reader.skipValue();
                    continue;
                }*/

                final int len = name.length();

                if (equals(CommonNames.ID, name, len))
                {
                    obj.id = reader.nextLong();
                    continue;
                }

                if (equals(CommonNames.POS, name, len))
                {
                    obj.pos = reader.nextInt();
                    continue;
                }

                if (equals(CATEGORY_ID, name, len))
                {
                    obj.categoryId = reader.nextLong();
                    continue;
                }

                if (equals(CommonNames.ORGANIZATION_ID, name, len))
                {
                    obj.organizationId = reader.nextLong();
                    continue;
                }

                if (equals(CommonNames.UPDATED_AT, name, len))
                {
                    obj.updatedAt = reader.nextLong();
                    continue;
                }

                if (equals(CommonNames.IS_DELETED, name, len))
                {
                    obj.deleted = 1 == reader.nextInt();
                    continue;
                }

                reader.skipValue();
            }
            reader.endObject();
            if (obj.id  >= 0 )
                organizationCategories.add(obj);
        }
        reader.endArray();
        return organizationCategories;
    }

}
