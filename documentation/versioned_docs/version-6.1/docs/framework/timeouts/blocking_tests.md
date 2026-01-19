---
id: blocking_tests
title: Blocking Tests
slug: blocking-tests.html
sidebar_label: Blocking Tests
---

When specifying timeouts in tests, Kotest uses the `withTimeout` coroutine functions that the Kotlin coroutine
library provides. These timeouts are co-operative in nature, and a timeout is detected when a coroutine suspends, resumes, or calls `yield`.

However when executing blocking code, the thread will be blocked and so the coperative approach will not work.
In this scenario we must revert to interrupting the thread using `Thread.interrupt` or something similar. In order
for this interruption to work safely, we must execute the test on a dedicated thread.

Therefore, it is up to the user to signify to Kotest that they want a particular test to execute on a dedicated
thread that can be safely used for interruption. We do this by enabling the `blockingTest` flag in test config.

For example:

```kotlin
class MyBlockingTest : FunSpec() {
  init {

    test("interrupt me!").config(blockingTest = true, timeout = 10.seconds) {
       Thread.sleep(100000000)
    }

    test("uses suspension").config(timeout = 10.seconds) {
      delay(100000000)
    }
  }
}
```

In the above example, the first test requires the `blockingTest` flag because it uses a thread blocking operation.
The second test does not because it uses a suspendable operation.

:::note
This feature is only available on the JVM.
:::

