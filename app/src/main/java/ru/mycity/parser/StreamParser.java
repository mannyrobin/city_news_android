package ru.mycity.parser;

import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class StreamParser implements IParser
{
    private final OutputStream os;

    public StreamParser(OutputStream os)
    {
        this.os = os;
    }

    public Object parse(Object src)
    {
        try
        {
            boolean v = IoUtils.copyStream((InputStream) src, os, null);
            return Boolean.valueOf(v);
        }
        catch (Throwable e)
        {
            if (null != e)
                System.err.print(e);
        }
        return Boolean.FALSE;
    }

    public boolean supportStream()
    {
        return true;
    }
    @Override
    public boolean selfClose()
    {
        return false;
    }

}
