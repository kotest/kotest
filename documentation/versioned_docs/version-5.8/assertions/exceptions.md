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

If you want to test that a specific type of exception is thrown, then use `shouldThrowExactly<E>`. For example, the
following block would catch a `FileNotFoundException` but not a `IOException` even though `FileNotFoundException`
extends from `IOException`.

```kotlin
val exception = shouldThrowExactly<FileNotFoundException> {
  // test here
}
```

If you simply want to test that _any_ exception is thrown, regardles of type, then use `shouldThrowAny`.


```kotlin
val exception = shouldThrowAny {
  // test here can throw any type of Throwable!
}
```


