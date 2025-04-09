---
id: test_coroutine_dispatcher
title: Test Coroutine Dispatcher
slug: test-coroutine-dispatcher.html
sidebar_label: Test Coroutine Dispatcher
---


A _TestDispatcher_ is a special [CoroutineDispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/) provided by
the [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/README.md) module that allows
developers to control its virtual clock and skip delays.

A _TestDispatcher_ supports the following operations:

  * `currentTime` gets the current virtual time.
  * `runCurrent()` runs the tasks that are scheduled at this point of virtual time.
  * `advanceUntilIdle()` runs all enqueued tasks until there are no more.
  * `advanceTimeBy(timeDelta)` runs the enqueued tasks until the current virtual time advances by timeDelta.


To use a _TestDispatcher_ for a test, you can enable `coroutineTestScope` in test config:

```kotlin
class TestDispatcherTest : FunSpec() {
   init {
      test("foo").config(coroutineTestScope = true) {
         // this test will run with a test dispatcher
      }
   }
}
```

Inside this test, can you retrieve a handle to the scheduler through the extension val `testCoroutineScheduler`.
Using this scheduler, you can then manipulate the time:

```kotlin
import io.kotest.core.test.testCoroutineScheduler

class TestDispatcherTest : FunSpec() {
   init {
      test("advance time").config(coroutineTestScope = true) {
        val duration = 1.days
        // launch a coroutine that would normally sleep for 1 day
        launch {
          delay(duration.inWholeMilliseconds)
        }
        // move the clock on and the delay in the above coroutine will finish immediately.
        testCoroutineScheduler.advanceTimeBy(duration.inWholeMilliseconds)
        val currentTime = testCoroutineScheduler.currentTime
      }
   }
}
```

You can enable a test dispatcher for all tests in a spec by setting `coroutineTestScope` to true at the spec level:


```kotlin
class TestDispatcherTest : FunSpec() {
   init {
      coroutineTestScope = true
      test("this test uses a test dispatcher") {
      }
      test("and so does this test!") {
      }
   }
}
```


Finally, you can enable test dispatchers for all tests in a module by using [ProjectConfig](../project_config.md):

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override var testCoroutineDispatcher = true
}
```
