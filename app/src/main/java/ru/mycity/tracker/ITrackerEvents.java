package ru.mycity.tracker;

public interface ITrackerEvents
{
    String CATEGORY_APPLICATION = "app";
    String CATEGORY_LIST = "list";
    String CATEGORY_PAGES = "pages";
    String CATEGORY_MAIN = "main";
    String CATEGORY_FORMS = "forms";
    String CATEGORY_MAPS = "maps";
    String CATEGORY_MENU = "menu";
    String CATEGORY_BUTTONS = "buttons";
    String CATEGORY_PUSH = "push";
    String CATEGORY_SECTION = "section";

    String ACTION_INSTALL = "install";
    String ACTION_START = "start";
    String ACTION_OPEN = "open";
    String ACTION_SEND = "send";
    String ACTION_CLICK = "click";
    String ACTION_SUBSCRIBE = "subscribe";
    String ACTION_SEARCH = "search";
    String ACTION_CALL = "call";
    String ACTION_SITE_CLICK = "site-click";
    String ACTION_ORGANIZATION_CLICK = "organization-click";

    String LABEL_ACTION_SECTION = "section";
    String LABEL_ACTION_PAGE = "page";
    String LABEL_ACTION_CALL = "call";
    String LABEL_ACTION_OPEN_LINK = "open-link";
    String LABEL_ACTION_OPEN_ORG_LINK = "open-organization-link";
    String LABEL_ACTION_MAP = "map";
    String LABEL_ACTION_BUTTON = "button";

    String LABEL_TARGET_CATEGORY = "category";
    String LABEL_TARGET_CATEGORIES = "categories";
    String LABEL_TARGET_ORGANIZATION = "organization";
    String LABEL_TARGET_ADD_ORGANIZATION = "add_organization";
    String LABEL_TARGET_EVENT = "event";
    String LABEL_TARGET_EVENTS = "events";
    String LABEL_TARGET_ADD_EVENT = "add_event";
    String LABEL_TARGET_MAIN = "main";
    String LABEL_TARGET_NEWS = "news";
    String LABEL_TARGET_ACTION = "action";
    String LABEL_TARGET_ACTIONS = "actions";
    String LABEL_TARGET_ABOUT = "about";

    String LABEL_PARAM_ENTITY_ID = "entity_id";
    String LABEL_PARAM_SEARCH_CONTEXT = "search_context";
    String LABEL_PARAM_SEARCH_QUERY = "search_query";
    String LABEL_PARAM_TITLE = "title";
    String LABEL_PARAM_NAME = "name";

    //https://docs.google.com/spreadsheets/d/1rmQZvb3pcIZ6XSSJAj1y3b67iwl9W80WUc_rV3kebA4/edit?usp=sharing
}

