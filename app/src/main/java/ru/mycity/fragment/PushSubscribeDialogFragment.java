package ru.mycity.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import ru.mycity.R;

public class PushSubscribeDialogFragment extends AppCompatDialogFragment implements View.OnClickListener
//public class PushSubscribeDialogFragment extends DialogFragment implements View.OnClickListener
{

    public interface Listener
    {
        void onSkip();
        void onСonfirm();
    }

    private Listener listener;



    @Override
    public void setupDialog(Dialog dialog, int style)
    {
        super.setupDialog(dialog, style);
        ((AppCompatDialog) dialog).supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.push_subsctibe, container, false);
        String title = getString(R.string.push_warning_title, getString(R.string.city_genitive));
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        v.findViewById(android.R.id.button1).setOnClickListener(this);
        v.findViewById(android.R.id.button2).setOnClickListener(this);
        tv.setText(title);
        return v;
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case android.R.id.button1:
                 if (null != listener)
                     listener.onSkip();
                 dismiss();
                 break;

            case android.R.id.button2:
                if (null != listener)
                    listener.onСonfirm();
                dismiss();
                break;

        }
    }

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    }

}
