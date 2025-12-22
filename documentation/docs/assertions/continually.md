---
id: continually
title: Continually
slug: continually.html
---




As the dual of eventually, `continually` allows you to assert that a block of code succeeds, and continues to succeed, for a period of time.
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

By default, the function passed to the `continually` block is executed every 25 milliseconds. We can explicitly set the poll interval. In the following example we set it to 50 milliseconds:

```kotlin
class MyTests: ShouldSpec() {
  init {
    should("pass for 60 seconds") {

     val config = continuallyConfig<Unit> {
        duration = 60.seconds
        intervalFn = DurationFn { 50.milliseconds }
     }

      continually(config) {
        // code here that should succeed and continue to succeed for 60 seconds
      }
    }
  }
}
```
