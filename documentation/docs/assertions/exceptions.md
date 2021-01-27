---
id: exceptions
title: Exceptions
slug: exceptions.html
---




To assert that a given block of code throws an exception, one can use the `shouldThrow` function. Eg,

```kotlin
shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
```

You can also check the caught exception:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
exception.message should startWith("Something went wrong")
```

If you want to test that _exactly_ a type of exception is thrown, then use `shouldThrowExactly<E>`.
If you want to test that _any_ exception is thrown, then use `shouldThrowAny`.




