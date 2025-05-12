
create table agent_codes
(
    id   int identity
        constraint PK__agent_codes
            primary key,
    code varchar(10) not null
        unique
)
go

create table countries
(
    id           int not null
        constraint countries_pk
            primary key,
    ISO2         varchar(255),
    country_name varchar(255)
)
go


create table oauth_client_details
(
    client_id               varchar(255) not null,
    resource_ids            varchar(255) default NULL,
    client_secret           varchar(255) default NULL,
    scope                   varchar(255) default NULL,
    authorized_grant_types  varchar(255) default NULL,
    web_server_redirect_uri varchar(255) default NULL,
    authorities             varchar(255) default NULL,
    access_token_validity   int          default NULL,
    refresh_token_validity  int          default NULL,
    additional_information  varchar(255) default NULL,
    autoapprove             varchar(255) default NULL
)
go

create table players
(
    id          int identity
        constraint PK__players
            primary key,
    user_name   varchar(100) not null
        unique,
    platform_id int          not null
        unique
)
go

create table player_wallet
(
    id               int identity
        constraint PK__player_wallet
            primary key,
    player_id        int    not null
        constraint player_wallet_player_fk
            references players,
    balance          bigint not null,
    platform_balance bigint not null
)
go



create table wallets
(
    id         int identity
        constraint PK__wallets
            primary key,
    balance    bigint not null,
    is_credits bit    not null
)
go

create table agents
(
    id                 int identity
        constraint PK__agents
            primary key,
    user_id            int            not null,
    wallet_id          int            not null
        constraint agents_wallet_fk
            references wallets,
    credit_wallet_id   int            not null
        constraint agents_credit_wallet_fk
            references wallets,
    can_have_subagents bit            not null,
    commission         decimal(10, 2) not null,
    parent_agent_id    int
        constraint agents_parent_agent_fk
            references agents,
    code_id            int            not null
        unique
        constraint agents_code_fk
            references agent_codes
)
go

create index index_agents_user_id
    on agents (user_id)
go

create table agents_players
(
    id         int identity
        constraint PK__agent_players
            primary key,
    agent_id   int not null
        constraint agents_players_agents_fk
            references agents,
    players_id int not null
        unique
        constraint agents_players_players_fk
            references players
)
go

create table users
(
    id                               int identity
        constraint PK__users__3213E83F3DBE1285
            primary key,
    address                          varchar(100),
    apartment_number                 varchar(50),
    btag_id                          int,
    bornDate                         datetime2           not null,
    city                             varchar(100),
    created_on                       datetime2           not null,
    currency                         int                 not null,
    document_number                  varchar(100),
    document_type                    int,
    mail                             varchar(100),
    first_name                       varchar(50),
    language                         varchar(3),
    last_bonus_check                 datetime2,
    last_login                       datetime2           not null,
    last_name                        varchar(30),
    msn                              varchar(100),
    neighborhood                     varchar(100),
    password                         varchar(128)        not null,
    phone                            varchar(30),
    receiveEmail                     bit                 not null,
    reference_point                  varchar(100),
    user_role                        varchar(40)         not null,
    sex                              varchar,
    skype                            varchar(100),
    state                            varchar(100),
    status                           varchar(20)         not null,
    test_user                        bit                 not null,
    time_zone                        varchar(50),
    twitter                          varchar(100),
    user_name                        varchar(100)        not null
        constraint uc_users_user_name
            unique,
    zip_code                         varchar(50),
    country                          int
        constraint FK6A68E08DCADA7FC
            references countries,
    allow_update_birthday            tinyint default '0' not null,
    valid_status                     tinyint default 0   not null,
    trans_date                       datetime2,
    last_modification_date           datetime2,
    bo_cashier_id                    int     default NULL,
    billpocket_id                    int,
    cellphone                        varchar(30),
    vip                              bit     default 0   not null,
    id_btag_netrefer                 int,
    receive_notifications            bit     default 1,
    phone_area                       int,
    phone_country                    int,
    cellphone_area                   int,
    cellphone_country                int,
    register_language                varchar(3),
    user_plataform                   varchar(7),
    cellphone_iso2                   varchar(50),
    player_type                      int,
    hide_from_ranking                bit     default 0,
    show_balance                     bit,
    user_have_muchbetter_transaction bit,
    date_change_phone_muchbetter     date,
    receive_sms                      bit,
    receive_call                     bit,
    last_update_personal_info        datetime2,
    self_exclusion_motive            int,
    agent_id                         int
        constraint users_agent_fk
            references agents
)
go

alter table agents
    add constraint agents_users_fk
        foreign key (user_id) references users
go

create table operators
(
    id               int identity
        constraint PK__operators
            primary key,
    user_id          int not null
        constraint operators_users_fk
            references users,
    wallet_id        int not null
        constraint operators_wallet_fk
            references wallets,
    credit_wallet_id int not null
        constraint operators_credit_wallet_fk
            references wallets
)
go

create index index_operators_user_id
    on operators (user_id)
go

create table user_transaction
(
    id             int identity
        constraint PK__user_transaction
            primary key,
    user_id        int         not null
        constraint user_transaction_user_fk
            references users,
    player_id      int         not null
        constraint user_transaction_player_fk
            references players,
    amount         bigint      not null,
    operation_type varchar(50) not null,
    operation_date datetime2   not null,
    balance        bigint      not null
)
go

create index index_users_created_on
    on users (created_on)
go

create index index_users_firstname
    on users (first_name)
go

create index index_users_adress
    on users (address)
go

create index index_users_lastname
    on users (last_name)
go

create index index_users_playertype
    on users (player_type)
go

create index index_users_documentnumber
    on users (document_number)
go

create index index_users_username
    on users (user_name)
go

create index index_users_email
    on users (mail)
go

create index index_users_user_role
    on users (user_role)
go

create table wallet_transaction
(
    id             int identity
        constraint PK__wallet_transaction
            primary key,
    wallet_id      int         not null
        constraint wallet_transaction_user_fk
            references wallets,
    amount         bigint      not null,
    operation_type varchar(50) not null,
    operation_date datetime2   not null,
    balance        bigint      not null,
    note           varchar(300)
)
go

