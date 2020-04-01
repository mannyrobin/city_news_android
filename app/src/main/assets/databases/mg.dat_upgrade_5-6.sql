-- upgrade 
-- create tables        
		CREATE TABLE IF NOT EXISTS OPTIONS( 
                _id INTEGER PRIMARY KEY AUTOINCREMENT, 
                ID INTEGER NOT NULL, 
                KEY TEXT NOT NULL, 
                TYPE TEXT, 
                VALUE TEXT, 
                DELETED INTEGER, 
                UPDATED_AT INTEGER 
                );

        CREATE UNIQUE INDEX IF NOT EXISTS OPTIONS_UNIQUE ON OPTIONS(ID);
        CREATE UNIQUE INDEX IF NOT EXISTS OPTIONS_UNIQUE_KEY ON OPTIONS(KEY);
        CREATE TABLE IF NOT EXISTS ABOUT( 
                _id INTEGER PRIMARY KEY AUTOINCREMENT, 
                ID INTEGER, 
                PHONE TEXT, 
                ABOUT_PROJECT TEXT, 
                UPDATED_AT INTEGER 
                );
 
        CREATE TABLE IF NOT EXISTS OWNERS( 
                _id INTEGER PRIMARY KEY AUTOINCREMENT, 
                pic_link TEXT, 
                name TEXT NOT NULL, 
                desc TEXT
                );