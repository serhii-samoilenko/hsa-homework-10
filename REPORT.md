# Isolations & locks demo report

Creating tables and inserting data

In MySQL:

```sql
-- mysql:
CREATE TABLE IF NOT EXISTS accounts (
    id     INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(255),
    amount INT
)
TRUNCATE TABLE accounts
INSERT INTO accounts (name, amount) VALUES ('Alice', 0), ('Bob', 0), ('Charlie', 0), ('Dave', 0), ('Eve', 0), ('Frank', 0)
-- Result: 205ms
```

In PostgreSQL:

```sql
-- postgres:
CREATE TABLE IF NOT EXISTS accounts (
    id     SERIAL PRIMARY KEY,
    name   VARCHAR(255),
    amount INT
)
TRUNCATE TABLE accounts
INSERT INTO accounts (name, amount) VALUES ('Alice', 0), ('Bob', 0), ('Charlie', 0), ('Dave', 0), ('Eve', 0), ('Frank', 0)
-- Result: 36ms
```

### [Lost update scenario](LOST_UPDATE.md)

### [Dirty read scenario](DIRTY_READ.md)

### [Non-repeatable read scenario](NON_REPEATABLE_READ.md)

### [Phantom read scenario](PHANTOM_READ.md)

