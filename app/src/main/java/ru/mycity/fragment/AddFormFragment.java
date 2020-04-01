package ru.mycity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import ru.mycity.R;
import ru.mycity._Application;
import ru.mycity.tasks.AddFormTask;
import ru.mycity.tasks.IResultCallback;
import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;
import ru.utils.KeyboardHelper;
import ru.utils.NameValueItem;

public abstract class AddFormFragment extends BaseFragment
                                      implements View.OnClickListener,
                                                 IResultCallback
{
    protected CharSequence title;
    private EditText editName, editPhone;
    protected ProgressBar progress;

    public AddFormFragment()
    {
    }

    public void setData(CharSequence title)
    {
        this.title = title;
    }

    @Override
    public void onDetach()
    {
        KeyboardHelper.hideSoftKeyboard(getActivity());
        super.onDetach();
    }

    @Override
    protected CharSequence getTitle()
    {
        return title;
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.add_form, container, false);
        progress = (ProgressBar) rootView.findViewById(android.R.id.progress);
        TextView tv = (TextView) rootView.findViewById(android.R.id.text1);
        CharSequence text = getDescriptionLabel();
        if (null != tv && null != text)
        {
            tv.setText(text);
        }
        rootView.findViewById(android.R.id.button1).setOnClickListener(this);
        editName  = (EditText) rootView.findViewById(R.id.name);
        editPhone = (EditText) rootView.findViewById(R.id.phone);
        return rootView;
    }

    abstract protected CharSequence getDescriptionLabel();

    @Override
    public void onClick(View v)
    {
        if (android.R.id.button1 == v.getId())
        {
            apply();
        }
    }

    private void apply()
    {
        Editable text = editName.getText();
        if (0 == text.length())
        {
            KeyboardHelper.hideSoftKeyboard(editName);
            showErrorToast(R.string.s3);
            return;
        }
        String name = text.toString();

        text = editPhone.getText();
        if (0 == text.length())
        {
            KeyboardHelper.hideSoftKeyboard(editPhone);
            showErrorToast(R.string.s4);
            return;
        }
        String phone = text.toString();
        onApply(name, phone);
    }

    protected abstract String getFormName();
    protected abstract String getFormUrlPart();

    protected void onApply(String name, String phone)
    {
        Activity activity = getActivity();
        if (null == activity)
            return;
        if (null != progress)
            progress.setVisibility(View.VISIBLE);
        _Application app =  (_Application) activity.getApplicationContext();
        KeyboardHelper.hideSoftKeyboard(activity);
        app.getAsyncTaskExecutor().execute(new AddFormTask(app, getFormUrlPart(),
                new NameValueItem[]
                        {
                           new NameValueItem("name", name),
                           new NameValueItem("phone", phone)
                        }, 0, this));

    }


    @Override
    public void onFinished(int rc, Result result)
    {
        hideProgress();
        onGetData((AddFormTask.Result) result);
        close();
    }

    private void hideProgress()
    {
        if (null != progress)
            progress.setVisibility(View.GONE);
    }

    @Override
    public void onFailed(int rc, Throwable error)
    {
        hideProgress();
        showErrorToast(R.string.s7, false);
        close();
    }

    @Override
    public void onFailed(int rc, String description)
    {
        hideProgress();
        showErrorToast(R.string.s7, false);
        close();
    }


    private void onGetData(AddFormTask.Result result)
    {
        if (null != result && result.bSuccess)
        {
            JSONObject obj = result.obj;
            if (null != obj)
            {
                int code = obj.optInt("code");
                if (0 == code)
                {
                    TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(this.getContext()), ITrackerEvents.CATEGORY_FORMS, ITrackerEvents.ACTION_SEND, null, null, getFormName()));
                    String message = obj.optString("message");
                    if (null != message && 0 != message.length())
                        showInfoToast(message, true);
                    else
                        showInfoToast(R.string.s9, true);

                    return;
                }
            }
        }
        showErrorToast(R.string.s7, false);
    }


}
