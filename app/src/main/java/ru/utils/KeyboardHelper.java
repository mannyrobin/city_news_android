package ru.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class KeyboardHelper
{
    public static void hideSoftKeyboard(View view)
    {
        if (null != view)
        {
            final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != imm)
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                    //InputMethodManager.HIDE_NOT_ALWAYS
                    0
            );
        }               
    }
    
    
    public static void hideSoftKeyboard(Activity activity)
    {
        if (null != activity)
        {
            final View v = activity.getCurrentFocus();
            if (null != v)
                hideSoftKeyboard(v);
        }               
    }
    
    public static void showSoftKeyboard(View v)
    {
        if (null != v)
        {
            InputMethodManager inputMethodManager=(InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }
    
    public static boolean checkDoneKeyEvent(int actionId, KeyEvent event)
    {
        return (    (EditorInfo.IME_ACTION_DONE  == actionId)
                ||
                    (
                      EditorInfo.IME_ACTION_UNSPECIFIED == actionId
                      && null != event
                      && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                      && KeyEvent.ACTION_DOWN   == event.getAction()
                     )
                );
        
    }
    
    public static boolean isHardKeyboardActive(final Context context)
    {  
        return (Configuration.KEYBOARD_NOKEYS != context.getResources().getConfiguration().keyboard);
    }
    
//    private boolean isMyKeyboardEnabled(final Context context)
//    {
//        InputMethodManager imm = (InputMethodManager)context.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
//        
//        List<InputMethodInfo> inputMethods = imm.getEnabledInputMethodList();
//        
//        for(InputMethodInfo inputMethodInfo : inputMethods)
//        {
//            final int scount = inputMethodInfo.getSubtypeCount();
//            
//            for (int i = 0; i < scount; i++) {
//                InputMethodSubtype subtype = inputMethodInfo.getSubtypeAt(i);
//                if (VOICE_IME_SUBTYPE_MODE.equals(subtype.getMode())) {
//                    if (inputMethodInfo.getComponent().getPackageName()
//                            .startsWith(VOICE_IME_PACKAGE_PREFIX)) {
//                        return inputMethodInfo;
//                    }
//                }
//        }
//        return false;
//    }
//    
    
    /*
    public static void showSoftKeyboard(final Context context)
    {
        if (null != context)
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        
                        final InputMethodManager imm = (InputMethodManager) context
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (null != imm)
                        {
                            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }, 100);
        }
    }
    */
}
