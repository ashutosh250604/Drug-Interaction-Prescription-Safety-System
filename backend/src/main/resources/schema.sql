-- Schema fixups that Hibernate's ddl-auto=update will not perform.
--
-- Older copies of this database defined severity as
-- ENUM('LOW','MEDIUM','HIGH') plus a CHECK constraint listing those three
-- values. The application now also stores UNKNOWN, which both reject with
-- "Data truncated for column 'severity'" / "Check constraint violated".
--
-- Widening the column is idempotent. The constraint is dropped only when it
-- exists, then recreated with UNKNOWN included.
ALTER TABLE interaction MODIFY COLUMN severity VARCHAR(10) NOT NULL;

SET @drop_severity_check := (
    SELECT IF(COUNT(*) > 0,
              'ALTER TABLE interaction DROP CHECK chk_severity',
              'SELECT 1')
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'interaction'
      AND CONSTRAINT_NAME = 'chk_severity'
      AND CONSTRAINT_TYPE = 'CHECK'
);
PREPARE drop_severity_check_stmt FROM @drop_severity_check;
EXECUTE drop_severity_check_stmt;
DEALLOCATE PREPARE drop_severity_check_stmt;

ALTER TABLE interaction ADD CONSTRAINT chk_severity
    CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN'));
