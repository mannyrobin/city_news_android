package ru.mycity.data;

import java.util.Comparator;


public class NewsComparator implements Comparator<News>
{
    //Compare by promoted desc and date asc
    @Override
    public int compare(News lhs, News rhs)
    {
        int i = rhs.promoted - lhs.promoted;

        if (0 == i)
        {
            long o1 = lhs.date;
            long o2 = rhs.date;
            return (o1 < o2) ? 1 : (o1 == o2) ? 0 : -1;
        }
        return i;
    }
}
