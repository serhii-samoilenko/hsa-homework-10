# Isolations & locks demo report

Creating tables and inserting data

In MySQL:

```sql
-- mysql:
CREATE TABLE IF NOT EXISTS accounts (
    id     INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(255),
    amount INT
);
TRUNCATE TABLE accounts;
INSERT INTO accounts (name, amount) VALUES ('Alice', 0), ('Bob', 0), ('Charlie', 0), ('Dave', 0), ('Eve', 0), ('Frank', 0)
-- Result: 194ms
```

In PostgreSQL:

```sql
-- postgres:
CREATE TABLE IF NOT EXISTS accounts (
    id     SERIAL PRIMARY KEY,
    name   VARCHAR(255),
    amount INT
);
TRUNCATE TABLE accounts;
INSERT INTO accounts (name, amount) VALUES ('Alice', 0), ('Bob', 0), ('Charlie', 0), ('Dave', 0), ('Eve', 0), ('Frank', 0)
-- Result: 41ms
```

### [Lost update scenario](LOST_UPDATE.md)

#### MySQL with `READ UNCOMMITTED`:

Lost update doesn't happen because of coincidence: second transaction will read uncommitted value of the first transaction and will use it
when updating the row.
The second transaction will wait for the first transaction to commit before committing itself.

#### PostgreSQL with `READ UNCOMMITTED`:

Lost update happens because second transaction doesn't have access to the first transaction's uncommitted changes, and it overwrites them.
The second transaction will wait for the first transaction to commit before committing itself.

#### MySQL with `READ COMMITTED`:

Lost update now happens because we don't have Dirty Read anymore. Same behavior as in PostgreSQL. Same wait for commit of the first
transaction.

#### PostgreSQL with `READ COMMITTED`:

Lost update happens the same way as with `READ UNCOMMITTED`.

#### MySQL with `REPEATABLE READ`:

Lost update happens, same as in `READ COMMITTED` scenario.

#### PostgreSQL with `REPEATABLE READ`:

Lost update happens the same way as with `READ UNCOMMITTED` and `READ COMMITTED`.

#### MySQL with `SERIALIZABLE`:

Lost update doesn't happen because the second transaction remains locked and can't even SELECT the row affected by the first transaction.
When the first transaction commits, the second one reads updated value and performs update basing on the updated value.

#### PostgreSQL with `SERIALIZABLE`:

Lost update doesn't happen because the DB detects the concurrent update by the second transaction. PostgreSQL doesn't block second transaction SELECT but blocks UPDATE until the second transaction commits. 

## [Dirty read scenario](DIRTY_READ.md)

#### MySQL with `READ UNCOMMITTED`:

TODO

#### PostgreSQL with `READ UNCOMMITTED`:

TODO

#### MySQL with `READ COMMITTED`:

TODO

#### PostgreSQL with `READ COMMITTED`:

TODO

#### MySQL with `REPEATABLE READ`:

TODO

#### PostgreSQL with `REPEATABLE READ`:

TODO

#### MySQL with `SERIALIZABLE`:

TODO

#### PostgreSQL with `SERIALIZABLE`:

TODO

### [Non-repeatable read scenario](NON_REPEATABLE_READ.md)

#### MySQL with `READ UNCOMMITTED`:

TODO

#### PostgreSQL with `READ UNCOMMITTED`:

TODO

#### MySQL with `READ COMMITTED`:

TODO

#### PostgreSQL with `READ COMMITTED`:

TODO

#### MySQL with `REPEATABLE READ`:

TODO

#### PostgreSQL with `REPEATABLE READ`:

TODO

#### MySQL with `SERIALIZABLE`:

TODO

#### PostgreSQL with `SERIALIZABLE`:

TODO

### [Phantom read scenario](PHANTOM_READ.md)

#### MySQL with `READ UNCOMMITTED`:

TODO

#### PostgreSQL with `READ UNCOMMITTED`:

TODO

#### MySQL with `READ COMMITTED`:

TODO

#### PostgreSQL with `READ COMMITTED`:

TODO

#### MySQL with `REPEATABLE READ`:

TODO

#### PostgreSQL with `REPEATABLE READ`:

TODO

#### MySQL with `SERIALIZABLE`:

TODO

#### PostgreSQL with `SERIALIZABLE`:

TODO
