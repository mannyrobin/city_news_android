package ru.mycity;

import android.widget.EditText;


public interface InputClickListener
{
    public void onNegativeClick();
    public boolean onPositiveClick(EditText input);
}
