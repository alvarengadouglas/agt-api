IF not EXISTS(SELECT 1 FROM sys.columns
              WHERE (name = 'permission_unblock')
                AND Object_ID = Object_ID(N'agents'))
BEGIN
ALTER TABLE agents ADD permission_unblock int DEFAULT 0 NULL
END;