---
id: eventually
title: Eventually
slug: eventually.html
---

:::note New improved module
Starting with Kotest 5.7, the non-deterministic testing functions have moved to the `kotest-assertions-core` module, and
are available under the new package `io.kotest.assertions.nondeterministic`. The previous iterations of these
functions are still available, but deprecated.
:::

Testing non-deterministic code can be hard. You might need to juggle threads, timeouts, race conditions, and the
unpredictability of when events are happening.

For example, if you were testing that an asynchronous file write was completed successfully, you need to wait until the
write operation has completed and flushed to disk.

Some common approaches to these problems are:

* Using callbacks which are invoked once the operation has completed. The callback can be then used to assert that the
  state of the system is as we expect. But not all operations provide callback functionality.

* Block the thread using `Thread.sleep` or suspend a function using `delay`, waiting for the operation to complete.
  The sleep threshold needs to be set high enough to be sure the operations will have completed on a fast or slow
  machine. Plus it means that your test will sit around waiting on the timeout even if
  the code completes quickly on a fast machine.

* Use a loop with a sleep and retry and a sleep and retry, but then you need to write boilerplate to track number of
  iterations, handle certain exceptions and fail on others, ensure the total time taken has not exceeded the max and so
  on.

* Use countdown latches and block threads until the latches are released by the non-determistic operation. This can
  work well if you are able to inject the latches in the appropriate places, but just like callbacks, it isn't always
  possible to have the code to be tested integrate with a latch.

As an alternative to the above solutions, kotest provides the `eventually` function which solves the common use case of
_"**I expect this code to pass after a short period of time**"_.

Eventually works by periodically invoking a given lambda, ignoring specified exceptions, until the lambda passes, or a
timeout is reached, or too many
iterations have passed. This is flexible and is perfect for testing nondeterministic code. Eventually can be customized
with regards to the types of exceptions to handle, how the lambda is considered a success or failure, with a listener,
and so on.

## API

There are two ways to use eventually. The first is simply providing a duration, using the Kotlin `Duration` type,
followed by the code that should eventually pass without an exception being raised.

For example:

```kotlin
eventually(5.seconds) {
  userRepository.getById(1).name shouldBe "bob"
}
```

The second is by providing a config block. This method should be used when you need to
set more options than just the duration. It also allows the config to be shared between multiple invocations of
eventually.

For example:

```kotlin
val config = eventuallyConfig {
  duration = 1.seconds
  interval = 100.milliseconds
}

eventually(config) {
  userRepository.getById(1).name shouldBe "bob"
}
```

## Configuration Options

### Durations and Intervals

The duration is the total amount of time to keep trying to pass the test. The `interval` allows us to
specify how often the test should be attempted. So if we set duration to 5 seconds, and interval to 250 millis,
then the test would be attempted at most `5000 / 250 = 20` times.

```kotlin
val config = eventuallyConfig {
  duration = 5.seconds
  interval = 250.milliseconds
}
```

Alternatively, rather than specifying the interval as a fixed number, we can pass in a function. This allows us to
perform some kind of backoff, or anything else we need.

For example, to use a fibonacci increasing interval, starting with 100ms:

```kotlin
val config = eventuallyConfig {
  duration = 5.seconds
  intervalFn = 100.milliseconds.fibonacci()
}
```

### Initial Delay

Usually `eventually` starts executing the test block immediately, but we can add an initial delay before the first
iteration using `initialDelay`, such as:

```kotlin
val config = eventuallyConfig {
  initialDelay = 1.seconds
}
```

### Retries

In addition to bounding the number of invocations by time, we can do so by iteration count. In the following example
we retry the operation 10 times, or until 8 seconds has expired.

```kotlin
val config = eventuallyConfig {
  initialDelay = 8.seconds
  retries = 10
}

eventually(config) {
  userRepository.getById(1).name shouldBe "bob"
}
```

### Specifying the exceptions to trap

By default, `eventually` will ignore any `AssertionError` that is thrown inside the function (note, that means it won't
catch `Error`). If you want to be more specific, you can tell `eventually` to ignore specific exceptions and any others
will immediately fail the test. We call these exceptions, the _expected exceptions_.

For example, when testing that a user should exist in the database, a `UserNotFoundException` might be thrown
if the user does not exist. We know that eventually that user will exist. But if an `IOException` is thrown, we don't
want to keep retrying as this indicates a larger issue than simply timing.

We can do this by specifying that `UserNotFoundException` is an exception to suppress.

```kotlin
val config = eventuallyConfig {
  duration = 5.seconds
  expectedExceptions = setOf(UserNotFoundException::class)
}

eventually(config) {
  userRepository.getById(1).name shouldBe "bob"
}
```

As an alternative to passing in a set of exceptions, we can provide a function which is invoked, passing in the throw
exception. This function should return true if the exception should be ignored, or false if the exception should bubble
out.

```kotlin
val config = eventuallyConfig {
  duration = 5.seconds
  expectedExceptions = { it is UserNotFoundException }
}

eventually(config) {
  userRepository.getById(1).name shouldBe "bob"
}
```

### Listeners

We can attach a listener, which will be invoked on each iteration, with the current iteration count and the
exception that caused the iteration to fail. Note: The listener will not be fired on a successful invocation.

```kotlin
val config = eventuallyConfig {
  duration = 5.seconds
  listener = { k, throwable -> println("Iteration $k, with cause $throwable") }
}

eventually(config) {
  userRepository.getById(1).name shouldBe "bob"
}
```

### Sharing configuration

Sharing the configuration for eventually is a breeze with the `eventuallyConfig` builder.
Suppose you have classified the operations in your system to "slow" and "fast" operations. Instead of remembering
which timing values were for slow and
fast we can set up some objects to share between tests and customize them per suite. This is also a perfect time to show
off the listener capabilities of `eventually` which give you insight into the current value of the result of your
producer and the state of iterations!

```kotlin
val slow = eventuallyConfig {
  duration = 5.minutes
  interval = 25.milliseconds.fibonacci()
  listener = { i, t -> logger.info("Current $i after {${t.times} attempts") }
}

val fast = slow.copy(duration = 5.seconds)

class FooTests : FunSpec({
  test("server eventually provides a result for /foo") {
    eventually(slow) {
      fooApi()
    }
  }
})

class BarTests : FunSpec({
  test("server eventually provides a result for /bar") {
    eventually(fast) {
      barApi()
    }
  }
})
```
