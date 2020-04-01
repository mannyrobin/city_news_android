package ru.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
/*
 * import java.util.Calendar;
    public static Date addMonthsToDate(Date date, int months, int d) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        return cal.getTime();
    }
*/

    private static SimpleDateFormat dateFormatterForCharts = new SimpleDateFormat("dd.MM.yyyy");

    public static final long MILLIS_PER_SECOND = 1000;
    /**
     * Number of milliseconds in a standard minute.
     *
     * @since 2.1
     */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    /**
     * Number of milliseconds in a standard hour.
     *
     * @since 2.1
     */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    /**
     * Number of milliseconds in a standard day.
     *
     * @since 2.1
     */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    /**
     * Adds the given number of months to a date.
     *
     * @param date   the date
     * @param months number of months
     */
    @SuppressWarnings("deprecation")
    public static void addMonthsToDate(Date date, int months)
    {
        if (months != 0)
        {
            int month = date.getMonth();
            int year = date.getYear();

            int resultMonthCount = year * 12 + month + months;
            int resultYear = resultMonthCount / 12;
            int resultMonth = resultMonthCount - resultYear * 12;

            date.setMonth(resultMonth);
            date.setYear(resultYear);
        }
    }

    /**
     * Adds the given number of days to a date.
     *
     * @param date the date
     * @param days number of days
     */
    @SuppressWarnings("deprecation")
    public static void addDaysToDate(Date date, int days)
    {
        date.setDate(date.getDate() + days);
    }

    public static boolean isDateInYesterday(Date date)
    {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.add(java.util.Calendar.DAY_OF_YEAR, -1);

        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(date);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDateInToday(Date date)
    {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(date);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameDay(Date date1, Date date2)
    {

        // Strip out the time part of each date.
        final long julianDayNumber1 = date1.getTime() / MILLIS_PER_DAY;
        final long julianDayNumber2 = date2.getTime() / MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }

    public static String formatDateForChartRange(final Date date)
    {
        return dateFormatterForCharts.format(date);
    }
    
    public static  Date convertToGmt(Date date)
    {
	TimeZone tz = TimeZone.getDefault();
	Date ret = new Date( date.getTime() - tz.getRawOffset() );

	// if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
	    if ( tz.inDaylightTime( ret )){
	        Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

	        // check to make sure we have not crossed back into standard time
	        // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
	        if ( tz.inDaylightTime( dstDate )){
	            ret = dstDate;
	        }
	     }
	     return ret;
}
    

//
//  /**
//   * Copies a date.
//   * 
//   * @param date the date
//   * @return the copy
//   */
//  public static Date copyDate(Date date) {
//    if (date == null) {
//      return null;
//    }
//    Date newDate = new Date();
//    newDate.setTime(date.getTime());
//    return newDate;
//  }
//
//  /**
//   * Returns the number of days between the two dates. Time is ignored.
//   * 
//   * @param start starting date
//   * @param finish ending date
//   * @return the different
//   */
//  public static int getDaysBetween(Date start, Date finish) {
//    if (hasTime(start)) {
//      start = copyDate(start);
//      resetTime(start);
//    }
//
//    if (hasTime(finish)) {
//      finish = copyDate(finish);
//      resetTime(finish);
//    }
//
//    long aTime = start.getTime();
//    long bTime = finish.getTime();
//
//    long adjust = 60 * 60 * 1000;
//    adjust = (bTime > aTime) ? adjust : -adjust;
//
//    return (int) ((bTime - aTime + adjust) / (24 * 60 * 60 * 1000));
//  }
//
//  /**
//   * Returns the day of the week on which week starts in the current locale. The
//   * range between 0 for Sunday and 6 for Saturday.
//   * 
//   * @return the day of the week
//   */
//  public static int getStartingDayOfWeek() {
//    return Integer.parseInt(LocaleInfo.getCurrentLocale().getDateTimeConstants().firstDayOfTheWeek()) - 1;
//  }
//
//  /**
//   * Is a day in the week a weekend?
//   * 
//   * @param dayOfWeek day of week
//   * @return is the day of week a weekend?
//   */
//  public static boolean isWeekend(int dayOfWeek) {
//    return dayOfWeek == (Integer.parseInt(LocaleInfo.getCurrentLocale().getDateTimeConstants().weekendRange()[0]) - 1) || dayOfWeek == (Integer.parseInt(LocaleInfo.getCurrentLocale().getDateTimeConstants().weekendRange()[1]) - 1);
//  }
//
//  /**
//   * Resets the date to have no time modifiers.
//   * 
//   * @param date the date
//   */
//  public static void resetTime(Date date) {
//    long msec = date.getTime();
//    msec = (msec / 1000) * 1000;
//    date.setTime(msec);
//
//    date.setHours(0);
//    date.setMinutes(0);
//    date.setSeconds(0);
//  }
//
//  /**
//   * Sets a date object to be at the beginning of the month and no time
//   * specified.
//   * 
//   * @param date the date
//   */
//  public static void setToFirstDayOfMonth(Date date) {
//    resetTime(date);
//    date.setDate(1);
//  }
//
//  private static boolean hasTime(Date start) {
//    return start.getHours() != 0 || start.getMinutes() != 0
//        || start.getSeconds() != 0;
//  }
}


