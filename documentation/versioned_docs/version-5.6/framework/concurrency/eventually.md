---
id: eventually
title: Eventually
slug: eventually.html
---

:::note New improved module
Starting with Kotest 4.6, a new experimental module has been added which contains improved
utilities for testing concurrent, asynchronous, or non-deterministic code. This module
is `kotest-framework-concurrency` and is intended as a long term replacement for the previous module. The previous
utilities are still available as part of the core framework.
:::

Testing non-deterministic code can be hard. You might need to juggle threads, timeouts, race conditions, and the
unpredictability of when events are happening.

For example, if you were testing that an asynchronous file write was completed successfully, you need to wait until the
write operation has completed and flushed to disk.

Some common approaches to these problems are:

* Using callbacks which are invoked once the operation has completed. The callback can be then used to assert that the
  state of the system is as we expect. But not all operations provide callback functionality.

* Block the thread using `Thread.sleep` or suspend a function using `delay`, waiting for the operation to complete.
  The sleep threshold needs to be set high enough to be sure the operations will have completed on a fast or slow machine, and
  even when complete, the thread will stay blocked until the timeout has expired.

* Use a loop with a sleep and retry and a sleep and retry, but then you need to write boilerplate to track number of
  iterations, handle certain exceptions and fail on others, ensure the total time taken has not exceeded the max and so
  on.

* Use countdown latches and block threads until the latches are released by the non-determistic operation. This can
  work well if you are able to inject the latches in the appropriate places, but just like callbacks, it isn't always
  possible to have the code to be tested integrate with a latch.

As an alternative to the above solutions, kotest provides the `eventually` utility which solves the common use case of
_"**I expect this code to pass after a short period of time**"_.

Eventually does this by periodically invoking a given lambda until the timeout is eventually reached or too many iterations have passed. This is
flexible and is perfect for testing nondeterministic code. Eventually can be customized in regardless to the types of
exceptions to handle, how the lambda is considered a success or failure, with a listener, and so on.

## API

There are two ways to use eventually. The first is simply providing a duration in either milliseconds
(or using the Kotlin `Duration` type) followed by the code that should eventually pass without an exception being raised.

```kotlin
eventually(5000) { // duration in millis
  userRepository.getById(1).name shouldBe "bob"
}
```

The second is by providing a configuration block before the test code. This method should be used when you need to
set more options than just the duration.

```kotlin
eventually({
  duration = 5000
  interval = 1000.fixed()
}) {
  userRepository.getById(1).name shouldBe "bob"
}
```

## Configuration

### Durations and Intervals

The duration is the total amount of time to keep trying to pass the test. The `interval` however allows us to
specify how often the test should be attempted. So if we set duration to 5 seconds, and interval to 250 millis,
then the test would be attempted at most `5000 / 250 = 20` times.

### Initial Delay

Usually `eventually` starts executing the test block immediately, but we can add an initial delay before the first
iteration using `initialDelay`, such as:

```kotlin
eventually({
  duration = 5000
  initialDelay = 1000
}) {
  userRepository.getById(1).name shouldBe "bob"
}
```


### Retries

In addition to bounding the number of invocations by time, we can do so by iteration count. In the following example
we retry the operation 10 times, or until 8 seconds has expired.

```kotlin
eventually({
  duration = 8000
  retries = 10
  suppressExceptions = setOf(UserNotFoundException::class)
}) {
  userRepository.getById(1).name shouldNotBe "bob"
}
```


### Specifying the exceptions to trap

By default, `eventually` will ignore any `AssertionError` that is thrown inside the function (note, that means it won't
catch `Error`). If you want to be more specific, you can tell `eventually` to ignore specific exceptions and any others
will immediately fail the test.

For example, when testing that a user should exist in the database, a `UserNotFoundException` might be thrown
if the user does not exist. We know that eventually that user will exist. But if an `IOException` is thrown, we don't
want to keep retrying as this indicates a larger issue than simply timing.

We can do this by specifying that `UserNotFoundException` is an exception to suppress.

```kotlin
eventually({
  duration = 8000
  suppressExceptions = setOf(UserNotFoundException::class)
}) {
  userRepository.getById(1).name shouldNotBe "bob"
}
```

As an alternative to passing in a set of exceptions, we can provide a function which is invoked, passing in the throw
exception. This function should return true if the exception should be handled, or false if the exception should bubble out.

```kotlin
eventually({
  duration = 8000
  suppressExceptionIf = { it is UserNotFoundException && it.username == "bob" }
}) {
  userRepository.getById(1).name shouldNotBe "bob"
}
```


### Predicates

In addition to verifying a test case eventually runs without throwing an exception, we can also verify that the
return value of the test is as expected - and if not, consider that iteration a failure and try again.

For example, here we continue to append "x" to a string until the result of the previous iteration is equal to "xxx".

```kotlin
var string = "x"

eventually({
  duration = 5.seconds()
  predicate = { it.result == "xxx" }
}) {
  string += "x"
  string
}
```

### Listeners

We can attach a listener, which will be invoked on each iteration, with the state of that iteration. The state object
contains the last exception, last value, iteration count and so on.


```kotlin
eventually({
  duration = 5.seconds()
  listener = { println("iteration ${it.times} returned ${it.result}") }
}) {
  string += "x"
  string
}
```


### Sharing configuration

Sharing the configuration for eventually is a breeze with the `EventuallyConfig` data class. Suppose you have classified the
operations in your system to "slow" and "fast" operations. Instead of remembering which timing values were for slow and
fast we can set up some objects to share between tests and customize them per suite. This is also a perfect time to show
off the listener capabilities of `eventually` which give you insight into the current value of the result of your
producer and the state of iterations!

```kotlin
val slow = EventuallyConfig<ServerResponse>(
  duration = 5.minutes,
  interval = 25.milliseconds.fibonacci(),
  suppressExceptions = setOf(ServerException::class)
)

class FooTests : StringSpec({
  val logger = logger("FooTests")
  val fSlow = slow.copy(listener = { i, t -> logger.info("Current $i after {${t.times} attempts") })

  "server eventually provides a result for /foo" {
    eventually(fSlow) {
      fooApi()
    }
  }
})

class BarTests : StringSpec({
  val logger = logger("BarTests")
  val bFast = fast.copy(listener = { i, t -> logger.info("Current $i after {${t.times} attempts") })

  "server eventually provides a result for /bar" {
    eventually(bFast) {
      barApi()
    }
  }
})

```

Here we can see sharing of configuration can be useful to reduce duplicate code while allowing flexibility for things
like custom logging per test suite for clear test logs.
