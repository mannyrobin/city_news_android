package ru.mycity.network;


import android.support.annotation.NonNull;
import android.util.Log;

import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import ru.mycity.AppData;
import ru.mycity.BuildConfig;
import ru.mycity.parser.IParser;
import ru.utils.NameValueItem;

public class HttpClient
{
    private final static int CONNECT_TIMEOUT = 3000;
    public  final static int READ_TIMEOUT   =  6000;
    //public final static int PAGE_SIZE = 50;

    public final static Result executeGet(String _url, IParser parser)
    {
        return executeGet(_url, READ_TIMEOUT, false, parser);
    }

    public final static Result executePost(String _url, NameValueItem[] postParameters, IParser parser)
    {
        Result result = new Result();
        read(result, _url, CONNECT_TIMEOUT, READ_TIMEOUT, false, postParameters, parser);
        return result;
    }

    public final static Result executeGet(String _url, int readTimeOut, boolean getPageCount, IParser parser)
    {
        Result result = new Result();
        String url = _url;
        /*
        if (url.lastIndexOf('?') > 0)
            url +='&';
        else
            url +='?';

        url += "per-page=-1";
        */

        read(result, url, CONNECT_TIMEOUT, readTimeOut, getPageCount, null, parser);
        return result;

        /*
        Result result = new Result();
        String url = _url;
        if (url.lastIndexOf('?') > 0)
            url +='&';
        else
            url +='?';
        url += "per-page=" + INetworkRequest.PAGE_SIZE + "&page=";

        result.pageCount = 1;
        int page = 1;
        while (page <= result.pageCount)
        {
            url += page;
            getPage(result, url, parser);
            if (result.rc != Result.SUCCESS)
                break;
            page++;
        }
        return result;
        */
    }



    private static NameValueItem [] headers = new NameValueItem[]
    {
            new NameValueItem("User-Agent"  , "Android"),
            //new NameValueItem("Content-Type", "application/json;charset=utf-8"),
            //new NameValueItem("connection"      , "close")
            new NameValueItem("Accept"      , "application/json"),
            new NameValueItem("X-Moygorod-Application-Id"   , AppData.getAppId())
    };


    @NonNull
    private static void read(Result result, String _url, int connectTimeout, int readTimeout, boolean getPageCount, NameValueItem[] postParameters, IParser parser)
    {
        InputStream inputStream = null;

        try
        {
            URL url = new URL(_url);
            URLConnection urlConnection = url.openConnection();
            //urlConnection.setDoOutput();

            if (null != postParameters)
            {
                urlConnection.setDoOutput(true);
            }

            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);


            for (NameValueItem header : headers)
                urlConnection.setRequestProperty(header.name  , header.value);


            urlConnection.connect();

            if (null != postParameters && 0 != postParameters.length)
            {
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(postParametersToString(postParameters));
                out.close();
            }


            /*
            final int resp = ((HttpURLConnection) urlConnection).getResponseCode();
            if ((resp != 200) && (resp != 201) && (resp != 202) )
            {
                result.description = ((HttpURLConnection) urlConnection).getResponseMessage();
                result.rc = Result.NETWORK_ERROR;
                result.parseData = null;
                return;
            }
            */


            inputStream = urlConnection.getInputStream();


            Object data = parser.parse(inputStream);
            if (parser.selfClose())
            {
                inputStream = null;
            }

            result.rc = Result.SUCCESS;
            result.parseData = data;
            if (getPageCount)
            {
                String s = urlConnection.getHeaderField("X-Pagination-Page-Count");
                int count = (null != s) ? Integer.parseInt(s) : 0;
                if (result.pageCount < count)
                    result.pageCount = count;
            }
        }
        catch (IOException e)
        {
            if (null != e && e instanceof  java.net.SocketTimeoutException)
                result.rc = Result.NETWORK_TIMEOUT_ERROR;
            else
                result.rc = Result.NETWORK_ERROR;
            result.parseData = null;
            result.description = e.getMessage();
            if (BuildConfig.DEBUG)
            {
                Log.e("0c0", "0c0 " + e.getMessage(), e);
            }
        }
        catch (Throwable ex2)
        {
            result.rc = Result.OTHER_ERROR;
            result.parseData = null;
            result.description = ex2.getMessage();
            if (BuildConfig.DEBUG)
            {
                Log.e("0c0", "0c0 " + ex2.getMessage(), ex2);
            }
        }
        finally
        {
            if (null != inputStream)
            {
                IoUtils.closeSilently(inputStream);
            }
        }
    }

    private static String postParametersToString(NameValueItem[] params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder(params.length * 32);
        boolean first = true;

        for (NameValueItem pair : params)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                result.append("&");
            }
            //result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append(pair.name);
            result.append('=');
            result.append(URLEncoder.encode(pair.value, "UTF-8"));
        }
        return result.toString();
    }

    public final static class Result
    {
        public final static int SUCCESS = 0;
        public final static int NETWORK_ERROR = -1;
        public final static int NETWORK_TIMEOUT_ERROR = -1;
        public final static int OTHER_ERROR = -3;

        public int rc;
        public int pageCount;
        public String description;
        public Object parseData;
    }

}