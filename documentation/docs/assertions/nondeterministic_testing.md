---
id: nondeterministic
title: Non-deterministic Testing
slug: non-deterministic-testing.html
---



Sometimes you have to work with code that is non-deterministic in nature. This is never the preferred scenario, but if you have no choice then
Kotest has you covered with several functions that cater to tests which may take some time to pass.

## Eventually <a name="eventually"></a>

When testing non-deterministic code, a common use case is "I expect this code to pass after a short period of time". For example, if you
were testing a IO operation, you might need to wait until the IO operation has flushed.

Sometimes you can do a Thread.sleep but this is isn't ideal as you need to set a sleep threshold high enough so that it won't expire prematurely on a slow machine.
Plus it means that your test will sit around waiting on the timeout even if the code completes quickly on a fast machine.

Or you can roll a loop and sleep and retry and sleep and retry, but this is just boilerplate slowing you down.

Another common approach is to use countdown latches and this works fine if you are able to inject the latches in the appropriate places but it isn't always
possible to have the code under test trigger a latch.

As an alternative, kotest provides the `eventually` function and the `Eventually` configuration which periodically test the code
ignoring your specified exceptions and ensuring the result satisfies an optional predicate, until the timeout is eventually reached or
too many iterations have passed. This is flexible and is perfect for testing nondeterministic code.

### Examples


#### Simple examples

Let's assume that we send a message to an asynchronous service. After the message is processed, a new row is inserted into user table.

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

By default, `eventually` will ignore any exception that is thrown inside the function (note, that means it won't catch `Error`).
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

## Continually <a name="continually"></a>

As the dual of eventually, `continually` allows you to assert that a block of code suceeds, and continues to succeed, for a period of time.
For example you may want to check that a http connection is kept alive for 60 seconds after the last packet has been received.
You could sleep for 60 seconds, and then check, but if the connection was terminated after 5 seconds, your test will sit idle for a further 55 seconds before then failing.
Better to fail fast.

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("pass for 60 seconds") {
      continually(60.seconds) {
        // code here that should succeed and continue to succeed for 60 seconds
      }
    }
  }
}
```

The function passed to the `continually` block is executed every 10 milliseconds. We can specify the poll interval if we prefer:

```kotlin
class MyTests: ShouldSpec() {
  init {
    should("pass for 60 seconds") {
      continually(60.seconds, 5.seconds) {
        // code here that should succeed and continue to succeed for 60 seconds
      }
    }
  }
}
```

## Retry <a name="retry"></a>

Retry is similar to eventually, but rather than attempt a block of code for a period of time, it attempts a block of code a maximum number of times.
We still provide a timeout period to avoid the loop running for ever.

```kotlin
class MyTests: ShouldSpec() {
  init {
    should("retry up to 4 times") {
      retry(4, 10.minutes) {
      }
    }
  }
}
```

Additional options include the delay between runs, a multiplier to use exponential delays, and an exception class if we only want to
repeat for certain exceptions and fail for others.

