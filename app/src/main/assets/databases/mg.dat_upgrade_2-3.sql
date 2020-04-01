-- upgrade 
delete from news where rowid not in (select max(rowid) from news group by TITLE);