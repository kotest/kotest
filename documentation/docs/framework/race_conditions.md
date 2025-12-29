---
title: Reproduce Race Conditions
slug: race_conditions.html
---

A simple tool to reproduce some common race conditions such as deadlocks in automated tests.
<br/>
<br/>

Whenever multiple coroutines or threads mutate shared state, there is a possibility of race conditions.
<br/>
<br/>
In many common cases this tool allows to reproduce them easily.
<br/>
<br/>
Suppose, for instance, that the following code runs without any synchronization concurrently:

```kotlin
if(canRunTask()) {
    runTask()
}
```

Without concurrency, this code will always run correctly. Let us reproduce concurrency as follows:

```kotlin
   private data class Box(val maxCapacity: Int) {
      private val items = mutableListOf<String>()

      fun addItem(item: String) = items.add(item)

      fun hasCapacity() = items.size < maxCapacity

      fun items() = items.toList()
   }

(snip)

"two tasks share one mutable state, both make the same decision at the same time" {
  val box = Box(maxCapacity = 2)
  box.addItem("apple")
  runInParallel({ runner: ParallelRunner ->
    val hasCapacity = box.hasCapacity()
    runner.await()
    if(hasCapacity) {
      box.addItem("banana")
    }
  },
    { runner: ParallelRunner ->
      val hasCapacity = box.hasCapacity()
      runner.await()
      if(hasCapacity) {
        box.addItem("orange")
      }
    }
  )
  // capacity is exceeded as a result of race condition
  box.items() shouldContainExactlyInAnyOrder listOf("apple", "banana", "orange")
}

```

For another example, suppose that we need to reproduce a deadlock between two threads that are trying to modify two Postgres tables in different order.

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
<br/>
Finally, let's use `parallelRunner` to demostrate that mocking a static function such as `LocalDateTime.now()` in one test can affect completely different tests running in parallel:

```kotlin
runInParallel(
{ runner: ParallelRunner ->
    timedPrint("Before mock on same thread: ${LocalDateTime.now().toString()}")
    runner.await()
    mockkStatic(LocalDateTime::class)
    val localTime = LocalDateTime.of(2022, 4, 27, 12, 34, 56)
    every { LocalDateTime.now(any<Clock>()) } returns localTime
    runner.await()
    timedPrint("After mock on same thread: ${LocalDateTime.now().toString()}")
},
{ runner: ParallelRunner ->
        timedPrint("Before mock on other thread: ${LocalDateTime.now().toString()}")
        runner.await()
        runner.await()
        timedPrint("After mock on other thread: ${LocalDateTime.now().toString()}")
    }
)

// the output from both threads shows the same mocked output:

Time: 2023-05-12T13:14:07.815923, Thread: 51, Before mock on other thread: 2023-05-12T13:14:07.737748
Time: 2023-05-12T13:14:07.816011, Thread: 50, Before mock on same thread: 2023-05-12T13:14:07.737736
Time: 2022-04-27T12:34:56, Thread: 51, After mock on other thread: 2022-04-27T12:34:56
Time: 2022-04-27T12:34:56, Thread: 50, After mock on same thread: 2022-04-27T12:34:56
```
