
CREATE OR ALTER FUNCTION fc_agents_hierarchy (
    @AgentId INT
)
    RETURNS TABLE
        AS
        RETURN

        WITH CTE AS
                 (
                     SELECT		t1.id,
                                   [users].id AS [user_id],
                                   trim([users].user_name) as user_name,
                                   t1.parent_agent_id,
                                   1 AS [Level],
                                   CAST((user_name) AS VARCHAR(MAX)) AS Hierarchy,
                                   t1.wallet_id,
                                   t1.credit_wallet_id,
                                   t1.balance,
                                   t1.commission
                     FROM		[agents] AS t1
                                     INNER JOIN  [users]	 ON [users].id = t1.user_id
                     WHERE		t1.id = @AgentId

                     UNION ALL

                     SELECT		t2.id,
                                   [users].id AS [user_id],
                                   trim([users].user_name) as user_name,
                                   t2.parent_agent_id,
                                   M.[level] + 1 AS [Level],
                                   CAST((M.Hierarchy + '->' + [users].user_name) AS VARCHAR(MAX)) AS Hierarchy,
                                   t2.wallet_id,
                                   t2.credit_wallet_id,
                                   t2.balance,
                                   t2.commission
                     FROM		[agents] AS t2
                                     INNER JOIN  [users]	 ON [users].id = t2.user_id
                                     INNER JOIN	CTE AS M ON t2.parent_agent_id = M.id

                 )
        SELECT	*
        FROM	CTE;
go

