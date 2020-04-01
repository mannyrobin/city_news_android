package com.readystatesoftware.sqliteasset;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Utils
{
    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();

    public static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer))>0){
            outs.write(buffer, 0, length);
        }
        outs.flush();
        outs.close();
        in.close();
    }

    public static ZipInputStream getFileFromZip(InputStream zipFileStream) throws IOException {
        ZipInputStream zis = new ZipInputStream(zipFileStream);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            Log.w(TAG, "extracting file: '" + ze.getName() + "'...");
            return zis;
        }
        return null;
    }

    /*
    public static List<String> splitSqlScript(String script, char delim)
    {
        List<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inLiteral = false;
        char[] content = script.toCharArray();
        int len = script.length();
        for (int i = 0; i < len; i++)
        {
            if (content[i] == '"')
            {
                inLiteral = !inLiteral;
            }
            if (content[i] == delim && !inLiteral)
            {
                if (sb.length() > 0)
                {
                    statements.add(sb.toString().trim());
                    sb = new StringBuilder();
                }
            }
            else
            {
                sb.append(content[i]);
            }
        }
        if (sb.length() > 0)
        {
            statements.add(sb.toString().trim());
        }
        return statements;
    }

    public static String convertStreamToString(InputStream is)
    {
        return new Scanner(is).useDelimiter("\\A").next();

    }
    */

    public static List<String> splitSqlScript(InputStream inputStream, boolean close) throws IOException
    {
        List<String> statements = new ArrayList<String>();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (0 == line.length())
                    continue;
                if (line.startsWith("--"))
                    continue;
                int index = line.indexOf(';');
                if (index > 0)
                {
                    sb.append(line, 0, index + 1);
                    sb.append(' ');
                    statements.add(sb.toString());
                    sb.setLength(0);
                    int len = line.length() - index;
                    if (len > 1)
                        sb.append(line, index + 1, len);
                }
                else
                {
                    sb.append(line).append(' ');
                }
            }

            if (null != sb && 0 != sb.length())
            {
                statements.add(sb.toString());
            }
        }
        finally
        {
            if (close)
            {
                if (null != reader)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (Throwable e)
                    {
                    }
                }
            }
        }
        return statements;
    }
}
