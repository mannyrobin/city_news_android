package ru.mycity.tasks;

import android.os.AsyncTask;

import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ChangeCacheDestinationTask extends AsyncTask<File, Void, Void>
{
    public ChangeCacheDestinationTask()
    {
    }

    @Override
    protected Void doInBackground(File... params)
    {
        try
        {
            File from = params[0];
            File to = params[1];
            moveFiles(from.listFiles(), to, true);
        }
        catch (Throwable e)
        {
        }
        return null;
    }

    private boolean moveFiles(File[] list, File ext, boolean bDelete)
    {
        if (null == list)
        {
            return false;
        }
        final int len = list.length;
        if (0 == len)
        {
            return true;
        }

        for (int i = 0; i < len; i++)
        {
            final File file = list[i];
            if (null == file)
            {
                continue;
            }
            try
            {
                IoUtils.copyStream(new FileInputStream(file), new FileOutputStream(new File(ext, file.getName())), null, 4096);
            }
            catch (Throwable e)
            {
            }
            if (bDelete)
            {
                file.delete();
            }
        }
        return true;
    }
}


