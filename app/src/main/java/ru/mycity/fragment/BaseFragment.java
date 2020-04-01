package ru.mycity.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.mycity.MainActivity;
import ru.mycity.R;
import ru.mycity._AppCompatActivity;

public abstract class BaseFragment extends Fragment
{
    protected final long startTime;
    public boolean isTopLevelFragment;
    private CharSequence prevTitle;
    protected String searchContext;

    private View rootView;
    protected boolean saveView; //Be careful, has a quite big memory footprint

    public BaseFragment()
    {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        prevTitle = ((MainActivity) activity).onOpenFragment(this, getTitle());
    }

    protected abstract CharSequence getTitle();
    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    //http://stackoverflow.com/questions/11353075/how-can-i-maintain-fragment-state-when-added-to-the-back-stack
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /*
        if (saveView && null != rootView)
        {
            saveView = false; // reset by default, set saveView = true on onViewCreated if needed
            return rootView;
        }
        */
        rootView = createView(inflater, container, savedInstanceState);
        return rootView;
    }


    @Override
    public void onDestroyView()
    {
        if (saveView && null != rootView)
        {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent)
                parent.removeView(rootView);
        }
        else
        {
            rootView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach()
    {
        ((MainActivity) getActivity()).onCloseFragment(this, prevTitle);
        super.onDetach();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FragmentActivity activity = getActivity();
        if (null != activity)
        {
            if (activity.onOptionsItemSelected(item))
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void close()
    {
        FragmentActivity activity = getActivity();
        if (null != activity)
        {
            //activity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            activity.onBackPressed();
        }
    }


    /*
    protected void checkOffline()
    {
        if (false == NetworkUtils.isNetworkConnected(getActivity(), true))
        {
            final Main main = (Main) getActivity();
            if (null != main)
                main.setOfflineMode();
        }
    }*/

    protected void showErrorToast(CharSequence text, boolean longShow)
    {
        _AppCompatActivity activity = (_AppCompatActivity) getActivity();
        if (null != activity)
            activity.showErrorToast(text, longShow);
    }

    protected void showErrorToast(int resId)
    {
        showErrorToast(resId, true);
    }

    protected void showErrorToast(int resId, boolean longShow)
    {
        _AppCompatActivity activity = (_AppCompatActivity) getActivity();
        if (null != activity)
            activity.showErrorToast(resId, longShow);
    }

    protected void showErrorToast(int resId, int errorCode, String desc, boolean longShow)
    {
        showInfoToast(getErrorText(resId, errorCode, desc), longShow);
    }

    protected void showInfoToast(CharSequence text, boolean longShow)
    {
        _AppCompatActivity activity = (_AppCompatActivity) getActivity();
        if (null != activity)
        {
            activity.showInfoToast(text, longShow);
        }
    }

    private CharSequence getErrorText(int resId, int errorCode, String desc)
    {
        StringBuilder sb = new StringBuilder(32);
        sb.append(getText(R.string.error));
        sb.append(':');
        sb.append(errorCode);
        sb.append('.');
        sb.append(' ');
        sb.append(getText(resId));
        if (null != desc && 0 != desc.length())
        {
            sb.append(desc);
        }
        return sb;
    }

    protected void showErrorDialog(int resId, int errorCode, String desc, final DialogInterface.OnClickListener listener)
    {
        CharSequence text = getErrorText(resId, errorCode, desc);
        _AppCompatActivity activity = (_AppCompatActivity) getActivity();
        if (null != activity)
            activity.showErrorDialog(text, listener);
    }

    protected void showInfoToast(int resId, boolean longShow)
    {
        _AppCompatActivity activity = (_AppCompatActivity) getActivity();
        if (null != activity)
            activity.showInfoToast(resId, longShow);
    }

    public boolean openDetailFragment(Fragment fragment, String tag)
    {
        MainActivity activity = (MainActivity) getActivity();
        return (null != activity) ?  activity.openDetailFragment(fragment, tag) : false;
    }

    public  boolean openMasterFragment(Fragment fragment, String tag)
    {
        MainActivity activity = (MainActivity) getActivity();
        return (null != activity) ?  activity.openMasterFragment(fragment, tag) : false;
    }

    protected void requestPermissions(String fragmentName, String[] permissions, int requestCode)
    {
        MainActivity activity = (MainActivity) getActivity();
        if (null != activity)
        {
            activity.setRequestPermissionsFragmentName(fragmentName);
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }
}