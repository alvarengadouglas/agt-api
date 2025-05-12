IF not EXISTS(SELECT 1 FROM sys.columns
              WHERE (name = 'bonus')
                AND Object_ID = Object_ID(N'user_transaction'))
    BEGIN
        alter table user_transaction add bonus bigint null
    END;

IF not EXISTS(SELECT 1 FROM sys.columns
              WHERE (name = 'bonus')
                AND Object_ID = Object_ID(N'agent_transaction'))
    BEGIN
        alter table agent_transaction add bonus bigint null
    END;