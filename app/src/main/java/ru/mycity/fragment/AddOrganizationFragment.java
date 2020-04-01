package ru.mycity.fragment;

import android.os.Bundle;
import android.view.View;

import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.R;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;

public class AddOrganizationFragment extends AddFormFragment
{
    public static final String NAME = "AddOrganizationFragment";

    @Override
    protected CharSequence getTitle()
    {
        return super.getTitle();
    }

    protected CharSequence getDescriptionLabel()
    {
        return getText(R.string.add_org_title_prompt);
    }

    protected String getFormName()
    {
        return "AddOrganizationForm";
    }

    protected String getFormUrlPart()
    {
        return "organizations";
    }
}