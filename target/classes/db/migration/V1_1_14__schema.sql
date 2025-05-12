IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.table_constraints WHERE table_name = 'users' and constraint_name = 'uc_users_user_name')
ALTER TABLE users drop constraint uc_users_user_name;

IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.table_constraints WHERE table_name = 'players' and constraint_name = 'UQ__players__7C9273C4FFF24FBA')
ALTER TABLE players drop constraint UQ__players__7C9273C4FFF24FBA;