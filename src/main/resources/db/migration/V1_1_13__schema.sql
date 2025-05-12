IF not EXISTS(SELECT 1 FROM sys.columns
          WHERE (name = 'parent_tree')
          AND Object_ID = Object_ID(N'agents'))
BEGIN
alter table agents add parent_tree varchar(max) null
END;
