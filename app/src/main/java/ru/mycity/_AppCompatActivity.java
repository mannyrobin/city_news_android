package ru.mycity;

import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;

public class _AppCompatActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
         */

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));
    }

    private final static int INFO_TITLE_COLOR = 0xFF036cb5;
    
    private void showSnackBar(CharSequence text, boolean isLong, int bkColor, int textColor)
    {
        try
        {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, 
                    isLong ? Snackbar.LENGTH_LONG: Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(bkColor);
            snackbar.setActionTextColor(textColor); 
            snackbar.show();
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(this), e, false);
        }
    }
    
    /*
    private void showToast(CharSequence text, boolean bLong)
    {
        if (!isFinishing())
        {
            try
            {
                Toast.makeText(this, text, bLong ? Toast.LENGTH_LONG: Toast.LENGTH_SHORT).show();
            }
            catch (Throwable e)
            {
            }            
        }        
    }*/

    public void showErrorToast(int resId, boolean longShow)
    {
        showSnackBar(getText(resId), longShow, Color.RED, Color.WHITE);
    }
    
    public void showErrorToast(CharSequence text, boolean longShow)
    {
        showSnackBar(text, longShow, Color.RED, Color.WHITE);
    }
    
    public void showInfoToast(int resId, boolean longShow)
    {
        showSnackBar(getText(resId), longShow, INFO_TITLE_COLOR, Color.WHITE);
    }
    
    public void showInfoToast(CharSequence text, boolean longShow)
    {
        showSnackBar(text, longShow, INFO_TITLE_COLOR, Color.WHITE);
    }
    
    public void showErrorDialog(CharSequence message, final OnClickListener listener)
    {
        DialogHelper.showError(this, message, listener);
    }
}
