IF NOT EXISTS (SELECT 1 FROM sys.sysobjects where name='comission_logs' and xtype = 'U')
    CREATE TABLE comission_logs (
        id INT PRIMARY KEY IDENTITY,
        date DATETIME NOT NULL,
        parent_agent_id INT NOT NULL,
        agent_id INT NOT NULL,
        commission DECIMAL NULL,
        commission_slots DECIMAL,
        commission_sports DECIMAL,
        commission_casino DECIMAL,
        commission_type VARCHAR(255) NOT NULL,
    );


IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('agents') AND name = 'comission_sports')
BEGIN
ALTER TABLE agents ADD comission_sports DECIMAL(10, 2),
        comission_slots DECIMAL(10, 2),
        comission_casino DECIMAL(10, 2),
        comission_type VARCHAR(255),
        last_comission_update DATETIME NOT NULL DEFAULT GETDATE();

EXEC('UPDATE agents SET comission_type = ''F'' ');

END


