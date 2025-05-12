IF NOT EXISTS (SELECT 1 FROM sys.sysobjects where name='ip_logins_agents' and xtype = 'U')
BEGIN
        CREATE TABLE ip_logins_agents (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        ip BIGINT NOT NULL,
        agent_id INT NOT NULL,
        login_date DATETIME2 NOT NULL,
        device VARCHAR(50)
    );
END

IF NOT EXISTS (SELECT 1 FROM sysobjects WHERE name='permission' AND xtype='U')
CREATE TABLE permission (
                            id INT PRIMARY KEY,
                            description VARCHAR(255) NOT NULL
);

IF NOT EXISTS (SELECT 1 FROM sysobjects WHERE name='groups' AND xtype='U')
CREATE TABLE groups (
                        id INT PRIMARY KEY,
                        description VARCHAR(255) NOT NULL
);

IF NOT EXISTS (SELECT 1 FROM sysobjects WHERE name='group_permission' AND xtype='U')
CREATE TABLE group_permission (
                                  group_id INT NOT NULL,
                                  permission_id INT NOT NULL,
                                  PRIMARY KEY (group_id, permission_id),
                                  FOREIGN KEY (group_id) REFERENCES groups(id),
                                  FOREIGN KEY (permission_id) REFERENCES permission(id)
);

IF NOT EXISTS (SELECT 1 FROM sysobjects WHERE name='user_group' AND xtype='U')
CREATE TABLE user_group (
                            group_id INT NOT NULL,
                            user_id INT NOT NULL,
                            PRIMARY KEY (group_id, user_id),
                            FOREIGN KEY (group_id) REFERENCES groups(id),
                            FOREIGN KEY (user_id) REFERENCES users(id)
);

IF NOT EXISTS (SELECT 1 FROM groups WHERE description = 'Risk Admin')
    INSERT INTO groups (id, description) VALUES (1, 'Risk Admin');

IF NOT EXISTS (SELECT 1 FROM permission WHERE description = 'RISK_ADMIN')
    INSERT INTO permission (id, description) VALUES (1, 'RISK_ADMIN');

IF NOT EXISTS (
    SELECT 1 FROM group_permission
    WHERE group_id = (SELECT id FROM groups WHERE description = 'Risk Admin')
      AND permission_id = (SELECT id FROM permission WHERE description = 'RISK_ADMIN')
)
    INSERT INTO group_permission (group_id, permission_id)
    VALUES ((SELECT id FROM groups WHERE description = 'Risk Admin'),
            (SELECT id FROM permission WHERE description = 'RISK_ADMIN'));

IF EXISTS (SELECT 1 FROM users WHERE user_name = 'operator') AND NOT EXISTS (
    SELECT 1 FROM user_group
    WHERE group_id = (SELECT id FROM groups WHERE description = 'Risk Admin')
      AND user_id = (SELECT id FROM users WHERE user_name = 'operator')
)
    INSERT INTO user_group (group_id, user_id)
    VALUES ((SELECT id FROM groups WHERE description = 'Risk Admin'),
            (SELECT id FROM users WHERE user_name = 'operator'));

IF EXISTS (SELECT 1 FROM users WHERE user_name = 'AdminPulpo') AND NOT EXISTS (
    SELECT 1 FROM user_group
    WHERE group_id = (SELECT id FROM groups WHERE description = 'Risk Admin')
      AND user_id = (SELECT id FROM users WHERE user_name = 'AdminPulpo')
)
    INSERT INTO user_group (group_id, user_id)
    VALUES ((SELECT id FROM groups WHERE description = 'Risk Admin'),
            (SELECT id FROM users WHERE user_name = 'AdminPulpo'));

IF EXISTS (SELECT 1 FROM users WHERE user_name = 'AgentSalsa') AND NOT EXISTS (
    SELECT 1 FROM user_group
    WHERE group_id = (SELECT id FROM groups WHERE description = 'Risk Admin')
      AND user_id = (SELECT id FROM users WHERE user_name = 'AgentSalsa')
)
    INSERT INTO user_group (group_id, user_id)
    VALUES ((SELECT id FROM groups WHERE description = 'Risk Admin'),
            (SELECT id FROM users WHERE user_name = 'AgentSalsa'));
