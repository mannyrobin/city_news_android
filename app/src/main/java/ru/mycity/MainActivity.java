package ru.mycity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

import ru.mycity.data.Action;
import ru.mycity.data.Category;
import ru.mycity.data.Event;
import ru.mycity.data.News;
import ru.mycity.data.Organization;
import ru.mycity.data.PushData;
import ru.mycity.database.DbActionsHelper;
import ru.mycity.database.DbCategoriesHelper;
import ru.mycity.database.DbEventsHelper;
import ru.mycity.database.DbNewsHelper;
import ru.mycity.database.DbOptionsHelper;
import ru.mycity.database.DbOrganizationsHelper;
import ru.mycity.fragment.AboutFragment;
import ru.mycity.fragment.ActionsFragment;
import ru.mycity.fragment.AddEventFragment;
import ru.mycity.fragment.AddOrganizationFragment;
import ru.mycity.fragment.BaseFragment;
import ru.mycity.fragment.CategoriesFragment;
import ru.mycity.fragment.EventFragment;
import ru.mycity.fragment.EventsFragment;
import ru.mycity.fragment.MainFragment;
import ru.mycity.fragment.NewFragment;
import ru.mycity.fragment.NewsFragment;
import ru.mycity.fragment.OrganizationFragment;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerAdapter;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.PermissionsUtils;
import ru.utils.PhoneUtils;

