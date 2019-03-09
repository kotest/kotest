Non-deterministic Tests
===================

Sometimes you have to work with code that are non-deterministic in nature. This is never ideal, but if you have no choice then
KotlinTest has this covered with two functions called `eventually` and `continually`.

Eventually <a name="eventually"></a>
------------------------

When testing non-deterministic code, it's handy to be able to say "I expect these assertions to pass within a period of time".
Sometimes you can do a Thread.sleep but this is bad as you need to set a timeout that's high enough so that it won't expire prematurely on a slow machine.
Plus it means that your test will sit around waiting on the timeout even if the code completes quickly on a fast machine.

Another common approach is to use countdown latches and this works fine if you are able to inject the latches in the appropriate places.

As an alternative, KotlinTest provides the `eventually` function which will repeatedly
test the code until it either passes, or the timeout is reached. This is perfect for nondeterministic code. For example:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("do something") {
      eventually(5.seconds) {
        // code in here that might fail at first, but will succeed within the given duration of 5 seconds.
      }
    }
  }
}
```

Continually <a name="eventually"></a>
-------------------------------

As the dual of eventually, `continually` allows you to assert that a block of code suceeds, and continues to succeed, for a period of time.
For example you may want to check that a http connection is kept alive for 60 seconds after the last packet has been received.
You could sleep for 60 seconds, and then check, but if the connection was terminated after 5 seconds, your test will sit idle for a further 55 seconds before then failing.
Better to fail fast.

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("do something") {
      continually(60.seconds) {
        // code here that should succeed and continue to succeed for 60 seconds
      }
    }
  }
}
```