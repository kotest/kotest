---
id: retry
title: Retry
slug: retry.html
---


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

