---
id: eventually
title: Eventually
slug: eventually.html
---


When testing non-deterministic code, a common use case is "I expect this code to pass after a short period of time".

For example, if you were testing a IO operation, you might need to wait until the IO operation has flushed.

Sometimes you can do a Thread.sleep but this is isn't ideal as you need to set a sleep threshold high enough so that it
won't expire prematurely on a slow machine. Plus it means that your test will sit around waiting on the timeout even if
the code completes quickly on a fast machine.

Or you can roll a loop and sleep and retry and sleep and retry, but this is just boilerplate slowing you down.

Another common approach is to use countdown latches and this works fine if you are able to inject the latches in the
appropriate places but it isn't always possible to have the code under test trigger a latch.

As an alternative, kotest provides the `eventually` function and the `Eventually` configuration which periodically test
the code ignoring your specified exceptions and ensuring the result satisfies an optional predicate, until the timeout
is eventually reached or too many iterations have passed. This is flexible and is perfect for testing nondeterministic
code.


### Examples


#### Simple examples

Let's assume that we send a message to an asynchronous service.
After the message is processed, a new row is inserted into user table.

We can check this behaviour with our `eventually` function.

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("check if user repository has one row after message is sent") {
      sendMessage()
      eventually(5.seconds) {
        userRepository.size() shouldBe 1
      }
    }
  }
}
```

#### Exceptions

By default, `eventually` will ignore any `AssertionError` that is thrown inside the function (note, that means it won't catch `Error`).
If you want to be more specific, you can tell `eventually` to ignore specific exceptions and any others will immediately fail the test.

Let's assume that our example from before throws a `UserNotFoundException` while the user is not found in the database.
It will eventually return the user when the message is processed by the system.

In this scenario, we can explicitly skip the exception that we expect to happen until the test passed, but any other exceptions would
not be ignored. Note, this example is similar to the former, but if there was some other error, say a ConnectionException for example, this would cause
the eventually block to immediately exit with a failure message.


```kotlin
class MyTests : ShouldSpec() {
  init {
    should("check if user repository has one row") {
      eventually(5.seconds, UserNotFoundException::class.java) {
        userRepository.findBy(1) shouldNotBe null
      }
    }
  }
}
```



#### Predicates

In addition to verifying a test case eventually runs without throwing, we can also verify the result and treat a non-throwing result as failing.

```kotlin
class MyTests : StringSpec({
  "check that predicate eventually succeeds in time" {
    var i = 0
    eventually<Int>(25.seconds, predicate = { it == 5 }) {
      delay(1.seconds)
      i++
    }
  }
})
```

#### Sharing configuration

Sharing the configuration for eventually is a breeze with the `Eventually` data class. Suppose you have classified the operations in your
system to "slow" and "fast" operations. Instead of remembering which timing values were for slow and fast we can set up some objects to share between tests
and customize them per suite. This is also a perfect time to show off the listener capabilities of `eventually` which give you insight
into the current value of the result of your producer and the state of iterations!

```kotlin
val slow = EventuallyConfig<ServerResponse, ServerException>(5.minutes, interval = 25.milliseconds.fibonacci(), exceptionClass = ServerException::class)
val fast = slow.copy(duration = 5.seconds)

class FooTests : StringSpec({
  val logger = logger("FooTests")
  val fSlow = slow.copy(listener = { i, t -> logger.info("Current $i after {${t.times} attempts")})

  "server eventually provides a result for /foo" {
    eventually(fSlow) {
      fooApi()
    }
  }
})

class BarTests : StringSpec({
  val logger = logger("BarTests")
  val bFast = fast.copy(listener = { i, t -> logger.info("Current $i after {${t.times} attempts")})

  "server eventually provides a result for /bar" {
    eventually(bFast) {
      barApi()
    }
  }
})

```

Here we can see sharing of configuration can be useful to reduce duplicate code while allowing flexibility for things like
custom logging per test suite for clear test logs.
