Non-deterministic Tests
===================

Sometimes you have to work with code that is non-deterministic in nature. This is never the preferred scenario, but if you have no choice then
Kotest has you covered with several functions that cater to tests which may take some time to pass.

Eventually <a name="eventually"></a>
------------------------

When testing non-deterministic code, a common use case is "I expect this code to pass after a short period of time". For example, if you
were testing a IO operation, you might need to wait until the IO operation has flushed.

Sometimes you can do a Thread.sleep but this is isn't ideal as you need to set a sleep threshold high enough so that it won't expire prematurely on a slow machine.
Plus it means that your test will sit around waiting on the timeout even if the code completes quickly on a fast machine.

Or you can roll a loop and sleep and retry and sleep and retry, but this is just boilerplate slowing you down.

Another common approach is to use countdown latches and this works fine if you are able to inject the latches in the appropriate places but it isn't always
possible to have the code under test trigger a latch.

### Usage

As an alternative, Kotest provides the `eventually` function which will periodically
test the code until it either passes, or the timeout is reached. This is perfect for nondeterministic code.

#### Simple example

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

By default, `eventually` will ignore any AssertionError thrown inside the function,
while any others will then immediately fail the test.

You can also tell `eventually` to ignore other exceptions:
Let's assume that our example from before throws a `UserNotFoundException` when the user is not found in the database.
It will eventually return the user when the message is processed by the system.
However, if there is another error, `eventually` will fail immediately.

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

**ATTENTION**: `eventually` currently does not works well within `assertSoftly`, since assertions aren't thrown normally in there:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("some test with eventually inside assert softly") {
      assertSoftly {
        eventually(1.seconds) {
          1 shouldBe 2
        }
        1 shouldBe 2 // Never gets executed
      }
    }
  }
}
```


Continually <a name="continually"></a>
-------------------------------

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



Retry <a name="retry"></a>
------------------------


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

