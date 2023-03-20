## Lost update

We will use two transactions to update the same row in the database.
The first transaction will read the value of the row, increment it by 1 and write it back.
The second transaction will read the value of the row, increment it by 2 and write it back.

We will run the two transactions in parallel.

We will use different isolation levels to see how they affect the result.

### MySQL with READ UNCOMMITTED:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 0
```

```sql
-- Alice:
UPDATE accounts SET amount = 1 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 1
```

`Bob: not finished after 1s`

---

Alice will commit

```sql
-- Bob:
UPDATE accounts SET amount = 11 WHERE name = 'Alice'
```

`Alice: committed`

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 11
```

Value: `11`

### MySQL with READ COMMITTED:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 11
```

```sql
-- Alice:
UPDATE accounts SET amount = 12 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 11
```

`Bob: not finished after 1s`

---

Alice will commit

```sql
-- Bob:
UPDATE accounts SET amount = 21 WHERE name = 'Alice'
```

`Alice: committed`

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 21
```

Value: `21`

### MySQL with REPEATABLE READ:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 21
```

```sql
-- Alice:
UPDATE accounts SET amount = 22 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 21
```

`Bob: not finished after 1s`

---

Alice will commit

```sql
-- Bob:
UPDATE accounts SET amount = 31 WHERE name = 'Alice'
```

`Alice: committed`

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 31
```

Value: `31`

### MySQL with SERIALIZABLE:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 31
```

```sql
-- Alice:
UPDATE accounts SET amount = 32 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

`Bob: not finished after 1s`

---

Alice will commit

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 32
```

`Alice: committed`

```sql
-- Bob:
UPDATE accounts SET amount = 42 WHERE name = 'Alice'
```

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 42
```

Value: `42`

### Postgres with READ UNCOMMITTED:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 0
```

```sql
-- Alice:
UPDATE accounts SET amount = 1 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 0
```

`Bob: not finished after 1s`

---

Alice will commit

```sql
-- Bob:
UPDATE accounts SET amount = 10 WHERE name = 'Alice'
```

`Alice: committed`

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 10
```

Value: `10`

### Postgres with READ COMMITTED:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 10
```

```sql
-- Alice:
UPDATE accounts SET amount = 11 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 10
```

`Bob: not finished after 1s`

---

Alice will commit

`Alice: committed`

```sql
-- Bob:
UPDATE accounts SET amount = 20 WHERE name = 'Alice'
```

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 20
```

Value: `20`

### Postgres with REPEATABLE READ:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 20
```

```sql
-- Alice:
UPDATE accounts SET amount = 21 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 20
```

`Bob: not finished after 1s`

---

Alice will commit

`Alice: committed`

```sql
-- Bob:
UPDATE accounts SET amount = 30 WHERE name = 'Alice'
-- Result: ERROR: could not serialize access due to concurrent update
```

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 21
```

Value: `21`

### Postgres with SERIALIZABLE:

`Alice: began transaction`

`Bob: began transaction`

```sql
-- Bob:
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
```

---

Alice will select the value of the row and increment it

```sql
-- Alice:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 21
```

```sql
-- Alice:
UPDATE accounts SET amount = 22 WHERE name = 'Alice'
```

Bob will select the value of the row and increment it

```sql
-- Bob:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 21
```

`Bob: not finished after 1s`

---

Alice will commit

`Alice: committed`

```sql
-- Bob:
UPDATE accounts SET amount = 31 WHERE name = 'Alice'
-- Result: ERROR: could not serialize access due to concurrent update
```

Bob will commit

`Bob: committed`

---

The value of the row is now:

```sql
-- System:
SELECT amount FROM accounts WHERE name = 'Alice'
-- Result: 22
```

Value: `22`

