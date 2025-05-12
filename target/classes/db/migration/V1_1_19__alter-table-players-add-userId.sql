IF not EXISTS(SELECT 1 FROM sys.columns
              WHERE (name = 'user_id')
                AND Object_ID = Object_ID(N'players'))
BEGIN
alter table players add user_id INT NULL
END;