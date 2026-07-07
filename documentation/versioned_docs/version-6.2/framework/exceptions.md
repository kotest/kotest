---
id: exceptions
title: Testing Exceptions
sidebar_label: Exceptions
slug: exceptions.html
---


Testing for [exceptions](https://kotest.io/docs/assertions/exceptions.html) is easy with Kotest:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
   // code in here that you expect to throw an IllegalAccessException
}
exception.message should startWith("Something went wrong")
```
