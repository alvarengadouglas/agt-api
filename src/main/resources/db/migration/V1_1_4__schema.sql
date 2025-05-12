INSERT INTO agent_codes (code) VALUES ('OPERATOR');
INSERT INTO agents
(user_id, wallet_id, credit_wallet_id, can_have_subagents, commission, parent_agent_id, code_id)
select o.user_id,  o.wallet_id, o.credit_wallet_id, 1, 0, null,
       IDENT_CURRENT('agent_codes') from operators o;


-- Declare the variable to be used.
DECLARE @OperatorId INT;
SET @OperatorId = IDENT_CURRENT('agents');

update agents
set parent_agent_id = @OperatorId
where parent_agent_id is null and id <> @OperatorId

drop table operators;