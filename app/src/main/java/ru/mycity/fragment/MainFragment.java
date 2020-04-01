package ru.mycity.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.mycity.MainActivity;
import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.adapter.ButtonsAdapter;
import ru.mycity.adapter.NewsAdapter;
import ru.mycity.data.Button;
import ru.mycity.data.News;
import ru.mycity.data.PushData;
import ru.mycity.data.RootData;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tasks.UpdateButtonsTask;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;

public class MainFragment extends BaseFragment implements AdapterView.OnItemClickListener, IResultCallback
{
    public static final String NAME = "MainFragment";
    private ListView listView;



    @Override
    protected CharSequence getTitle()
    {
        return getText(R.string.app_name);
    }

    @Override
    public View createView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        listView = (ListView) rootView.findViewById(R.id.list);
        //ArrayList<News> news = (null != rootData) ? rootData.news : null;
        final _Application application = (_Application) getActivity().getApplication();
        final RootData    rootData = application._rootData;

        ArrayList<News> news = (null != rootData) ?
                rootData.topNews : new ArrayList<News>();


        final ArrayList<Button> buttons = (null != rootData) ? rootData.buttons : null;
        if (null != buttons  && !buttons.isEmpty())// && null != news && !news.isEmpty())// && buttons.size() > 100)
        {
            initListView(inflater, news, buttons);
        }
        else
        {
            //Log.d("0c0", "0c0 " + " buttons empty");
            AsyncTask task = new UpdateButtonsTask(application, buttons, news, 0, this);
            application.getAsyncTaskExecutor().execute(task, (Void) null);

            /*
            final ArrayList<News> _news = news;
            listView.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    ArrayList<Button> b = DbButtonsHelper.getButtons(application.getDbHelper().getReadableDatabase());
                    rootData.buttons = b;
                    initListView(inflater, listView, _news , b);
                }
            }, 10);
            */
        }
        return rootView;
    }

    void initListView(LayoutInflater inflater, ArrayList<News> news, ArrayList<Button> buttons)
    {
        final ArrayList<Button> _buttons = (null != buttons) ? buttons : new ArrayList<Button>(1);
        addHeader(inflater, listView, new ButtonsAdapter(inflater, _buttons));
        listView.setAdapter(new NewsAdapter(inflater, news));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //Adapter adapter = parent.getAdapter();
        //News item = (News) adapter.getItem(position);

        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) parent.getAdapter();
        NewsAdapter adapter = (NewsAdapter) headerViewListAdapter.getWrappedAdapter();
        News item = adapter.getItemAtPosition(position - 1); //-headerViewListAdapter.getHeadersCount());

        NewFragment f = new NewFragment();
        f.setData(item, item.title, null);
        openDetailFragment(f, NewFragment.NAME);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        _Application application  = (_Application) view.getContext().getApplicationContext();
        RootData    rootData = application._rootData;
        PushData pushData = (null != rootData) ? rootData.pushData : null;
        if (null != pushData)
        {
            MainActivity activity = (MainActivity) getActivity();
            if (null != activity)
            {
                activity.openItem(pushData);
            }
            rootData.pushData = null;
        }
    }

    private void addHeader(LayoutInflater inflater, final ListView listView, final ButtonsAdapter buttonsAdapter)
    {
        View header = inflater.inflate(R.layout.buttons_list, null);
        final ru.utils.ExpandableHeightGridView gridView = (ru.utils.ExpandableHeightGridView) header.findViewById(R.id.tiles);
        gridView.setAdapter(buttonsAdapter);
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Button button = (Button) parent.getAdapter().getItem(position);
                MainActivity activity = (MainActivity) getActivity();
                if (null != activity)
                {
                    if (null != button.table && 0 != button.table.length())
                    {
                        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(activity), ITrackerEvents.CATEGORY_BUTTONS, ITrackerEvents.ACTION_CLICK, TrackerEventHelper.makeLabel(ITrackerEvents.LABEL_ACTION_BUTTON, String.valueOf(button.id)), TrackerEventHelper.makeLabel(button.table, TrackerEventHelper.makeLabel("click", String.valueOf(button.row_id)))));
                    }
                    activity.openItem(button.table, button.row_id, button.link, button.phone, false);
                }
            }
        });

        final int buttonSize = getResources().getDimensionPixelSize(R.dimen.button_grid_size);
        final int buttonSpacing = getResources().getDimensionPixelSize(R.dimen.button_grid_horizontalSpacing);

        gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if (buttonsAdapter.getNumColumns() == 0)
                {
                    final int numColumns = (int) Math.floor(gridView.getWidth() / (buttonSize + buttonSpacing));

                    if (numColumns > 0)
                    {
                        //final int columnWidth = (gridView.getWidth() / numColumns) - buttonSpacing;
                        final int columnWidth = (gridView.getWidth() - buttonSpacing) / numColumns - buttonSpacing;
                        buttonsAdapter.setNumColumns(numColumns);
                        buttonsAdapter.setItemHeight(columnWidth);
                    }
                }
            }
        });

        listView.addHeaderView(header);

        /*

        //ExpandableHeightListView lv = (ExpandableHeightListView) footer;
        ExpandableHeightListView lv = (ExpandableHeightListView) footer.findViewById(R.id.list);

        View empty = footer.findViewById(android.R.id.text1);
        lv.setEmptyView(empty);

        lv.setAdapter(organizationsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CategoriesFragment.super.onItemClick(parent, view, position, id);
            }
        });
        //lv.setVisibility(View.GONE);

        lv.setExpanded(true);
        */
    }

    @Override
    public void onFinished(int rc, Result result)
    {
        if (0 == rc)
        {
            if (null != result)
            {
                UpdateButtonsTask.Result  res  = (UpdateButtonsTask.Result) result;
                initListView(LayoutInflater.from(getContext()), res.news, res.buttons);
            }
        }
    }

    @Override
    public void onFailed(int rc, Throwable error)
    {

    }

    @Override
    public void onFailed(int rc, String description)
    {

    }


    /*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.main_fragment, container,false);
        GridView gridView = (GridView) rootView.findViewById(R.id.tiles);
        List<Button> buttons = (null != rootData) ? rootData.buttons : null;
        if (null != buttons && !buttons.isEmpty())
        {
            gridView.setAdapter(new ButtonsAdapter(inflater, buttons));
            gridView.setOnItemClickListener(this);
        }
        else
        {
            gridView.setVisibility(View.GONE);
        }
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Button button = (Button) parent.getAdapter().getItem(position);
        MainActivity activity = (MainActivity) getActivity();
        if (null != activity)
        {
            activity.openLink(button.table, button.row_id, false);
        }
    }
    */
}


