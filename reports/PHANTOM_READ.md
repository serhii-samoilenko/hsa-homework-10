## Phantom read

A phantom read occurs when a transaction retrieves a set of rows twice and new rows are inserted into or removed from that set 
by another transaction that is committed in between.

We will run two transactions in parallel.
The first transaction will query rows matching a criteria.
The second transaction will insert a new row matching the criteria and commit.
Then the first transaction will repeat the query.

We will use different isolation levels to see how they affect the result.

### MySQL with READ UNCOMMITTED:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 11ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-158', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-158]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-158'
-- Result: 9ms
```

Values: `[Dave, User-158]`

### MySQL with READ COMMITTED:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 2ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-658', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-658]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-658'
-- Result: 10ms
```

Values: `[Dave, User-658]`

### MySQL with REPEATABLE READ:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 2ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-734', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-734'
-- Result: 12ms
```

Values: `[Dave]`

### MySQL with SERIALIZABLE:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 3ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

`Bob: not finished after 1s`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

Alice will commit

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-336', 100)
```

`Alice: committed`

---

`Bob: committed`

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-336'
-- Result: 4ms
```

Values: `[Dave]`

### Postgres with READ UNCOMMITTED:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 2ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-331', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-331]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-331'
-- Result: 2ms
```

Values: `[Dave, User-331]`

### Postgres with READ COMMITTED:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 2ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-554', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-554]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-554'
-- Result: 3ms
```

Values: `[Dave, User-554]`

### Postgres with REPEATABLE READ:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 2ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-182', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-182'
-- Result: 2ms
```

Values: `[Dave]`

### Postgres with SERIALIZABLE:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 2ms
```

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

Alice will perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

---

Bob will insert a new row matching the criteria and commit

`Bob: began transaction`

```sql
-- Bob:
INSERT INTO accounts (name, amount) VALUES ('User-400', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-400'
-- Result: 2ms
```

Values: `[Dave]`

