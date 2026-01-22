---
id: conditional_exceptions
title: Conditional Exceptions
slug: conditional-exceptions.html
sidebar_label: Exceptions
---


Kotest supports skipping a test by throwing an exception during test execution. Depending on the platform, there are two
ways to do this.

Firstly, `io.kotest.engine.TestAbortedException` can be thrown to skip a test after the test has been invoked.
The test will then be marked as ignored in the test report. This exception is available on all platforms.

Secondly, the exception types supplied by the [opentest4j](https://github.com/ota4j-team/opentest4j) project are
supported by Kotest. These are `org.opentest4j.TestAbortedException` and `org.opentest4j.TestSkippedException`.
While these convey different semantics, they are treated the same by Kotest, and both of these will result in the test
being marked as ignored in the test report. These exceptions are only available on JVM.

For example:

```kotlin
class MySkippedExample: FreeSpec() {
   init {
      "this test will be skipped" {
         throw TestAbortedException()
      }
   }
}
```
