package ru.mycity;

/*
{ "alert": "🎁ДАРИМ🎁 Cертификат на 5000р в GAGARIN BAR 😉 Подробности в Акциях ", "open_id": "1521", "table": "action" }
*/
public interface PushTableNames
{
    String EVENTS_TABLE = "event";
    //private final static int RC_GET_CATEGORIES = 1;
    String ORGANIZATIONS_TABLE = "organization";
    String NEWS = "news";
    String CATEGORY = "category";
    String ACTION_PREFIX = "action"; // action or actions
    String PROMOTION_PREFIX = "promotion"; // "promotion" or "promotions"
    //private boolean wrongTheme = true;
    //String AFISHA = "afisha";
}
