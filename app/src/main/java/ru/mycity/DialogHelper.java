package ru.mycity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import ru.mycity.tracker.TrackerExceptionHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.Density;
import ru.utils.KeyboardHelper;

public class DialogHelper
{
        //public final static int info_icon = android.R.drawable.ic_dialog_info;
    //public final static int info_icon = 0;
    public final static int info_icon = R.drawable.ic_info_outline_black_24dp;

    public final static int question_icon = R.drawable.ic_help_outline_black_24dp;

    //public final static int error_icon = 0;
    //public final static int error_icon = android.R.drawable.ic_dialog_alert;
    //public final static int error_icon = R.drawable.ic_error_outline_black_24dp;
    public final static int error_icon = R.drawable.ic_error_black_24dp;
    //public final static int error_icon = R.drawable.ic_error_outline_red_24dp;


    public static void showError(final Context context, CharSequence message, final OnClickListener listener)
    {
        show(context, context.getText(R.string.app_name), message, error_icon,
                context.getText(android.R.string.ok), null, true, listener);
    }
    
    public static void showQuestion(final Context context, CharSequence message, final OnClickListener listener)
    {
        show(context, context.getText(R.string.app_name), message, info_icon,
                context.getText(R.string.yes), context.getText(R.string.no), false, listener);
    }

    public static void show(final Context context, CharSequence message, int icon, CharSequence positive, CharSequence negative, boolean cancelable, final OnClickListener listener)
    {
        show(context, context.getText(R.string.app_name), message, icon, positive, negative, cancelable, listener);
    }

    
    public static void show(final Context context, CharSequence title, CharSequence message, int icon, CharSequence positive, CharSequence negative, boolean cancelable, final OnClickListener listener)
    {
        AlertDialog.Builder builder = createBuilder(context).setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable);

        if (0 != icon)
            builder.setIcon(icon);
        
        if (null != positive)
             builder.setPositiveButton(positive, listener);
         if (null != negative)
             builder.setNegativeButton(negative, listener);
        try
        {
            builder.create().show();
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), e, false);
        }
    }
    
    
    public static void showInput(final Context context, CharSequence title, CharSequence hint, CharSequence positive, CharSequence negative, boolean cancelable, final InputClickListener listener)
    {
      //http://android-pratap.blogspot.ru/2014/12/creating-input-dialog-box-with-xml.html
        
        AlertDialog.Builder builder = createBuilder(context)                
                .setCancelable(cancelable);
        
        final EditText input = new EditText(context);
        //final EditText input = (EditText) LayoutInflater.from(context).inflate(R.layout.search, null);
        if (null != hint)
             input.setHint(hint); 
        
        final float scale = Density.getDensity(context);
        
        input.setMinWidth((int) (scale * 300));
        if (null != title && 0 != title.length())
             builder.setTitle(title);
        if (null != positive)
             builder.setPositiveButton(positive, null);
         if (null != negative)
             builder.setNegativeButton(negative, null);

        
        final AlertDialog dialog = builder.create();
        dialog.setView(input,
                       (int) (24  * scale + 0.5f),
                       (int) ( ((null != title && 0 != title.length()) ? 10: 20)  * scale + 0.5f),
                       (int) (24  * scale + 0.5f),
                       (int) (0   * scale + 0.5f)
                       );
        
        //input.requestFocus();
        try
        {
            dialog.show();
            //input.requestFocus();
            //KeyboardHelper.showSoftKeyboard(input);
     
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
             {
                 @Override
                 public void onClick(View v)
                 {
                     if (null != listener)
                     {
                         if (false == listener.onPositiveClick(input))
                             return;
                         try
                         {
                             KeyboardHelper.hideSoftKeyboard(input);
                         }
                         catch (Throwable w)
                         {
                            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(v), w, false);
                         }
                         try
                         {
                             dialog.dismiss();
                         }
                         catch (Throwable w)
                         {
                            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), w, false);
                         }
                     }
 
                 }
             });            
            
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
             {
                 @Override
                 public void onClick(View v)
                 {
                     try
                     {
                         KeyboardHelper.hideSoftKeyboard(input);
                     }
                     catch (Throwable w)
                     {
                        TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), w, false);
                     }
                     try
                     {
                         dialog.dismiss();
                     }
                     catch (Throwable w)
                     {
                        TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), w, false);
                     }
                     if (null != listener)
                         listener.onNegativeClick();
                 }
             });
            
            input.postDelayed(new Runnable()
                {
                    
                    @Override
                    public void run()
                    {
                        input.requestFocus();
                        KeyboardHelper.showSoftKeyboard(input);                                                
                    }
                }, 50);             
        }
        catch (Throwable e)
        {
            TrackerExceptionHelper.sendExceptionStatistics(TrackerHelper.getTracker(context), e, false);
        }
    }
    
    public static AlertDialog.Builder createBuilder(final Context context)
    {

        // May be change android.app.AlertDialog to appcompat AlertDialog


        /*
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
        {
            //return new AlertDialog.Builder(context, R.style.Theme_App_Dialog_Alert);
            return new AlertDialog.Builder(context);
        }
        else
            return new AlertDialog.Builder(context);
        */
        return new AlertDialog.Builder(context);
            //return new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppCompatAlertDialogStyle));
        //return new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
    }
    
}

