package ru.mycity.data;

import java.util.Comparator;

public class EventPromotedOnlyComparator implements Comparator<Event>
{
    //Compare by action desc
    @Override
    public int compare(Event lhs, Event rhs)
    {
        return  rhs.promoted - lhs.promoted;
    }
}
