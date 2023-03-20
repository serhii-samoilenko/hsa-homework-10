## Non-repeatable read

A non-repeatable read occurs when a transaction retrieves a row twice and that row is updated by another transaction that is committed in between.

We will run two transactions in parallel.
The first transaction will read the value of the row.
The second transaction will increment the value of the row and commit.
Then the first transaction will read the value of the row again.

We will use different isolation levels to see how they affect the result.

### MySQL with READ UNCOMMITTED:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 0
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 10
```

Alice will commit

`Alice: committed`

---

Value: `10`

### MySQL with READ COMMITTED:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 10
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 20 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 20
```

Alice will commit

`Alice: committed`

---

Value: `20`

### MySQL with REPEATABLE READ:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 20
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 30 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 20
```

Alice will commit

`Alice: committed`

---

Value: `20`

### MySQL with SERIALIZABLE:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 30
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

`Bob: not finished after 1s`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 30
```

Alice will commit

```sql
-- Bob:
UPDATE accounts SET amount = 40 WHERE name = 'Charlie'
```

`Alice: committed`

---

`Bob: committed`

Value: `30`

### Postgres with READ UNCOMMITTED:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 0
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 10
```

Alice will commit

`Alice: committed`

---

Value: `10`

### Postgres with READ COMMITTED:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 10
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 20 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 20
```

Alice will commit

`Alice: committed`

---

Value: `20`

### Postgres with REPEATABLE READ:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 20
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 30 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 20
```

Alice will commit

`Alice: committed`

---

Value: `20`

### Postgres with SERIALIZABLE:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 30
```

---

Bob will select the value of the row, increment it and commit

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 40 WHERE name = 'Charlie'
```

`Bob: committed`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Charlie'
-- Result: 30
```

Alice will commit

`Alice: committed`

---

Value: `30`

