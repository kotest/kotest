---
id: autoclose
title: Closing resources automatically
slug: autoclose.html
---


You can let Kotest close resources automatically after all tests have been run:

```kotlin
class StringSpecExample : StringSpec() {

  val reader = autoClose(StringReader("xyz"))

  init {
    "your test case" {
      // use resource reader here
    }
  }
}
```

Resources that should be closed this way must implement [`java.lang.AutoCloseable`](https://docs.oracle.com/javase/7/docs/api/java/lang/AutoCloseable.html). Closing is performed in
reversed order of declaration after the return of the last spec interceptor.


