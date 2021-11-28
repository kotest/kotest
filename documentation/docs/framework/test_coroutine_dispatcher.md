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


To use a _TestDispatcher_ for a test, you can enable `testCoroutineDispatcher` in test config:

```kotlin
class TestDispatcherTest : FunSpec() {
   init {
      test("foo").config(testCoroutineDispatcher = true) {
         // this test will run with a test dispatcher
      }
   }
}
```

Inside this test, can you retrieve a handle to the dispatcher through the extension value `delayController`.
Using this controller, you can then manipulate time:

```kotlin
import io.kotest.core.test.delayController

class TestDispatcherTest : FunSpec() {
   init {
      test("foo").config(testCoroutineDispatcher = true) {
        delayController.advanceTimeBy(1234)
        val currentTime = delayController.currentTime
      }
   }
}
```

You can enable test dispatchers for all tests in a spec by setting `testCoroutineDispatcher` to true at the spec level:


```kotlin
class TestDispatcherTest : FunSpec() {
   init {
      testCoroutineDispatcher = true
      test("this test uses a test dispatcher") {
      }
      test("and so does this test!") {
      }
   }
}
```


Finally, you can enable test dispatchers for all tests in a module by using [ProjectConfig](project_config.md):

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val testCoroutineDispatcher = true
}
```
