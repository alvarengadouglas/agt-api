alter table user_transaction
add  direct_player_agent_id int
go

update user_transaction
set direct_player_agent_id = u.id
from user_transaction ut
join agents_players ap on ut.player_id = ap.players_id
join agents a on ap.agent_id = a.id
join users u on a.user_id = u.id
go

ALTER TABLE user_transaction
ALTER COLUMN direct_player_agent_id int NOT NULL
go

ALTER TABLE user_transaction
ADD CONSTRAINT fk_user_transaction_direct_player_agent_id FOREIGN KEY (direct_player_agent_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
go

create index index_user_transaction_direct_player_agent_id
on user_transaction (direct_player_agent_id)
go
