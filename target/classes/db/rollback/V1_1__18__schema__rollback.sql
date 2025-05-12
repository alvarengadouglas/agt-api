-- Remove 'bonus' column from agent_transaction table if it exists
IF EXISTS (
    SELECT *
    FROM sys.columns
    WHERE object_id = OBJECT_ID('agent_transaction')
      AND name = 'bonus'
)
    BEGIN
        ALTER TABLE agent_transaction
            DROP COLUMN bonus;
    END

-- Remove 'bonus' column from user_transaction table if it exists
IF EXISTS (
    SELECT *
    FROM sys.columns
    WHERE object_id = OBJECT_ID('user_transaction')
      AND name = 'bonus'
)
    BEGIN
        ALTER TABLE user_transaction
            DROP COLUMN bonus;
    END
