DELETE FROM USERS
WHERE agent_id IN (SELECT id FROM AGENTS)

DELETE FROM player_wallet

DELETE FROM user_transaction;

DELETE FROM wallet_transaction;

DELETE FROM agent_transaction;

DELETE FROM agents_players;

DELETE FROM AGENTS;

DELETE FROM wallets;

DELETE from users;

DELETE from agent_codes;

DELETE FROM players;