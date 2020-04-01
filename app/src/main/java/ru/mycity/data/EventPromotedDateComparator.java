package ru.mycity.data;

import java.util.Comparator;

public class EventPromotedDateComparator implements Comparator<Event>
{
    //Compare by promoted desc and date asc
    @Override
    public int compare(Event lhs, Event rhs)
    {
         int i = rhs.promoted - lhs.promoted;

         if (0 == i)
         {
             long o1 = lhs.date;
             long o2 = rhs.date;
             return (o1 > o2) ? 1 : (o1 == o2) ? 0 : -1;
         }
         return i;
    }
}
