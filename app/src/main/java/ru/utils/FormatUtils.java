package ru.utils;

public class FormatUtils
{

    public static final StringBuilder zeroPaddingAppendTwoDigitNumber(int value, StringBuilder buffer)
    {
        if (0 == value)
        {
            return buffer.append('0').append('0');
        }
        if (value < 10)
        {
            return buffer.append('0').append((char) ('0' + value));
        }
        buffer.append((char)('0' + value / 10));
        buffer.append((char)('0' + value % 10));
        return buffer;
    }

    public static final StringBuilder appendTwoDigitNumber(int value, StringBuilder buffer)
    {
        if (0 == value)
        {
            return buffer.append('0');
        }
        if (value < 10)
        {
            buffer.append((char) ('0' + value));
            return buffer;
        }
        buffer.append((char)('0' + value / 10));
        buffer.append((char)('0' + value % 10));
        return buffer;
    }
}