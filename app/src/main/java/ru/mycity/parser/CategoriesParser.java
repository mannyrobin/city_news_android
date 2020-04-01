package ru.mycity.parser;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.mycity.data.Category;

public class CategoriesParser extends CategoriesBaseParser
{
    @Override
    //protected Object read(JsonReader reader, Object prevResult) throws IOException
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();
        final ArrayList<Category> list;
        //if (null != prevResult)
        //    list = (ArrayList<Category>) prevResult;
        //else
            list = (_defaultObjectCount > 0) ? new ArrayList<Category>(_defaultObjectCount) : new ArrayList<Category>();

        Comparator<Category> cmp = new Comparator<Category>(){

            @Override
            public int compare(Category o1, Category o2)
            {
                return ((o1.updated_at < o2.updated_at) ? -1 : ((o1.updated_at == o2.updated_at) ? 0 : 1));
            }
        };
        while (reader.hasNext())
        {
            Category category = readCategory(reader);
            if (null != category && category.id >= 0)
            {
                int pos = Collections.binarySearch(list, category, cmp);
                if (pos < 0)
                    list.add(0 - pos - 1, category);
                else
                    list.add(pos, category);
            }
        }
        reader.endArray();
        return list;
    }
}

