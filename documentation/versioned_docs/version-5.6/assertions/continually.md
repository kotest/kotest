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
