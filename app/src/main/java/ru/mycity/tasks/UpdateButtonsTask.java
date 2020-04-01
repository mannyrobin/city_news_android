package ru.mycity.tasks;

import java.util.ArrayList;

import ru.mycity._Application;
import ru.mycity.data.Button;
import ru.mycity.data.News;
import ru.mycity.data.RootData;
import ru.mycity.database.DbButtonsHelper;
import ru.mycity.database.DbNewsHelper;

public class UpdateButtonsTask extends UpdateTask
{
    private final ArrayList<Button> buttons;
    private final ArrayList<News> news;
    private final RootData rootData;
    private ArrayList<News> _news;
    private ArrayList<Button> _buttons;

    public UpdateButtonsTask(_Application application, ArrayList<Button> buttons, ArrayList<News> news, int rc, IResultCallback resultCallback)
    {
        super(application, rc, resultCallback);
        this.buttons = buttons;
        this.news = news;
        this.rootData = application._rootData;
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        _buttons = buttons;
        if (null != _buttons && !_buttons.isEmpty())
        {
        }
        else
        {
            _buttons = readButtonsFromDB();
            if (null != _buttons && !_buttons.isEmpty())
            {
            }
            else
            {
                if (loadButtons(0) > 0)
                    _buttons = readButtonsFromDB();
            }
        }

        _news = news;
        if (null != _news && !_news.isEmpty())
        {
        }
        else
        {
            _news = readNewsFromDB();

            if (null != _news && !_news.isEmpty())
            {
            }
            else
            {
                if (loadNews(0, 10) > 0)
                    _news = readNewsFromDB();
            }
        }
        if (null == _news)
            _news = new ArrayList<>();
        if (null == _buttons)
            _buttons = new ArrayList<>();

        if (null != rootData)
        {
            rootData.buttons = _buttons;
            rootData.topNews = _news;
        }
        return Boolean.TRUE;
    }

    private ArrayList<Button> readButtonsFromDB()
    {
        try
        {
            return DbButtonsHelper.getButtons(application.getDbHelper().getReadableDatabase());
        }
        catch (Throwable e)
        {

        }
        return null;
    }

    private ArrayList<News> readNewsFromDB()
    {
        try
        {
            return DbNewsHelper.getTopNews(application.getDbHelper().getReadableDatabase());
        }
        catch (Throwable e)
        {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        if (null != resultCallback)
        {
            Result result = new Result();
            result.buttons = _buttons;
            result.news = _news;
            resultCallback.onFinished(rc, result);
        }
    }

    public static class Result extends IResultCallback.Result
    {
        public ArrayList<Button> buttons;
        public ArrayList<News> news;
    }
}
