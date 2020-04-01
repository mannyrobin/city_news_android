package ru.mycity.parser;

import com.nostra13.universalimageloader.utils.IoUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonObjectParser implements IParser
{
    @Override
    public Object parse(Object src) //, Object prevResult)
    {
        Reader reader = null;
        InputStream is;
        try
        {
            is = (InputStream) src;
            BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) src));
            StringBuilder total = new StringBuilder(is.available());
            String line;
            while ((line = r.readLine()) != null)
            {
                total.append(line);
            }
            return new JSONObject(total.toString());

        }
        catch (Throwable e)
        {

        }
        finally
        {
            if (null != reader)
            {
                IoUtils.closeSilently(reader);
            }
        }
        return null;
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

}
