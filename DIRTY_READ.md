## Dirty read

The first transaction will perform two reads of the same row.
The second transaction will update the row between the two reads.

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
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 10
```

Alice will commit

`Alice: committed`

Bob will rollback

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
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

`Alice: committed`

Bob will rollback

---

Value: `0`

### MySQL with REPEATABLE READ:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

`Alice: committed`

Bob will rollback

---

Value: `0`

### MySQL with SERIALIZABLE:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

`Bob: not finished after 1s`

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

`Alice: committed`

Bob will rollback

---

Value: `0`

### Postgres with READ UNCOMMITTED:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

`Alice: committed`

Bob will rollback

---

Value: `0`

### Postgres with READ COMMITTED:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

`Alice: committed`

Bob will rollback

---

Value: `0`

### Postgres with REPEATABLE READ:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

`Alice: committed`

Bob will rollback

---

Value: `0`

### Postgres with SERIALIZABLE:

`Alice: began transaction`

```sql
-- Alice:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

Alice will select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

---

Bob will select the value of the row and increment it

`Bob: began transaction`

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Bob'
```

---

Alice will AGAIN select the value of the row

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Bob'
-- Result: 0
```

Alice will commit

`Alice: committed`

Bob will rollback

---

Value: `0`

