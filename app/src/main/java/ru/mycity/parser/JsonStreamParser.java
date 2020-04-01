package ru.mycity.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import ru.mycity.data.Common;

public abstract class JsonStreamParser implements IParser
{
    protected  int _defaultObjectCount = 0;

    protected static boolean equals(final String key, final String value, final int l)
    {
        return l == key.length() && key.regionMatches(0, value, 0, l);
    }

    @Override
    public Object parse(Object src) //, Object prevResult)
    {
        Reader reader = null;
        JsonReader jsonReader = null;
        InputStream is = null;
        try
        {
            is = (InputStream) src;
            //reader = new BufferedReader(new InputStreamReader(is));
            reader = new InputStreamReader(is);
            jsonReader = new JsonReader(reader);
            Object result = read(jsonReader); //, prevResult);
            return result;
        }
        catch (Throwable e)
        {
            onError(e);
        }
        finally
        {
            Closeable obj = (null != jsonReader) ? jsonReader : (null != reader) ? reader : is;
            if (null != obj)
            {
                IoUtils.closeSilently(obj);
            }
            onFinish();
        }
        return null;
    }

    //abstract protected Object read(JsonReader reader, Object prevResult) throws IOException;
    abstract protected Object read(JsonReader reader) throws IOException;

    protected void onError(Throwable e)
    {
        System.err.print(e);
    }

    protected void onFinish()
    {
    }

    @Override
    public boolean supportStream()
    {
        return true;
    }

    @Override
    public boolean selfClose()
    {
        return true;
    }

    protected static String readListAsString(JsonReader reader) throws IOException
    {
        StringBuilder sb = null;
        reader.beginArray();
        while (reader.hasNext())
        {
            if (JsonToken.NULL == reader.peek())
            {
                reader.skipValue();
                continue;
            }
            String s = reader.nextString();
            if (null != s && 0 != s.length())
            {
                if (null == sb)
                    sb = new StringBuilder(32);
                if (sb.length() > 0)
                    sb.append(Common.LIST_DELIMITER);
                sb.append(s);
            }
        }
        reader.endArray();
        if (null != sb && 0 != sb.length())
            return sb.toString();
        return null;
    }

    /*
    protected static String readNullableString(JsonReader reader) throws IOException
    {
        if (JsonToken.NULL != reader.peek())
        {
            String s = reader.nextString();
            if (null != s && 0 != s.length())
                return s;
        }
        else
        {
            reader.skipValue();
        }
        return null;
    }
    */

    public JsonStreamParser setDefaultObjectCount(int defaultObjectCount)
    {
        this._defaultObjectCount = defaultObjectCount;
        return this;
    }

}


