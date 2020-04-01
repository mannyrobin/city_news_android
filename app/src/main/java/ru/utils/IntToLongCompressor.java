package ru.utils;

public class IntToLongCompressor 
{
    public static long pack(int x, int y) 
    {
        long xPacked = ((long)x) << 32;
        long yPacked = y & 0xFFFFFFFFL;
        return xPacked | yPacked;
    }

    public static int unpackX(long packed) 
    {
        return (int) (packed >> 32);
    }

    public static int unpackY(long packed) 
    {
        return (int) (packed & 0xFFFFFFFFL);
    }
}