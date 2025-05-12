IF NOT EXISTS (SELECT 1 FROM sys.sysobjects where name='user_transaction_interval' and xtype = 'U')
create table user_transaction_interval
(
    id             int identity
        constraint PK__user_transaction_interval
        primary key,
    user_player_id int   not null,
    operation_type varchar(50) not null,
    user_type varchar(10) not null,
    operation_date datetime2   not null
)

IF NOT EXISTS (
    SELECT name
    FROM sys.indexes
    WHERE name = 'index_user_transaction_interval_user_id'
    AND object_id = OBJECT_ID('user_transaction_interval')
)
CREATE INDEX index_user_transaction_interval_user_id
    ON user_transaction_interval (user_player_id)
