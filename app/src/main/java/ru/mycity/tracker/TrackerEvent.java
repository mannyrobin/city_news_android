package ru.mycity.tracker;


/**
 * Created by Sergey Rumyantsev on 07.03.2016.
 */
public class TrackerEvent
{
    public TrackerEvent(TrackerAdapter tracker, String category, String action)
    {
        this.tracker = tracker;
        this.category = category;
        this.action = action;
    }

    public TrackerEvent(TrackerAdapter tracker, String category, String action, String label)
    {
        this.tracker = tracker;
        this.category = category;
        this.action = action;
        this.labelParameters = label;
    }

    public TrackerEvent(TrackerAdapter tracker, String category, String action, String labelAction, String labelTarget)
    {
        this.tracker = tracker;
        this.category = category;
        this.action = action;
        this.labelAction = labelAction;
        this.labelTarget = labelTarget;
    }

    public TrackerEvent(TrackerAdapter tracker, String category, String action, String labelAction, String labelTarget, String labelParameters)
    {
        this.tracker = tracker;
        this.category = category;
        this.action = action;
        this.labelAction = labelAction;
        this.labelTarget = labelTarget;
        this.labelParameters = labelParameters;
    }

    public TrackerEvent(TrackerAdapter tracker, String category, String action, String labelAction, String labelTarget, String labelParameters, long value)
    {
        this.tracker = tracker;
        this.category = category;
        this.action = action;
        this.labelAction = labelAction;
        this.labelTarget = labelTarget;
        this.labelParameters = labelParameters;
        this.value = value;
    }

    public String getLabel()
    {
        StringBuilder sb = new StringBuilder();

        if (null != labelAction)
        {
            if (0 != sb.length())
                sb.append('-');
            sb.append(labelAction);
        }

        if (null != labelTarget)
        {
            if (0 != sb.length())
                sb.append('-');
            sb.append(labelTarget);
        }

        if (null != labelParameters)
        {
            if (0 != sb.length())
                sb.append(',');
            sb.append(labelParameters);
        }

        return sb.toString();
    }

    public TrackerAdapter tracker;
    public String         category;
    public String         action;

    public String         labelAction;
    public String         labelTarget;
    public String         labelParameters;

    public Long           value;
}