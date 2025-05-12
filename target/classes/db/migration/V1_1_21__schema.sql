-- Verifica se a constraint única existe antes de tentar removê-la
IF EXISTS (
    SELECT 1
    FROM sys.objects
    WHERE name = 'UQ__players__5F8F663DE878888E'
    AND type = 'UQ'
)
ALTER TABLE players DROP CONSTRAINT UQ__players__5F8F663DE878888E;

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_players_platform_id'
    AND object_id = OBJECT_ID('players')
)
CREATE INDEX IX_players_platform_id ON players(platform_id);
