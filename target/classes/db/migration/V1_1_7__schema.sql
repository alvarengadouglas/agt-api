create index user_transaction_operation_date_idx
    on user_transaction (operation_date)
go

create index agent_transaction_operation_date_idx
    on agent_transaction (operation_date)
go

create index agent_players_agent_id_idx
    on agents_players (agent_id)
go
