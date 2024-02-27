---
id: until
title: Until
slug: until.html
---

When testing non-deterministic code, a common use case is "I expect this code to pass after a short period of time".

For example, you might want to test that a message has been received by a broker. You could setup a time limit,
and repeatedly poll until the message was received, but this would block the thread. Plus you would have to write
the loop code, adding boilerplate.

As an alternative, kotest provides the `until` function which will periodically execute a function until either that
function returns true, or the given duration expires.

Until is the predicate equivalent of [eventually](eventually.md).

### Duration

Let's say we have a function that polls a broker, and returns a list of messages. We want to test that when we
send a message the message is picked up by the broker within 5 seconds.

```kotlin
class MyTests : ShouldSpec() {

  private val broker = createBrokerClient()

  init {
    should("broker should receive a message") {
      sendMessage()
      until(5.seconds) {
        broker.poll().size > 0
      }
    }
  }
}
```

### Interval

By default, the predicate is checked every second. We can specify an interval which controls the delay between invocations.
Here is the same example again, this time with a more aggressive fixed 250 millisecond interval.

```kotlin
class MyTests : ShouldSpec() {

  private val broker = createBrokerClient()

  init {
    should("broker should receive a message") {
      sendMessage()
      until(5.seconds, 250.milliseconds.fixed()) {
        broker.poll().size > 0
      }
    }
  }
}
```

We can also specify a fibonacci interval, if we want to increase the delay after each failure.

```kotlin
class MyTests : ShouldSpec() {

  private val broker = createBrokerClient()

  init {
    should("broker should receive a message") {
      sendMessage()
      until(5.seconds, 100.milliseconds.fibonacci()) {
        broker.poll().size > 0
      }
    }
  }
}
```
