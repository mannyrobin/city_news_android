package ru.mycity.tracker;

import com.google.android.gms.analytics.HitBuilders;


/**
 * Created by Serge Rumyantsev on 15.03.2016.
 */
public class TrackerEventHelper
{
    public static String makeLabel(String labelAction, String labelTarget)
    {
        return labelAction + '-' + labelTarget;
    }

    public static String makeLabelParameter(String name, String value)
    {
        return ((null == value) ? "" : (name + '=' + value));
    }

    public static String appendLabelParameter(String head, String tail)
    {
        StringBuilder sb = new StringBuilder();

        if (null != head && 0 != head.length())
            sb.append(head);

        if (0 != tail.length())
        {
            if (0 != sb.length())
                sb.append(',');
            sb.append(tail);
        }

        return sb.toString();
    }

    public static String appendLabelParameter(String head, String name, String value)
    {
        return appendLabelParameter(head, makeLabelParameter(name, value));
    }

    public static void sendEventStatistics(TrackerAdapter tracker, String category, String action, String label)
    {
        if (null == tracker)
            return;

        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label);
        tracker.send(builder.build());
    }

    public static void sendEventStatistics(TrackerAdapter tracker, String category, String action, String label, long value)
    {
        if (null == tracker)
            return;

        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value);
        tracker.send(builder.build());
    }

    public static void sendEventStatistics(TrackerEvent event)
    {
        if (null != event.value)
        {
            sendEventStatistics(event.tracker, event.category, event.action, event.getLabel(), event.value);
        }
        else
        {
            sendEventStatistics(event.tracker, event.category, event.action, event.getLabel());
        }
    }
}
