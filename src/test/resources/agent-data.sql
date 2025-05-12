------------------------------------------------------
-- operator
--
-- agentNoSub_1
--     player_1_1
-- agentNoSub_2
--
-- agentWithSub_3
-- 	subagent_3_1
-- 		player_3_1_1
-- 		subagent_3_1_1
-- 			player_3_1_1_1
-- 		subagent_3_1_2
-- 			subagent_3_1_2_1
-- 				player_3_1_2_1_1
-- 	subagent_3_2
-- 		player_3_2_1
-- 		player_3_2_2
--
-- agentWithSub_4
-- 	player_4_1
-- 	subagent_4_1
--
-- agentWithSub_5
-- 	subagent_5_1
-- 		player_5_1
--
-- agentWithSub_6
-- 	subagent_6_1

----------------------------------------------------------------------------------------------------

set identity_insert users on;

insert into users
(id, user_name, password, user_role, created_on, bornDate, currency, mail, last_login, phone,
 receiveEmail, status, test_user, first_name, last_name)
values (1, 'operator', 'pass', 'OPERATOR', N'2019-11-30 23:56:00.0000000',
        N'1988-06-27 06:30:00.0000000',
        0, 'operator@gmail.com', N'2020-01-15 13:50:00.0000000', '0785455685', 1, 'ACTIVE', 0,
        'operatorFirstName', 'operatorLastName'),
       (2, 'agentNoSub_1', 'pass', 'AGENT', N'2021-07-01 01:58:58.0300000',
        N'2001-07-01 01:32:32.6990000', 0, N'agentNoSub_1@gmail.com',
        N'2022-09-01 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        'agentNoSub_1_FN', 'agentNoSub_1_LN'),
       (3, N'agentNoSub_2', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'agentNoSub_2@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'agentNoSub_2_FN', N'agentNoSub_2_LN'),
       (4, N'agentWithSub_3', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'agentWithSub_3@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'agentWithSub_3_FN', N'agentWithSub_3_LN'),
       (5, N'subagent_3_1', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_3_1@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_3_1_FN', N'subagent_3_1_LN'),
       (6, N'subagent_3_1_1', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_3_1_1@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_3_1_1_FN', N'subagent_3_1_1_LN'),
       (7, N'subagent_3_1_2', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_3_1_2@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_3_1_2_FN', N'subagent_3_1_2_LN'),
       (8, N'subagent_3_1_2_1', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_3_1_2_1@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_3_1_2_1_FN', N'subagent_3_1_2_1_LN'),
       (9, N'subagent_3_2', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_3_2@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_3_2_FN', N'subagent_3_2_LN'),
       (10, N'agentWithSub_4', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'agentWithSub_4@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'agentWithSub_4_FN', N'agentWithSub_4_LN'),
       (11, N'subagent_4_1', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_4_1@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_4_1_FN', N'subagent_4_1_LN'),
       (12, N'agentWithSub_5', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'agentWithSub_5@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'agentWithSub_5_FN', N'agentWithSub_5_LN'),
       (13, N'subagent_5_1', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_5_1@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_5_1_FN', N'subagent_5_1_LN'),
       (14, N'agentWithSub_6', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'agentWithSub_6@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'agentWithSub_6_FN', N'agentWithSub_6_LN'),
       (15, N'subagent_6_1', N'pass', N'AGENT', N'2021-07-02 01:58:58.0300000',
        N'2001-07-02 01:32:32.6990000', 0, N'subagent_6_1@gmail.com',
        N'2022-09-02 01:35:43.4630000', N'111112222233', 1, 'ACTIVE', 0,
        N'subagent_6_1_FN', N'subagent_6_1_LN');

set identity_insert users off;

----------------------------------------------------------------------------------------------------

set identity_insert wallets on;

insert into wallets (id, balance, is_credits)
values (1, 100000, 0), --  operator
       (2, 200000, 1), --  operator
       (3, 1500, 0),   --  agentNoSub_1
       (4, 4000, 1),   --  agentNoSub_1
       (5, 1500, 0),   --  agentNoSub_2
       (6, 4000, 1),   --  agentNoSub_2
       (7, 1500, 0),   --  agentWithSub_3
       (8, 4000, 1),   --  agentWithSub_3
       (9, 1500, 0),   --  subagent_3_1
       (10, 4000, 1),  --  subagent_3_1
       (11, 1500, 0),  --  subagent_3_1_1
       (12, 4000, 1),  --  subagent_3_1_1
       (13, 1500, 0),  --  subagent_3_1_2
       (14, 4000, 1),  --  subagent_3_1_2
       (15, 1500, 0),  --  subagent_3_1_2_1
       (16, 4000, 1),  --  subagent_3_1_2_1
       (17, 1500, 0),  --  subagent_3_2
       (18, 4000, 1),  --  subagent_3_2
       (19, 1500, 0),  --  agentWithSub_4
       (20, 4000, 1),  --  agentWithSub_4
       (21, 1500, 0),  --  subagent_4_1
       (22, 4000, 1),  --  subagent_4_1
       (23, 1500, 0),  --  agentWithSub_5
       (24, 4000, 1),  --  agentWithSub_5
       (25, 1500, 0),  --  subagent_5_1
       (26, 4000, 1),  --  subagent_5_1
       (27, 1500, 0),  --  agentWithSub_6
       (28, 4000, 1),  --  agentWithSub_6
       (29, 1500, 0),  --  subagent_6_1
       (30, 4000, 1); --  subagent_6_1

set identity_insert wallets off;

----------------------------------------------------------------------------------------------------

set identity_insert agent_codes on;

insert into agent_codes (id, code)
values (1, 'A1'),   --  agentNoSub_1
       (2, 'A2'),   --  agentNoSub_2
       (3, 'A3'),   --  agentWithSub_3
       (4, 'A4'),   --  subagent_3_1
       (5, 'A5'),   --  subagent_3_1_1
       (6, 'A6'),   --  subagent_3_1_2
       (7, 'A7'),   --  subagent_3_1_2_1
       (8, 'A8'),   --  subagent_3_2
       (9, 'A9'),   --  agentWithSub_4
       (10, 'A10'), --  subagent_4_1
       (11, 'A11'), --  agentWithSub_5
       (12, 'A12'), --  subagent_5_1
       (13, 'A13'), --  agentWithSub_6
       (14, 'A14'), --  subagent_6_1
       (15, 'OPERATOR'); --  subagent_6_1

set identity_insert agent_codes off;

----------------------------------------------------------------------------------------------------

set identity_insert agents on;

insert into agents (id, user_id, wallet_id, credit_wallet_id, can_have_subagents, commission, parent_agent_id, code_id)
values (15, 1, 1, 2, 1, 0, null, 15);

set identity_insert agents off;

----------------------------------------------------------------------------------------------------

set identity_insert agents on

insert into agents (id, user_id, wallet_id, credit_wallet_id, can_have_subagents, commission,
                    parent_agent_id, code_id)
values (1, 2, 3, 4, 1, 5, 15, 1),      -- agentNoSub_1
       (2, 3, 5, 6, 1, 10, 15, 2),     -- agentNoSub_2
       (3, 4, 7, 8, 1, 5, 15, 3),      -- agentWithSub_3
       (4, 5, 9, 10, 1, 10, 3, 4),       -- subagent_3_1
       (5, 6, 11, 12, 1, 5, 4, 5),       -- subagent_3_1_1
       (6, 7, 13, 14, 1, 10, 4, 6),      -- subagent_3_1_2
       (7, 8, 15, 16, 1, 5, 6, 7),       -- subagent_3_1_2_1
       (8, 9, 17, 18, 1, 10, 3, 8),      -- subagent_3_2
       (9, 10, 19, 20, 1, 5, 15, 9),   -- agentWithSub_4
       (10, 11, 21, 22, 1, 10, 9, 10),   -- subagent_4_1
       (11, 12, 23, 24, 1, 5, 15, 11), -- agentWithSub_5
       (12, 13, 25, 26, 1, 10, 11, 12),  -- subagent_5_1
       (13, 14, 27, 28, 1, 5, 15, 13), -- agentWithSub_6
       (14, 15, 29, 30, 1, 10, 13, 14); --  subagent_6_1

set identity_insert agents off

----------------------------------------------------------------------------------------------------

set identity_insert players on;

insert into players (id, user_name, platform_id)
values (1, 'player_1_1', 11),
       (2, 'player_3_1_1', 12),
       (3, 'player_3_1_1_1', 13),
       (4, 'player_3_1_2_1_1', 14),
       (5, 'player_3_2_1', 15),
       (6, 'player_3_2_2', 16),
       (7, 'player_4_1', 17),
       (8, 'player_5_1', 18);

set identity_insert players off;

----------------------------------------------------------------------------------------------------

set identity_insert player_wallet on;

insert into player_wallet (id, player_id, balance, platform_balance)
values (1, 1, 45, 150),
       (2, 2, 50, 250),
       (3, 3, 45, 150),
       (4, 4, 50, 250),
       (5, 5, 45, 150),
       (6, 6, 50, 250),
       (7, 7, 45, 150),
       (8, 8, 50, 250);

set identity_insert player_wallet off;

----------------------------------------------------------------------------------------------------

set identity_insert agents_players on;

insert into agents_players (id, agent_id, players_id)
values (1, 1, 1), --  agentNoSub_1     ->  player_1_1
       (2, 4, 2), --  subagent_3_1     ->  player_3_1_1
       (3, 5, 3), --  subagent_3_1_1   ->  player_3_1_1_1
       (4, 7, 4), --  subagent_3_1_2_1 ->  player_3_1_2_1_1
       (5, 8, 5), --  subagent_3_2     ->  player_3_2_1
       (6, 8, 6), --  subagent_3_2     ->  player_3_2_2
       (7, 9, 7), --  agentWithSub_4   ->  player_4_1
       (8, 12, 8); --  subagent_5_1     ->  player_5_1

set identity_insert agents_players off;

----------------------------------------------------------------------------------------------------
