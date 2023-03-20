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
-- Result: 12ms
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
INSERT INTO accounts (name, amount) VALUES ('User-638', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-638]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-638'
-- Result: 7ms
```

Values: `[Dave, User-638]`

### MySQL with READ COMMITTED:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 1ms
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
INSERT INTO accounts (name, amount) VALUES ('User-454', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-454]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-454'
-- Result: 6ms
```

Values: `[Dave, User-454]`

### MySQL with REPEATABLE READ:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 1ms
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
INSERT INTO accounts (name, amount) VALUES ('User-475', 100)
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
DELETE FROM accounts WHERE name = 'User-475'
-- Result: 13ms
```

Values: `[Dave]`

### MySQL with SERIALIZABLE:

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
INSERT INTO accounts (name, amount) VALUES ('User-868', 100)
```

`Alice: committed`

---

`Bob: committed`

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-868'
-- Result: 11ms
```

Values: `[Dave]`

### Postgres with READ UNCOMMITTED:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 24ms
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
INSERT INTO accounts (name, amount) VALUES ('User-218', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-218]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-218'
-- Result: 2ms
```

Values: `[Dave, User-218]`

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
INSERT INTO accounts (name, amount) VALUES ('User-561', 100)
```

`Bob: committed`

---

Alice will AGAIN perform a query

```sql
-- Alice:
SELECT name FROM accounts WHERE amount >= 100
-- Result: [Dave, User-561]
```

Alice will commit

`Alice: committed`

---

```sql
-- System:
DELETE FROM accounts WHERE name = 'User-561'
-- Result: 2ms
```

Values: `[Dave, User-561]`

### Postgres with REPEATABLE READ:

System will make one row match the criteria

```sql
-- System:
Update accounts set amount = 100 where name = 'Dave'
-- Result: 3ms
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
INSERT INTO accounts (name, amount) VALUES ('User-170', 100)
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
DELETE FROM accounts WHERE name = 'User-170'
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
INSERT INTO accounts (name, amount) VALUES ('User-826', 100)
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
DELETE FROM accounts WHERE name = 'User-826'
-- Result: 4ms
```

Values: `[Dave]`

