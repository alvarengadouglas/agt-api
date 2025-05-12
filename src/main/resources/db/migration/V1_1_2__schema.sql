alter table agents
    add balance bigint default 0 not null;

create table agent_transaction
(
    id             int identity
        constraint PK__agent_transaction
            primary key,
    source_user_id      int         not null
        constraint agent_transaction_source_user_fk
            references users,
    target_user_id      int         not null
        constraint agent_transaction_target_user_fk
            references users,
    amount         bigint      not null,
    operation_type varchar(50) not null,
    operation_date datetime2   not null,
    balance        bigint      not null,
    note           varchar(300)
)
go