IF NOT EXISTS (SELECT 1 FROM sys.sysobjects where name='mfa_code' and xtype = 'U')
    create table mfa_code (
        id bigint identity
            constraint PK__mfa_code
                primary key,
        user_id int not null,
        expires_at datetime not null,
        created_at datetime not null default getdate(),
        code varchar(6) not null,
        constraint mfa_code_user_id_fk
            foreign key (user_id) references users(id)
    )