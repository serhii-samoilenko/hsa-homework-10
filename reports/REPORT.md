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

Dirty read happens because the second transaction has access to uncommitted changes of the first one.

#### PostgreSQL with `READ UNCOMMITTED`:

Dirty read doesn't happen because PostgreSQL doesn't have dirty read capability.

#### MySQL with `READ COMMITTED`:

Dirty read doesn't happen on this isolation level.

#### PostgreSQL with `READ COMMITTED`:

Same as in `READ UNCOMMITTED` scenario.

#### MySQL with `REPEATABLE READ`:

Dirty read doesn't happen on this isolation level.

#### PostgreSQL with `REPEATABLE READ`:

Same as in previous PostgreSQL scenarios.

#### MySQL with `SERIALIZABLE`:

Dirty read doesn't happen, the second transaction is blocked until the first one commits.

#### PostgreSQL with `SERIALIZABLE`:

Same as in previous PostgreSQL scenarios.

### [Non-repeatable read scenario](NON_REPEATABLE_READ.md)

#### MySQL with `READ UNCOMMITTED`:

The first transaction will have non-repeatable read when the second transaction updates the row between the first transaction's SELECTs.

#### PostgreSQL with `READ UNCOMMITTED`:

The first transaction will have non-repeatable read, same as MySQL behavior in the scenario above.

#### MySQL with `READ COMMITTED`:

The first transaction will have non-repeatable read as in `READ UNCOMMITTED` scenario.

#### PostgreSQL with `READ COMMITTED`:

The first transaction will have non-repeatable read, same as MySQL behavior in the scenario above.

#### MySQL with `REPEATABLE READ`:

The first transaction will have repeatable reads, and the second transaction's write won't be blocked.

#### PostgreSQL with `REPEATABLE READ`:

The first transaction will have repeatable read behavior same as MySQL.

#### MySQL with `SERIALIZABLE`:

The first transaction will have repeatable reads, and the second transaction will be blocked until the first one commits.

#### PostgreSQL with `SERIALIZABLE`:

The first transaction will have repeatable reads, and the second transaction won't be blocked with its write.

### [Phantom read scenario](PHANTOM_READ.md)

#### MySQL with `READ UNCOMMITTED`:

Phantom read happens, the first transaction sees results added by the second transaction between the first transaction's SELECTs.

#### PostgreSQL with `READ UNCOMMITTED`:

Phantom read happens, same as in MySQL `READ UNCOMMITTED` scenario.

#### MySQL with `READ COMMITTED`:

Phantom read happens, same as in `READ UNCOMMITTED` scenario.

#### PostgreSQL with `READ COMMITTED`:

Phantom read happens, same as in scenarios above.

#### MySQL with `REPEATABLE READ`:

Phantom read doesn't happen, the second transaction's write is not visible to the first transaction.

#### PostgreSQL with `REPEATABLE READ`:

Phantom read doesn't happen, same as in MySQL `REPEATABLE READ` scenario above.

#### MySQL with `SERIALIZABLE`:

Phantom read doesn't happen, the second transaction is blocked until the first one commits.

#### PostgreSQL with `SERIALIZABLE`:

Phantom read doesn't happen, and the second transaction is not blocked.
