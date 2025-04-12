---
id: instant
title: Current Instant Listeners
sidebar_label: Current Instant Listeners
slug: instant.html
---


### Current instant listeners

Sometimes you may want to use the `now` static functions located in `java.time` classes for multiple reasons, such as setting the creation date of an entity

`data class MyEntity(creationDate: LocalDateTime = LocalDateTime.now())`.

But what to do when you want to test that value? `now` will be different
each time you call it!

For that, Kotest provides `ConstantNowListener` and `withConstantNow` functions.

While executing your code, your `now` will always be the value that you want to test against.

```kotlin
val foreverNow = LocalDateTime.now()

withConstantNow(foreverNow) {
  LocalDateTime.now() shouldBe foreverNow
  delay(10) // Code is taking a small amount of time to execute, but `now` changed!
  LocalDateTime.now() shouldBe foreverNow
}

```

Or, with a listener for all the tests:

```kotlin
  override fun listeners() = listOf(
    ConstantNowTestListener(foreverNow)
  )
```

:::caution
`withContantNow` and `ConstantNowTestListener` are very sensitive to race conditions. Using them, mocks the static method `now` which is global to the whole JVM instance,
if you're using it while running test in parallel, the results may be inconsistent.
:::




