---
title: Reproduce Race Conditions
slug: race_conditions.html
---

A simple tool to reproduce race conditions such as deadlocks in automated tests.
<br/>
<br/>
For example, suppose that we need to reproduce a deadlock between two threads that are trying to modify two Postgres tables in different order.

| Orders       | Items        |
|--------------|--------------|
| Thread 1     | Thread 2     |
| Lock Order 1 |              |
|              | Lock Item 2  |
| Lock Item 2  |              |
|              | Lock Order 1 |

A brute force approach would be to run this scenario many times, hoping that eventually we shall reproduce the deadlock. Eventually this should work, but we shall have to spend some time setting up the test, and we might have to wait until it does reproduce.
<br/>
<br/>
Kotest's `runInParallel` makes the task much easier, and the deadlock is reproduced on the first attempt. The following code shows how to do this, assuming that `executeSql` function is implemented and does execute SQL.
Both threads do the following:
* begin a transaction
* update one table
* wait for the other thread to complete its first update
* try to update the other table

This is a textbook scenario of a deadlock, and it is reliably reproduced every time we run this code. All the busywork of setting up threads and synchronizing them is handled by `runInParallel`.

```kotlin
// Prerequisites:
executeSql(
  "DROP TABLE IF EXISTS test0",
  "DROP TABLE IF EXISTS test1",
  "SELECT 1 AS id, 'green' AS color INTO test0",
  "SELECT 1 AS id, 'yellow' AS color INTO test1",
)

// reproduce a deadlock

var successCount = 0
var thrownExceptions = mutableListOf<Throwable>()
runInParallel(
  { runner ->
    try {
      executeSql(jdbi, "UPDATE test0 SET color = 'blue' WHERE id = 1")
      jdbi.useTransaction<Exception> { handle ->
        handle.execute("UPDATE test0 SET color = 'blue' WHERE id = 1")
        runner.await() // wait for the other thread to do its thing
        handle.execute("UPDATE test1 SET color = 'purple' WHERE id = 1")
        successCount++
      }
    } catch (ex: Throwable) {
      thrownExceptions.add(ex)
    }
  },
  { runner ->
    try {
      jdbi.useTransaction<Exception> { handle ->
        handle.execute("UPDATE test1 SET color = 'blue' WHERE id = 1")
        runner.await() // wait for the other thread to do its thing
        handle.execute("UPDATE test0 SET color = 'purple' WHERE id = 1")
        successCount++
      }
    } catch (ex: Throwable) {
      thrownExceptions.add(ex)
    }
  }
)
successCount shouldBe 1
thrownExceptions shouldHaveSize 1
isDeadlock(thrownExceptions[0]) shouldBe true

```

