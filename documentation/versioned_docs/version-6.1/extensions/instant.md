---
id: instant
title: Current Instant Listeners
sidebar_label: Current Instant Listeners
slug: instant.html
---

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-now.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-now)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-now%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-now/maven-metadata.xml)

:::tip
Since Kotest 5.6.0, Current instant listeners are located in the artifact `io.kotest:kotest-extensions-now:${kotest-version}`.

Add it as a dependency to use any of the functionality mentioned below.
:::

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