public class MainActivity extends _AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IResultCallback
        //, FragmentManager.OnBackStackChangedListener
{
    private final static int RC_CALL_SPLASH_SCREEN = 1;
    //private final static int RC_RESOLVE_EVENT  = 2;
    private String phone;

    DrawerLayout mDrawerLayout;
    private String defaultTitle;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Handler handler;
    private String requestPermissionsFragmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));

        setContentView(R.layout.splash);

        /*
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        if (null != progressBar)
        {
            //progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, getResources().getColor(R.color.primary_dark)));
            int color = 0xFF00FF00;
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } */

        //moveTaskToBack(true);

        //findViewById(R.id.drawer_layout).setBackgroundColor(getResources().getColor(R.color.primary));
        this.defaultTitle = getString(R.string.app_name);

        //init();

        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setExitTransition(null);

        showSplashScreen();
        /*
        findViewById(android.R.id.icon).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                showSplashScreen();
            }
        },1);
        */
    }

    void showSplashScreen()
    {
        Intent source = getIntent();
        final   Intent intent;
        SharedPreferences prefs = Config.getSharedPreferences(this);
        if (prefs.getBoolean(Config.intro_shown, false))
            intent = new Intent(getApplicationContext(), SplashScreen.class);
        else
            intent = new Intent(getApplicationContext(), IntroActivity.class);
        if (null != source)
        {
            intent.replaceExtras(source);
            source.replaceExtras(new Bundle());
        }
        //if (intent.hasExtra(PushData.INTENT_KEY))
        //{
        //intent.putExtras(source.getExtras());
        //source.removeExtra(PushData.INTENT_KEY);
        //source.putE   xtras(new Bundle());
        //}
        startActivityForResult(intent, RC_CALL_SPLASH_SCREEN);

        initScreen();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    /*
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (null != mDrawerToggle) mDrawerToggle.syncState();
        handleItem(R.id.menu_item_main);
    }*/
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        if (null != mDrawerToggle)
        {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        if (null != item)
        {
            item.setVisible(false);
            //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
            //searchView.setOnQueryTextListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }


    /*
    <string name="push_warning_title">Получайте уведомления о важных событиях %1$s!</string>
    <string name="push_warning_text">Разрешите уведомления, будьте в курсе новостей, событий и акций, происходящих в нашем городе!</string>

    <string name="push_warning_allow">Разрешить</string>
    <string name="push_warning_skip">Пропустить</string>

     */

    /*
    private boolean handlePush(PushData p)
    {
        PushData pushData = p;

        //if (null == pushData)
        //{
        //    pushData = new PushData();
        //    pushData.table="afisha";
        //    pushData.id = "60";
        //}

        if (null == pushData)
            return false;

        String table = pushData.table;
        if (null != table)
        {
            String id = pushData.id;
            if (null == id)
                return false;
            if (0 == id.length())
                return false;
            final int len = table.length();
            String e = "afisha";
            if (len == e.length() && e.regionMatches(0, table, 0, len))
            {
                _Application application = (_Application) getApplicationContext();
                application.getAsyncTaskExecutor().execute(
                        new ResolveEventTask(application, rootData, pushData, RC_RESOLVE_EVENT, this));
                return true;
            }
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings)
        //{
        //    return true;
        //}
        //return super.onOptionsItemSelected(item);

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (null != mDrawerToggle && mDrawerToggle.onOptionsItemSelected(item)) { return true; }

        // Handle action buttons
        switch (id)
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            //case R.id.action_settings:
            //    break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle(CharSequence title)
    {
        if (null != title)
        {
            mTitle = title;
            setTitleText(title);
        }
    }


    private void setTitleText(CharSequence title)
    {
        ActionBar appbar = getSupportActionBar();
        if (null != appbar)
        {
            if (null == title)
                title =  getText(R.string.app_name);
            appbar.setTitle(title);
        }
    }


    /*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (!viewIsAtHome) { //if the current view is not the News fragment
            displayView(R.id.nav_news); //display the News fragment
        } else {
            moveTaskToBack(true);  //If view is in News fragment, exit application
        }
    } */

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        CharSequence title = (R.id.menu_item_main == id) ? defaultTitle : item.getTitle();
        //setTitle(title);
        if (null != mDrawerLayout)
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        onMenuItemClick(item, id, title);
        return true;
    }

    private void onMenuItemClick(MenuItem item, int menuItemId, CharSequence title)
    {
        switch (menuItemId)
        {
            //Справочник (categories)
            case R.id.menu_item_categories:
                //Новости (news)
            case R.id.menu_item_news:
                //Aфиша (events)
            case R.id.menu_item_placard:
            case R.id.menu_item_actions:
                handleMenuItem(menuItemId, title, true);
                trackMenuItem(item);
                //onSelectCategories();
                break;

            //Главная
            case R.id.menu_item_main:
            case R.id.menu_item_add_organization:
            case R.id.menu_item_add_event:
            case R.id.menu_item_about:
            case R.id.menu_item_social_login:
                handleMenuItem(menuItemId, title, true);
                //onSelectCategories();
                break;
        }
    }

    public boolean handleMenuItem(int id, CharSequence title, boolean setTopLevel)
    {
        //String title = null; //getString(R.string.app_name);
        String name = getItemTypeName(id);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        boolean bExists = (null != fragmentManager.findFragmentByTag(name));

        if (bExists)
        {
            if (R.id.menu_item_main == id)
            {
                int count = fragmentManager.getBackStackEntryCount();
                if (0 == count)
                {
                    return false;
                }
                else
                {
                    //fragmentManager.popBackStack();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    displayHomeUp(false);
                    return false;
                }
            }
            //else
            //{
            //    return false;
            //}
        }

        if (fragmentManager.getBackStackEntryCount() > 0)
        //if (R.id.menu_item_main != id)
        {
            //skipToggleHomeButton = true;
            //fragmentManager.popBackStack();

            //fragmentManager.popBackStackImmediate();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        BaseFragment fragment = null;

        _Application application = (_Application) this.getApplication();
        switch (id)
        {
            case R.id.menu_item_main:
            {
                MainFragment f = new MainFragment();
                //MainFragmentOld f = new MainFragmentOld();

                fragment = f;
            }
            //title  = "News";
            break;
            case R.id.menu_item_categories:
            {
                CategoriesFragment f = new CategoriesFragment();
                f.setData(application._rootData.categories, null, title);
                fragment = f;
            }
            break;
            case R.id.menu_item_news:
            {
                NewsFragment f = new NewsFragment();
                f.setData(application._rootData, title);
                fragment = f;
            }
            break;
            case R.id.menu_item_placard:
            {
                EventsFragment f = new EventsFragment();
                f.setData(application._rootData.events, title);
                fragment = f;
            }
            break;
            case R.id.menu_item_actions:
            {
                ActionsFragment f = new ActionsFragment();
                f.setData(application._rootData.actions, title);
                fragment = f;
            }
            break;
            case R.id.menu_item_add_organization:
            {
                AddOrganizationFragment f = new AddOrganizationFragment();
                f.setData(title);
                fragment = f;
            }
            break;
            case R.id.menu_item_add_event:
            {
                AddEventFragment f = new AddEventFragment();
                f.setData(title);
                fragment = f;
            }
            break;

            case R.id.menu_item_about:
            {
                AboutFragment f = new AboutFragment();
                f.setData(title);
                fragment = f;
            }
            break;

        }

        if (fragment != null)
        {
            fragment.isTopLevelFragment = setTopLevel;

            FragmentTransaction transaction =
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, name);

            if (R.id.menu_item_main != id)
            {
                // if (Configuration.ORIENTATION_PORTRAIT == orientation)
                {
                    //skipToggleHomeButton = true;
                    transaction.addToBackStack(null);
                }
            }
            transaction.commitAllowingStateLoss();
            //transaction.commit();
            //showProgressBar(false);
            // clientsCount--;
        }
        else
        {
            // Error
            Log.e(this.getClass().getName(), "Error. Fragment is not created");
            return false;
        }
        // set the toolbar title
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setTitle(title);
        displayHomeUp(false);
        return true;
    }

    /*
    @Override
    public boolean onSupportNavigateUp(){

        // or call onBackPressed()
        return true;
    }*/

    private void trackMenuItem(MenuItem item)
    {
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_MENU, ITrackerEvents.ACTION_OPEN));
    }

    private String getItemTypeName(int itemId)
    {
        switch (itemId)
        {
            case R.id.menu_item_main:
                return MainFragment.NAME;

            case R.id.menu_item_categories:
                return CategoriesFragment.NAME;

            case R.id.menu_item_news:
                return NewsFragment.NAME;

            case R.id.menu_item_placard:
                return EventsFragment.NAME;

            case R.id.menu_item_actions:
                return ActionsFragment.NAME;

            case R.id.menu_item_add_organization:
                return AddOrganizationFragment.NAME;

            case R.id.menu_item_add_event:
                return AddEventFragment.NAME;

            case R.id.menu_item_about:
                return AboutFragment.NAME;

        }
        return null;
    }

    private void displayHomeUp(boolean canBack)
    {
        if (null != mDrawerToggle)
        {
            mDrawerToggle.setDrawerIndicatorEnabled(!canBack);
            if (canBack)
            {
                mDrawerToggle.setHomeAsUpIndicator(0);
            }
        }
    }

    //private void onSelectCategories()
    //{
    //    new GetCategoriesTask(RC_GET_CATEGORIES, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    //}

    @Override
    public void onFinished(int rc, Result result)
    {
        /*
        switch (rc)
        {
            case RC_RESOLVE_EVENT :
                 if (false == onGetEvent((ResolveEventTask.Result) result))
                     ;
                     //handleMenuItem(R.id.menu_item_main, null, true);
                 break;
        }
        */
    }

    @Override
    public void onFailed(int rc, Throwable error)
    {
        onFailed(rc, (null != error) ? error.getMessage() : null);
    }

    @Override
    public void onFailed(int rc, String description)
    {
        showErrorToast(R.string.get_data_error, true);
    }

    /*
    public CharSequence getCurrentTitle()
    {
        return mTitle;
    }
    */

    public void onCloseFragment(BaseFragment fragment, CharSequence prevTitle)
    {
        DrawerLayout drawer = mDrawerLayout; //(DrawerLayout) findViewById(R.id.drawer_layout);
        if (null != drawer && drawer.isDrawerVisible(GravityCompat.START))
            drawer.closeDrawers();

        CharSequence title = (null != prevTitle) ? prevTitle : defaultTitle;
        if (null != title)
        {
            setTitle(title);
        }
        //showProgressBar(false);
        shouldDisplayHomeUp(fragment, true);
    }

    //private boolean skipToggleHomeButton;
    public void shouldDisplayHomeUp(BaseFragment _fragment, boolean onClose)
    {
        //   if (skipToggleHomeButton)
        //   {
        //       skipToggleHomeButton = false;
        //       return;
        //   }
        //Enable Up button only  if there are entries in the back stack
        final int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        //TODO
        //boolean canBack = backStackEntryCount > 0;
        boolean canBack;
        if (backStackEntryCount > 0)
        {
            canBack = true;
            if (onClose && 1 == backStackEntryCount)
            {
                Fragment f = null;
                boolean  bFirst = false;

                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

                final int size = fragmentList.size();
                for (int i = size -1; i>=0 ; i--)
                {
                    f = fragmentList.get(i);
                    if (f != null)
                    {
                        if (!bFirst)
                            bFirst = true;
                        else
                            break;
                    }
                }

                BaseFragment fragment = (BaseFragment) f;
                if (null != fragment && fragment.isTopLevelFragment)
                    canBack = false;
            }
        }
        else
        {
            if (null != _fragment && !_fragment.isTopLevelFragment && !onClose)
                canBack = true;
            else
                canBack = false;
        }

        //boolean canBack = backStackEntryCount > 1;

        /*
        if (canBack)
        { // if we have something on the stack (doesn't include the current shown fragment)
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        */
        //getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        displayHomeUp(canBack);
    }

    public CharSequence onOpenFragment(BaseFragment fragment, CharSequence newTitle)
    {
        onOpenFragment(fragment);
        return (null != newTitle) ? setTitleOnNewFragment(newTitle) : defaultTitle;
    }

    protected void onOpenFragment(BaseFragment fragment)
    {
        /*
        if (null != mDrawerToggle)
        {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.setHomeAsUpIndicator(0);
        }
        */
        shouldDisplayHomeUp(fragment, false);
    }

    protected CharSequence setTitleOnNewFragment(CharSequence newTitle)
    {
        if (null != newTitle)
        {
            CharSequence title = mTitle;
            if (null == mTitle)
                mTitle = getText(R.string.app_name);
            setTitle(newTitle);
            return title;
        }
        else
        {
            // mSaveTitle = null;
        }
        // return mSaveTitle;
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case RC_CALL_SPLASH_SCREEN:
            {
                if (resultCode != RESULT_OK)
                {
                    finish();
                    return;
                }
                //initScreen();

                if (screenInitialized)
                    init();
                else
                    getHandler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            init();
                        }
                    }, 900);


                //setContentViewMain();
                //init();

                /*
                if (null == handler)
                    handler = new Handler();

                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        init();
                    }
                });
                */
            }
            break;
        }

    }

    private boolean screenInitialized;

    private void initScreen()
    {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //getWindow().setBackgroundDrawable(null);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentViewMain();
        screenInitialized = true;
    }

    private void setContentViewMain()
    {
        setContentView(R.layout.activity_main);
    }



    /*
    @Override
    public void onBackStackChanged()
    {
        shouldDisplayHomeUp();
    }
    */

    /*
    public Fragment getTopFragment()
    {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment top = null;
        final int size = fragmentList.size();
        for (int i = size -1; i>=0 ; i--) {
            top = (Fragment) fragmentList.get(i);
            if (top != null) {
                return top;
            }
        }
        return top;
    }
    */

    void init()
    {
        //_Application application = (_Application) getApplicationContext();
        //RootData rootData = application._rootData;
        //application.setSettings(rootData.options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            // http://stackoverflow.com/questions/26440279/show-icon-in-actionbar-toolbar-with-appcompat-v7-21
            // abc_ic_menu_moreoverflow_mtrl_alpha

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setDisplayHomeAsUpEnabled(false);
        }

        /*
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                _Application application = (_Application) drawerView.getContext().getApplicationContext();
                TrackerAdapter tracker = application.getTracker();
                if (null != tracker)
                {
                    tracker.send(new HitBuilders.EventBuilder().setCategory(ITrackerEvents.CATEGORY_MENU).setAction(ITrackerEvents.ACTION_OPEN).build());
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //getSupportFragmentManager().addOnBackStackChangedListener(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        alignHeader(navigationView);
        setupNavigationView(navigationView);

        //handleMenuItem(R.id.menu_item_main, null, true);
        try
        {
            MainFragment fragment = new MainFragment();
            fragment.isTopLevelFragment = true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, MainFragment.NAME);
            // transaction.commitAllowingStateLoss();
            transaction.commitAllowingStateLoss();
        }
        catch (Throwable e)
        {
            getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    handleMenuItem(R.id.menu_item_main, null, true);
                }
            });
        }


        /*
        if (!handlePush(rootData.pushData))
        {
            //handleMenuItem(R.id.menu_item_main, null, true);
        }
        else
        {
            rootData.pushData = null;
        }*/


        getHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    showPushWarningIfNeeded();
                }
                catch (Throwable e)
                {
                    TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(getApplication()), e, false);
                }
            }
        }, 1000);
    }

    private void setupNavigationView(NavigationView navigationView)
    {
        final _Application application = (_Application) this.getApplication();
        final ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        final int maxScreenSize;
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT > 12)
        {
            final Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            maxScreenSize = Math.max(size.x, size.y);
        }
        else
        {
            final DisplayMetrics displaymetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displaymetrics);
            maxScreenSize = Math.max(displaymetrics.widthPixels, displaymetrics.heightPixels);
        }

        imageView.setMaxWidth(maxScreenSize);
        final DisplayImageOptions.Builder optionsBuilder = application.generateDefaultImageOptionsBuilder();
        application.getImageLoader().displayImage(
                "drawable://" + R.drawable.cover, imageView, optionsBuilder.build());

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void alignHeader(NavigationView navigationView)
    {
        View navigationViewHeader = navigationView.getHeaderView(0);

        //v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + topOffset, v.getPaddingRight(), v.getPaddingBottom());
        if (null != navigationViewHeader)
        {
            View v = navigationViewHeader.findViewById(R.id.imageView);
            if (null != v)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                int topOffset = ru.mycity.utils.SystemUtils.getStatusBarHeight(this);
                params.topMargin += topOffset;
                v = navigationViewHeader.findViewById(R.id.title);
                if (null != v)
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + topOffset, v.getPaddingRight(), v.getPaddingBottom());
            }
        }
    }

    void showPushWarningIfNeeded()
    {
        _Application application = (_Application) getApplication();
        SharedPreferences prefs = Config.getSharedPreferences(this);
        if (prefs.getBoolean(Config.push_suggested, false))
        {
            return;
        }

        //JSONArray settings = application._rootData.options;

        //Config.putInt(prefs, Config.run_count, 0);
        final int count = prefs.getInt(Config.run_count, 0) + 1;
        Config.putInt(prefs, Config.run_count, count);

        SQLiteDatabase db = application.getDbHelper().getReadableDatabase();

        if (DbOptionsHelper.getBoolean(db, "ask_pushes_at_first", true))
        //if (null == settings || (null != settings && OptionsUtils.getBoolean(settings, "ask_pushes_at_first", true)))
        {
            if (1 == count)
            {
                if (null != mDrawerLayout)
                {
                    try
                    {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    catch (Throwable e)
                    {
                        TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(getApplication()), e, false);
                    }
                }
                showPushWarning();
                return;
            }
        }

        int n = DbOptionsHelper.getInt(db,"ask_pushes_n", 0);
        //int n;
        //if (null != settings && (0 != (n = OptionsUtils.getInt(settings, "ask_pushes_n", 0))))
        if (0 != n)
        {
            if (0 == (count % n))
            {
                showPushWarning();
            }
            return;
        }
        n = DbOptionsHelper.getInt(db,"ask_pushes_probability", 0);
        //if (null != settings && (0 != (n = OptionsUtils.getInt(settings, "ask_pushes_probability", 0))))
        if (0 != n)
        {
            if (0 == (System.currentTimeMillis() % n))
            {
                showPushWarning();
            }
            return;
        }
        //showPushWarning();
    }

    private void showPushWarning()
    {
        DialogHelper.show(MainActivity.this, getText(R.string.push_confirm), getText(R.string.push_warning_text),
                //DialogHelper.question_icon,
                DialogHelper.info_icon,
                getText(R.string.push_confirm_allow), getText(R.string.push_confirm_skip), true, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_NEGATIVE:
                        setPushSuggested();
                        TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_PUSH, ITrackerEvents.ACTION_SUBSCRIBE, TrackerEventHelper.makeLabel("click", "later")));
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        setPushSuggested();
                        pushEnable(true);
                        break;

                }
            }
        });



        /*
        PushSubscribeDialogFragment newFragment = new PushSubscribeDialogFragment();

        newFragment.setListener(new PushSubscribeDialogFragment.Listener()
        {
            @Override
            public void onSkip()
            {
            }

            @Override
            public void onСonfirm()
            {
                DialogHelper.show(MainActivity.this, getText(R.string.push_confirm), getText(R.string.push_warning_text), DialogHelper.question_icon, getText(R.string.push_confirm_allow), getText(R.string.push_confirm_skip), true, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_NEGATIVE:
                                setPushSuggested();
                                TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_PUSH, ITrackerEvents.ACTION_SUBSCRIBE, TrackerEventHelper.makeLabel("click", "later")));
                                break;
                            case DialogInterface.BUTTON_POSITIVE:
                                setPushSuggested();
                                pushEnable(true);
                                break;

                        }
                    }
                });
            }
        });

        //newFragment.show(getSupportFragmentManager(), "push_subscribe");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(newFragment, "push_subscribe");
        ft.commitAllowingStateLoss();
        */

        /*
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();
        */
    }

    /*
    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        //getSupportFragmentManager().popBackStack();
        return true;
    }
*/


    /*
    private  boolean onGetEvent(ResolveEventTask.Result result)
    {
        if (null == result)
            return false;
        Event event = result.event;
        if (null == event)
            return false;

        EventFragment fragment = new EventFragment();
        fragment.setData(event, event.title);
        getSupportFragmentManager().beginTransaction().replace(
                //R.id.content_details,
                R.id.content_frame,
                fragment, EventFragment.NAME)
        .addToBackStack(null).commit();
        return true;
    }*/

    void setPushSuggested()
    {
        Config.putBoolean(Config.getSharedPreferences(this), Config.push_suggested, true);
    }

    void pushEnable(boolean enable)
    {
        Config.putBoolean(Config.getSharedPreferences(this), Config.push_enable, enable);
        //ParsePush.
        //PushService.
        //ParseInstallation installation = ParseInstallation.getCurrentInstallation();


        //installation.
        //PushService.
        /*
        ParseInstallation currentInst = ParseInstallation.getCurrentInstallation();
        currentInst.put("enable", "<true/false>");
        currentInst.saveInBackground();
         */

        if (true == enable)
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_PUSH, ITrackerEvents.ACTION_SUBSCRIBE, TrackerEventHelper.makeLabel("click", "yes")));


        }
    }

    //http://stackoverflow.com/questions/6787071/android-fragment-how-to-save-states-of-views-in-a-fragment-when-another-fragmen
    //http://stackoverflow.com/questions/11111649/savedinstancestate-when-restoring-fragment-from-back-stack
    //http://stackoverflow.com/questions/11353075/how-can-i-maintain-fragment-state-when-added-to-the-back-stack

    @Override
    public void onBackPressed()
    {
        if (null != mDrawerLayout && mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        _Application application = (_Application) getApplicationContext();
        PushData pushData = PushData.extract(application, intent);

        if (null != pushData)
        {
            openItem(pushData);
            //intent.removeExtra(PushData.INTENT_KEY);
            intent.replaceExtras(new Bundle());
        }
    }

    public boolean openDetailFragment(Fragment fragment, String tag)
    {
        return openFragment(this, fragment, tag, R.id.content_details);
    }

    public static boolean openFragment(FragmentActivity activity, Fragment fragment, String tag, int containerViewId)
    {
        if (null == activity)
            return false;
        if (activity.isFinishing())
            return false;
        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerViewId, fragment, tag);
        {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
        return true;
    }

    public boolean openMasterFragment(Fragment fragment, String tag)
    {
        return openFragment(this, fragment, tag, R.id.content_frame);
    }

    public void openItem(PushData pushData)
    {
        long id = 0;

        if (null != pushData.id)
        {
            try
            {
                id = Long.parseLong(pushData.id);
            }
            catch (Throwable e)
            {
                TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(getApplication()), e, false);
            }
        }
        openItem(pushData.table, id, null, null, true);
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_PUSH, ITrackerEvents.ACTION_OPEN, null, null, "push-message-uniq-id=" + id, id));
    }


    private boolean openUrl(String url)
    {
        try
        {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
            startActivity(intent);
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_MAIN, ITrackerEvents.ACTION_SITE_CLICK, ITrackerEvents.LABEL_ACTION_OPEN_LINK, ITrackerEvents.LABEL_TARGET_MAIN, "url=" + url, 0));
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(getApplication()), e, false);
        }
        return false;
    }


    public boolean openItem(String table, final long id, String externalLink, String phone, boolean fromPush)
    {
        if (null != table && 0 != table.length())
        {
            return openTableItem(table, id);
        }

        if (null != externalLink && 0 != externalLink.length())
        {
            return openUrl(externalLink);
        }

        if (null != phone && 0 != phone.length())
        {
            return makePhoneCall(phone, true);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            if (null != requestPermissionsFragmentName)
            {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = (null != fragmentManager) ? fragmentManager.findFragmentByTag(requestPermissionsFragmentName) : null;
                requestPermissionsFragmentName = null;
                if (null != fragment)
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return ;
            }

            switch (requestCode)
            {
                case IPermissionCodes.PERMISSION_CALL_PHONE_RC:
                {
                    if (PermissionsUtils.isAllPermissionsGranted(permissions, grantResults))
                    {
                        makePhoneCall(phone, false);
                    }
                }
                break;
            }
        }
    }

    private boolean makePhoneCall(String phone, boolean checkPermissions)
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if (checkPermissions)
            {
                if (!PermissionsUtils.checkPermission(this, android.Manifest.permission.CALL_PHONE))
                {
                    this.phone = phone;
                    requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, IPermissionCodes.PERMISSION_CALL_PHONE_RC);
                    return true;
                }
            }
        }

        if (true == PhoneUtils.makeCall(this, phone))
        {
            TrackerEventHelper.sendEventStatistics(new TrackerEvent(((_Application) getApplication()).getTracker(), ITrackerEvents.CATEGORY_MAIN, ITrackerEvents.ACTION_CALL, ITrackerEvents.LABEL_ACTION_CALL, ITrackerEvents.LABEL_TARGET_ABOUT, "phone=" + phone));
            return true;
        }
        return false;
    }



    protected boolean openTableItem(String table, long id)
    {
        final int len = table.length();

        //category
        if (len == PushTableNames.CATEGORY.length() && table.regionMatches(0, PushTableNames.CATEGORY, 0, len))
        {
            if (0 == id)
            {
                handleMenuItem(R.id.menu_item_categories, getText(R.string.menu_item_categories), false);
            }
            else
            {
                openCategory(id);
            }
            return true;
        }

        //event, afisha
        if ((len == PushTableNames.EVENTS_TABLE.length() && table.regionMatches(0, PushTableNames.EVENTS_TABLE, 0, len)) //||
            // (len == PushTableNames.AFISHA.length() && table.regionMatches(0, PushTableNames.AFISHA, 0, len))
                )
        {
            if (0 == id)
            {
                handleMenuItem(R.id.menu_item_placard, getText(R.string.menu_item_placard), false);
            }
            else
            {
                openEvent(id);
            }
            return true;
        }


        //if ((len == PushTableNames.ACTIONS.length() && table.regionMatches(0, PushTableNames.ACTIONS, 0, len)) ||
        //    (len == PushTableNames.PROMOTIONS.length() && table.regionMatches(0, PushTableNames.PROMOTIONS, 0, len)))
        if (table.startsWith(PushTableNames.ACTION_PREFIX) ||
                table.startsWith(PushTableNames.PROMOTION_PREFIX))
        {
            if (0 == id)
            {
                openActions();
            }
            else
            {
                openAction(id);
            }
            return true;
        }

        //organization
        if (len == PushTableNames.ORGANIZATIONS_TABLE.length() && table.regionMatches(0, PushTableNames.ORGANIZATIONS_TABLE, 0, len))
        {
            if (0 != id)
                openOrganization(id);
            return true;
        }

        //news
        if (len == PushTableNames.NEWS.length() && table.regionMatches(0, PushTableNames.NEWS, 0, len))
        {
            if (0 == id)
                handleMenuItem(R.id.menu_item_news, getText(R.string.menu_item_news), false);
            else
                openNews(id);
            return true;
        }
        return false;
    }

    private void openActions()
    {
        _Application application = (_Application) this.getApplication();

        ActionsFragment f = new ActionsFragment();
        ArrayList<Action> actions = application._rootData.actions;
        if (null == actions)
        {
            actions = new ArrayList<>();
        }
        f.setData(actions, null);
        openMasterFragment(f, ActionsFragment.NAME);
    }

    void openCategoryById(long categoryId)
    {
        if (this.isFinishing())
            return;
        Category category = null;
        _Application application = (_Application) getApplication();
        if (null == application)
            return;

        List<Category> cats = application._rootData.categories;
        if (null != cats)
        {
            for (Category c : cats)
            {
                if (c.id == categoryId)
                {
                    category = c;
                    break;
                }
            }
        }

        if (null == category)
        {
            category = DbCategoriesHelper.getCategory(application.getDbHelper().getReadableDatabase(), null, categoryId);
        }

        if (null != category)
        {
            ArrayList<Organization> organizations = category.organizations;
            if (null != organizations && organizations.isEmpty())
                ;
            else
            {
                organizations = DbOrganizationsHelper.getOrganizations(application.getDbHelper().getReadableDatabase(), category.id, DbOrganizationsHelper.PAGE_SIZE, 0);
                if (null != organizations)
                {
                    category.organizations = organizations;
                    if (0 == category.childCount)
                    {
                        //category.childCount = organizations.size();
                        category.childCount = (int) DbOrganizationsHelper.getOrganizationsCount(application.getDbHelper().getReadableDatabase(), category.id);
                    }
                }
            }
        }

        if (null != category)
        {
            ArrayList<Category> categories = category.subCategories;
            if (null != categories && !categories.isEmpty())
                ;
            else
            {
                category.subCategories = DbCategoriesHelper.getCategories(application.getDbHelper().getReadableDatabase(), category);
            }
        }

        if (null != category)
            CategoriesFragment.openCategory(category, this);
    }

    //http://findevelop.blogspot.ru/2014/01/handler-android.html
    private void openCategory(final long id)
    {
        getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                openCategoryById(id);
            }
        });
    }

    private void openAction(final long id)
    {
        getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                openEventById(id, true);
            }
        });
    }

    private void openNews(final long id)
    {
        getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                openNewsById(id);
            }
        });
    }

    private void openOrganization(final long id)
    {
        getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                openOrganizationById(id);
            }
        });
    }

    void openNewsById(long id)
    {
        if (this.isFinishing())
            return;

        _Application application = (_Application) getApplication();
        if (null == application)
            return;

        News news = null;

        List<News> newsList = application._rootData.news;
        if (null != newsList)
        {
            for (News n : newsList)
            {
                if (n.id == id)
                {
                    news = n;
                    break;
                }
            }
        }

        if (null == news)
        {
            news = DbNewsHelper.getById(application.getDbHelper().getReadableDatabase(), id);
        }

        if (null != news)
        {
            NewFragment f = new NewFragment();
            f.setData(news, news.title, null);

            //openDetailFragment(f, NewFragment.NAME);
            openMasterFragment(f, NewFragment.NAME);
        }
    }

    private void openEvent(final long id)
    {
        getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                openEventById(id, false);
            }
        });
    }

    void openOrganizationById(long id)
    {
        if (this.isFinishing())
            return;

        _Application application = (_Application) getApplication();
        if (null == application)
            return;
        Organization obj = DbOrganizationsHelper.getOrganizationById(application.getDbHelper().getReadableDatabase(), id);

        if (null != obj)
        {
            Category category = DbCategoriesHelper.getOrganizationCategory(application.getDbHelper().getReadableDatabase(), id);
            if (null != category)
            {
                List<Category> cats = application._rootData.categories;
                if (null != cats)
                {
                    for (Category c : cats)
                    {
                        if (c.id == category.parent_id)
                        {
                            category.parent = c;
                            break;
                        }
                    }
                }
            }

            OrganizationFragment f = new OrganizationFragment();
            f.setData(obj, category, null);

            //openDetailFragment(f, NewFragment.NAME);
            openMasterFragment(f, NewFragment.NAME);

            //            f.sendEventStatistics(
            //                    new TrackerEvent(
            //                            application.getTracker(),
            //                            ITrackerEvents.CATEGORY_PAGES,
            //                            ITrackerEvents.ACTION_ORGANIZATION_CLICK,
            //                            ITrackerEvents.LABEL_ACTION_OPEN_ORG_LINK,
            //                            ITrackerEvents.LABEL_TARGET_EVENT,
            //                            ",entity_id=" + id, id
            //                    )
            //            );
        }

    }

    void openEventById(long id, boolean isAction)
    {
        if (this.isFinishing())
            return;

        _Application application = (_Application) getApplication();
        Event obj = DbEventsHelper.getEvent(application.getDbHelper().getReadableDatabase(), isAction ? DbActionsHelper.TABLE_NAME : DbEventsHelper.TABLE_NAME, id);
        if (null == obj)
        {
            obj = DbEventsHelper.getEvent(application.getDbHelper().getReadableDatabase(), !isAction ? DbActionsHelper.TABLE_NAME : DbEventsHelper.TABLE_NAME, id);
        }

        if (null != obj)
        {
            EventFragment f = new EventFragment();
            f.setData(obj, obj.title, isAction, null);
            //openDetailFragment(f, EventFragment.NAME);
            openMasterFragment(f, EventFragment.NAME);
        }
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setRequestPermissionsFragmentName(String requestPermissionsFragmentName)
    {
        this.requestPermissionsFragmentName = requestPermissionsFragmentName;
    }

    /*
        public void showProgressBar(boolean show)
        {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
            if (null == progressBar)
            {
                View v = mDrawerLayout;//(null != mDrawerLayout) ? mDrawerLayout.getRootView() : null;
                if (null != v && v instanceof ViewGroup)
                {
                    progressBar = (ProgressBar) getLayoutInflater().inflate(R.layout.progress, null);
                    progressBar.setId(R.id.progress);

                    ((ViewGroup) v).addView(progressBar, 0);
                }
                progressBar.bringToFront();
                v.invalidate();
            }

            if (null != progressBar)
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        */
    private Handler getHandler()
    {
        if (null == handler)
            handler = new Handler();
        return handler;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        //outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        //super.onSaveInstanceState(outState);
    }

}
