IF NOT EXISTS (SELECT 1 FROM groups WHERE description = 'Agent Default')
    INSERT INTO groups (id, description) VALUES (2, 'Agent Default');

IF NOT EXISTS (SELECT 1 FROM permission WHERE description = 'AGENT_DEFAULT')
    INSERT INTO permission (id, description) VALUES (2, 'AGENT_DEFAULT');

IF NOT EXISTS (
    SELECT 1 FROM group_permission
    WHERE group_id = (SELECT id FROM groups WHERE description = 'Agent Default')
      AND permission_id = (SELECT id FROM permission WHERE description = 'AGENT_DEFAULT')
)
    INSERT INTO group_permission (group_id, permission_id)
    VALUES ((SELECT id FROM groups WHERE description = 'Agent Default'),
            (SELECT id FROM permission WHERE description = 'AGENT_DEFAULT'));

IF NOT EXISTS (SELECT 1 FROM groups WHERE description = 'Operator Default')
    INSERT INTO groups (id, description) VALUES (3, 'Operator Default');

IF NOT EXISTS (SELECT 1 FROM permission WHERE description = 'OPERATOR_DEFAULT')
    INSERT INTO permission (id, description) VALUES (3, 'OPERATOR_DEFAULT');

IF NOT EXISTS (
    SELECT 1 FROM group_permission
    WHERE group_id = (SELECT id FROM groups WHERE description = 'Operator Default')
      AND permission_id = (SELECT id FROM permission WHERE description = 'OPERATOR_DEFAULT')
)
    INSERT INTO group_permission (group_id, permission_id)
    VALUES ((SELECT id FROM groups WHERE description = 'Operator Default'),
            (SELECT id FROM permission WHERE description = 'OPERATOR_DEFAULT'));


IF EXISTS (SELECT 1 FROM users WHERE user_role = 'OPERATOR')
    BEGIN
        INSERT INTO user_group (group_id, user_id)
        SELECT
            (SELECT id FROM groups WHERE description = 'Operator Default') AS group_id,
            u.id AS user_id
        FROM users u
        WHERE u.user_role = 'OPERATOR'
          AND NOT EXISTS (
            SELECT 1
            FROM user_group ug
            WHERE ug.user_id = u.id
              AND ug.group_id = (SELECT id FROM groups WHERE description = 'Operator Default')
        );
    END


BEGIN
    INSERT INTO user_group (group_id, user_id)
    SELECT
        (SELECT id FROM groups WHERE description = 'Agent Default') AS group_id,
        u.id AS user_id
    FROM users u
    WHERE u.user_role = 'AGENT'
      AND NOT EXISTS (
        SELECT 1
        FROM user_group ug
        WHERE ug.user_id = u.id
          AND ug.group_id = (SELECT id FROM groups WHERE description = 'Agent Default')
    );
END