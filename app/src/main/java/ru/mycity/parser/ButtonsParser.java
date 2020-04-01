package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

import ru.mycity.data.Button;


public class ButtonsParser extends JsonStreamParser
{
    private static final String BUTTON_ID    = "button_id";
    private static final String ICON         = "icon";
    private static final String LINK         = "link";
    private static final String ROW_ID       = "row_id";
    private static final String TABLE        = "table";



    @Override
    //protected Object read(JsonReader reader, Object prevResult) throws IOException
    protected Object read(JsonReader reader) throws IOException
    {
        reader.beginArray();
        final ArrayList<Button> list;

        //if (null != prevResult)
        //    list = (ArrayList<Button>) prevResult;
        //else
            list = (_defaultObjectCount > 0) ? new ArrayList<Button>(_defaultObjectCount) :
                    new ArrayList<Button>();

        while (reader.hasNext())
        {
            reader.beginObject();
            Button button = new Button();
            while (reader.hasNext())
            {
                final String name = reader.nextName();
                if (JsonToken.NULL == reader.peek())
                {
                    reader.skipValue();
                    continue;
                }
                final int len = name.length();

                if (equals(BUTTON_ID, name, len))
                {
                    button.id = reader.nextLong();
                    continue;
                }

                if (null == button.title && equals(CommonNames.TITLE, name, len))
                {
                    button.title = reader.nextString();
                    continue;
                }

                if (null == button.icon && equals(ICON, name, len))
                {
                    button.icon = reader.nextString();
                    continue;
                }

                if (null == button.link && equals(LINK, name, len))
                {
                    button.link = reader.nextString();
                    continue;
                }

                if (equals(ROW_ID, name, len) )
                {
                    if (JsonToken.NULL != reader.peek())
                        button.row_id = reader.nextInt();
                    else
                        reader.skipValue();
                    continue;
                }

                if (null == button.table && equals(TABLE, name, len))
                {
                    button.table = reader.nextString();
                    continue;
                }

                if (null == button.phone && equals(CommonNames.PHONE, name, len))
                {
                    button.phone = reader.nextString();
                    continue;
                }

                if (null == button.pic && equals(CommonNames.PIC, name, len))
                {
                    button.pic = reader.nextString();
                    continue;
                }

                if (equals(CommonNames.POS, name, len))
                {
                    button.pos = reader.nextInt();
                    continue;
                }

                if (null == button.image && equals(CommonNames.IMAGE, name, len))
                {
                    button.image = reader.nextString();
                    continue;
                }

                if (equals(CommonNames.IS_DELETED, name, len))
                {
                    button.deleted = 1 == reader.nextInt();
                    continue;
                }

                if (equals(CommonNames.PUBLISHED, name, len))
                {
                    button.published = 1 == reader.nextInt();
                    continue;
                }

                if (equals(CommonNames.UPDATED_AT, name, len))
                {
                    button.updated_at = reader.nextLong();
                    continue;
                }

                reader.skipValue();
            }
            reader.endObject();

            if (button.id >= 0)
                list.add(button);
        }
        reader.endArray();
        return list;
    }
}