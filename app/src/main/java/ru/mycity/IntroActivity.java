package ru.mycity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.DefaultIndicatorController;
import com.github.paolorotolo.appintro.IndicatorController;
import com.google.android.gms.analytics.HitBuilders;

import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerAdapter;


public class IntroActivity extends StartActivity implements ViewPager.OnPageChangeListener
{
    private IndicatorController mController;
    private Button nextButton;
    ViewPager pager;
    //private _PagerAdapter pagerAdapter;
    private boolean dataRead;
    private boolean introFinished;
    //static int PAGE_COUNT = 3;
    static int PAGE_COUNT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        init();

        if (checkPermissions())
        {
            readData();
        }
    }


    private void init()
    {
        setContentView(R.layout.intro);
        //dumpIntent(getIntent());
        initController(PAGE_COUNT);
        initViewPager();
        initNextButton();
    }

    private void initNextButton()
    {
        nextButton = (Button) findViewById(android.R.id.button1);

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != pager)
                {
                    int item = pager.getCurrentItem() + 1;
                    if (item == PAGE_COUNT)
                    {
                        onFinish();
                    }
                    else
                    {
                        pager.setCurrentItem(item);
                    }
                }
            }
        });
    }

    void onFinish()
    {
        introFinished = true;
        if (dataRead)
        {
            close();
        }
        else
        {
            setContentView(R.layout.splash);
            TextView tv = (TextView) findViewById(R.id.intro);
            if (null != tv) tv.setText(R.string.s11);
        }
    }


    private void initViewPager()
    {
        pager = (ViewPager) findViewById(R.id.pager);
        //pagerAdapter = new _PagerAdapter(createViews());
        //pagerAdapter = new _PagerAdapter(getLayoutInflater());
        //pager.setAdapter(pagerAdapter);
        pager.setAdapter(new _PagerAdapter(getLayoutInflater()));
        pager.addOnPageChangeListener(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        //init();
        if (null != pager)
        {
            //_PagerAdapter pagerAdapter = (_PagerAdapter) pager.getAdapter();
            //pagerAdapter.setInflater(getLayoutInflater());
            //pager.removeAllViews();
            //pager.invalidate();
            ////pager.invalidate();
            //pagerAdapter.setPages(createViews());
            //pager.invalidate();
            //pagerAdapter.notifyDataSetChanged();
            //pager.invalidate();

            //pagerAdapter = new _PagerAdapter(getLayoutInflater());
            //pager.setAdapter(pagerAdapter);
            //pager.setAdapter(null);

            //pager.postDelayed(new Runnable()
            //{
            //    @Override
            //    public void run()
            //    {
                    //pager.setAdapter(new _PagerAdapter(getLayoutInflater()));
            pager.getAdapter().notifyDataSetChanged();
            //    }
            //}, 1000);

            //pager.setAdapter(new _PagerAdapter(getLayoutInflater()));
            //pager.setCurrentItem(0);
            //pager.invalidate();

        }
    }


    /*
    @NonNull
    private ArrayList<View> createViews()
    {
        ArrayList<View> views = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.intro1, null);
        TextView tv = (TextView) v.findViewById(android.R.id.text2);
        tv.setText(getString(R.string.intro1_text2, getString(R.string.app_name)));
        views.add(v);
        views.add(inflater.inflate(R.layout.intro2, null));
        views.add(inflater.inflate(R.layout.intro3, null));
        return views;
    }
    */

    private void initController(int count)
    {
        mController = new DefaultIndicatorController();
        mController.newInstance(this, (LinearLayout) findViewById(R.id.indicator));
        mController.initialize(count);
    }

    protected void onDataRead()
    {
        dataRead = true;
        if (introFinished)
        {
            close();
        }
    }

    private void close()
    {
        SharedPreferences prefs = Config.getSharedPreferences(this);
        Config.putBoolean(prefs, Config.intro_shown, true);

        _Application application = (_Application) getApplication();
        TrackerAdapter tracker = application.getTracker();
        if (null != tracker)
            tracker.send(new HitBuilders.EventBuilder()
                            .setCategory(ITrackerEvents.CATEGORY_APPLICATION)
                            .setAction(ITrackerEvents.ACTION_INSTALL)
                            .build());

        setResult(RESULT_OK);
        finish();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
        if (null != mController)
        {
            mController.selectPosition(position);
        }
        if (null != nextButton)
        {
            if (position == 2)
            {
                nextButton.setText(R.string.begin);
            }
            else
            {
                nextButton.setText(R.string.further);
            }
        }
    }


    @Override
    public void onPageScrollStateChanged(int state)
    {
    }



    public static class _PagerAdapter extends PagerAdapter
    {
        private LayoutInflater inflater;

        public _PagerAdapter(LayoutInflater inflater)
        {
            this.inflater = inflater;
        }

        public void setInflater(LayoutInflater inflater)
        {
            this.inflater = inflater;
        }

        public int getItemPosition(Object object)
        {
            //return POSITION_UNCHANGED;
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(View collection, int position)
        {
            final View v;
            Resources res = inflater.getContext().getResources();
            boolean isLandscape = res.getBoolean(R.bool.is_landscape);

            //Configuration configuration = res.getConfiguration();
            //int orientation = configuration.orientation;
            //if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation)
            //if (metrics.heightPixels < metrics.widthPixels)

            switch (position)
            {
                case 0:
                {
                    v = inflater.inflate(R.layout.intro1, null);

                    if (isLandscape)
                    {
                        v.findViewById(R.id.splash).setVisibility(View.GONE);
                    }

                    TextView tv = (TextView) v.findViewById(android.R.id.text2);
                    final Context context = inflater.getContext();
                    tv.setText(context.getString(R.string.intro1_text, context.getString(R.string.intro_name)));
                }
                break;
                //case 1:
                //    v = inflater.inflate(isLandscape ? R.layout.intro2_land :R.layout.intro2, null);
                //    break;
                default:
                    v = inflater.inflate(isLandscape ? R.layout.intro3_land : R.layout.intro3, null);
            }
            ((ViewPager) collection).addView(v, 0);
            return v;
        }

        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }

        /*
        private List<View> pages;

        public _PagerAdapter(List<View> pages)
        {
            this.pages = pages;
        }

        public void setPages(List<View> pages)
        {
            this.pages = pages;
            notifyDataSetChanged();
        }


        @Override
        public Object instantiateItem(View collection, int position)
        {
            View v = pages.get(position);
            ((ViewPager) collection).addView(v, 0);
            return v;
        }

        @Override
        public int getCount()
        {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view.equals(object);
        }

        */

        @Override
        public void destroyItem(View collection, int position, Object view)
        {
            ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view.equals(object);
        }


    }
}


//mProgressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0xFFFFFF));
