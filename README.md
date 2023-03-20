# Highload Software Architecture 8 Lesson 10 Homework

Transactions, Isolations, Locks
---

## Test project setup

The demo is written in Kotlin/Quarkus and uses docker-compose to run MongoDB and PostgreSQL databases.

The `com.example.DemoKt.runDemo` function is used to run four scenarios to demonstrate different isolation levels effects in MongoDB and
PostgreSQL.

Each scenario produces its own report in a Markdown format. The final reports are located in the [reports](reports) folder:

- [`LOST_UPDATE`](reports/LOST_UPDATE.md) scenario produced by [`LostUpdate.kt`](src/main/kotlin/com/example/scenarios/LostUpdate.kt)
  script.
- [`DIRTY_READ`](reports/DIRTY_READ.md) scenario produced by [`DirtyRead.kt`](src/main/kotlin/com/example/scenarios/DirtyRead.kt) script.
- [`NON_REPEATABLE_READ`](reports/NON_REPEATABLE_READ.md) scenario produced
  by [`NonRepeatableRead.kt`](src/main/kotlin/com/example/scenarios/NonRepeatableRead.kt) script.
- [`PHANTOM_READ`](reports/PHANTOM_READ.md) scenario produced by [`PhantomRead.kt`](src/main/kotlin/com/example/scenarios/PhantomRead.kt)
  script.

The summary of the results is located in the [REPORT.md](reports/REPORT.md) file.

## How to build and run

Start up MongoDB and PostgreSQL

```shell script
docker-compose up -d
```

Build and run demo application (Requires Java 17+)

```shell script
./gradlew build && \
java -jar build/quarkus-app/quarkus-run.jar
```

You can also run application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```
