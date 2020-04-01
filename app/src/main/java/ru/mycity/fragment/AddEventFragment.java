package ru.mycity.fragment;

import android.os.Bundle;
import android.view.View;

import ru.mycity.tracker.ITrackerEvents;
import ru.mycity.R;
import ru.mycity.tracker.TrackerEvent;
import ru.mycity.tracker.TrackerEventHelper;
import ru.mycity.tracker.TrackerHelper;


public class AddEventFragment extends AddFormFragment
{
    public static final String NAME = "AddEventFragment";

    @Override
    protected CharSequence getTitle()
    {
        if (null == title)
        {
            title = getText(R.string.menu_item_add_event);
        }
        return title;
    }

    protected CharSequence getDescriptionLabel()
    {
        return getText(R.string.add_event_title_prompt);
    }

    protected String getFormName()
    {
        return "AddEventForm";
    }

    protected String getFormUrlPart()
    {
        return "events";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TrackerEventHelper.sendEventStatistics(new TrackerEvent(TrackerHelper.getTracker(view.getContext()), ITrackerEvents.CATEGORY_SECTION, ITrackerEvents.ACTION_OPEN, ITrackerEvents.LABEL_ACTION_SECTION, ITrackerEvents.LABEL_TARGET_ADD_EVENT, null, System.currentTimeMillis() - startTime));
    }
}
