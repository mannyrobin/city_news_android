-- upgrade 
-- upgrade 
ALTER TABLE ACTIONS ADD COLUMN DATE_END NUMBER;
ALTER TABLE ACTIONS ADD COLUMN LIFE_TIME_TYPE NUMBER;
DELETE FROM ACTIONS;
