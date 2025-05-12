IF not EXISTS(SELECT 1 FROM sys.columns
              WHERE (name = 'transaction_status')
                AND Object_ID = Object_ID(N'user_transaction'))
    BEGIN
        alter table user_transaction add transaction_status varchar(255)
    END;
