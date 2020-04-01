package ru.mycity.utils;

public class DataUtils
{
    /*
    public static JSONObject readJSONObject(final Context context, String assetName, String fileName)
    {
        String data = read(context, assetName, fileName);
        if (null == data)
            return null;
        try
        {
            return new JSONObject(data);
        }
        catch (Throwable e)
        {

        }
        return null;
    }

    public static JSONArray readJSONArray(final Context context, String assetName, String fileName)
    {
        String data = read(context, assetName, fileName);
        if (null == data)
            return null;
        try
        {
            return new JSONArray(data);
        }
        catch (Throwable e)
        {
        }
        return null;
    }


    private static String read(final Context context, String assetName, String fileName)
    {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists())
        {
            InputStream in = null;
            try
            {
                String result = read(in = new FileInputStream(file));
                if (null != result)
                    return  result;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (null != in)
                    IoUtils.closeSilently(in);
            }
        }

        InputStream in = null;
        try
        {
            AssetManager am = context.getAssets();
            String result = read(in = am.open(assetName));
            if (null != result)
                return  result;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != in)
                IoUtils.closeSilently(in);
        }
        return null;
    }

    private static String read(InputStream inputStream)
    {
        BufferedReader in = null;
        try
        {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder(inputStream.available());
            String line;
            while ((line = r.readLine()) != null)
            {
                total.append(line);
            }
            return total.toString();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != in)
                IoUtils.closeSilently(in);
        }
        return null;
    }
    */
}


