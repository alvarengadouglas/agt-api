-- Drop the comission_logs table if it exists
IF EXISTS (SELECT 1 FROM sys.sysobjects WHERE name='comission_logs' AND xtype = 'U')
    BEGIN
        DROP TABLE comission_logs;
    END

-- Remove the added columns from the agents table if they exist
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('agents') AND name = 'comission_sports')
    BEGIN
        ALTER TABLE agents DROP COLUMN comission_sports;
    END

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('agents') AND name = 'comission_slots')
    BEGIN
        ALTER TABLE agents DROP COLUMN comission_slots;
    END

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('agents') AND name = 'comission_casino')
    BEGIN
        ALTER TABLE agents DROP COLUMN comision_casino;
    END

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('agents') AND name = 'comission_type')
    BEGIN
        ALTER TABLE agents DROP COLUMN comission_type;
    END

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('agents') AND name = 'last_comission_update')
    BEGIN
        ALTER TABLE agents DROP COLUMN last_comission_update;
    END
