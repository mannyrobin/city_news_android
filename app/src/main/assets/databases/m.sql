-- JUST DRAFT 

CREATE TABLE IF NOT EXISTS RATINGS_TOP( 
    _id INTEGER PRIMARY KEY AUTOINCREMENT, 
    ID           INTEGER NOT NULL, 
    RATING       INTEGER NOT NULL, 
    NOTE         TEXT, 
    ENTITY_ID    INTEGER NOT NULL, 
    ENTITY_TYPE  INTEGER,
    USER_ID      TEXT, 
    IMAGE        TEXT,
    CREATED_AT   NUMBER NOT NULL, 
    ENTITY_TITLE TEXT,  
    AUTHOR_NAME  TEXT
);

CREATE TABLE IF NOT EXISTS RATINGS( 
    _id INTEGER PRIMARY KEY AUTOINCREMENT, 
    ID           INTEGER NOT NULL, 
    RATING       INTEGER NOT NULL, 
    NOTE         TEXT, 
    ENTITY_ID    INTEGER NOT NULL, 
    ENTITY_TYPE  INTEGER,
    USER_ID      TEXT, 
    IMAGE        TEXT,
    CREATED_AT   NUMBER NOT NULL, 
    AUTHOR_NAME  TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS RATINGS_UNIQUE1 ON RATINGS(ID);



CREATE TABLE IF NOT EXISTS ORGANIZATIONS_BACKGROUND_COLORS( 
    _id INTEGER PRIMARY KEY AUTOINCREMENT, 
    TITLE        TEXT, 
    CODE         TEXT, 
    COLOR_START  TEXT, 
    COLOR_END    TEXT, 
    CREATED_AT   INTEGER, 
    IS_DELETED   INTEGER,
    UPDATED_AT   INTEGER
);


ALTER TABLE ORGANIZATIONS ADD COLUMN VOTES_RATING TEXT;
ALTER TABLE ORGANIZATIONS ADD COLUMN VOTES_COUNT NUMBER;
--ALTER TABLE ORGANIZATIONS ADD COLUMN SOURCE_NAME TEXT;
--ALTER TABLE ORGANIZATIONS ADD COLUMN SOURCE_ID TEXT;
ALTER TABLE ORGANIZATIONS ADD COLUMN DISCOUNT_TITLE TEXT;
ALTER TABLE ORGANIZATIONS ADD COLUMN HAS_DISCOUNT NUMBER;

-- ??
ALTER TABLE ORGANIZATIONS ADD COLUMN PICTURES TEXT;

ALTER TABLE ORGANIZATIONS ADD COLUMN DISCOUNT_BODY TEXT;
ALTER TABLE ORGANIZATIONS ADD COLUMN BG_COLOR_ID NUMBER;

   