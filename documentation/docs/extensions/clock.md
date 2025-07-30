---
id: clock
title: Test Clock
sidebar_label: Test Clock
slug: test_clock.html
---

[![Latest Release](https://img.shields.io/maven-central/v/io.kotest/kotest-extensions)](https://search.maven.org/artifact/io.kotest/kotest-extensions)

The JVM provides the `java.time.Clock` interface which is used to generate `Instant`s. When we have code that relies on time,
we can use a `Clock` to generate the values, rather than using things like `Instant.now()` or `System.currentTimeMillis()`.

Then in tests we can provide a fixed or controllable clock which avoids issues where the time changes on each test run.
In your real code, you provide an instance of Clock.systemUTC() or whatever.

:::note
The following module is needed: `io.kotest:kotest-extensions` in your build. Search maven central for latest version [here](https://search.maven.org/search?q=kotest-extensions).
:::

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group, with version cadence tied to
main Kotest releases.
:::


In order to use it, we create an instance of the `TestClock` passing in an instant and a zone offset.

```
val timestamp = Instant.ofEpochMilli(1234)
val clock = TestClock(timestamp, ZoneOffset.UTC)
```

We can control the clock via `plus` and `minus` which accept durations, eg


```
clock.plus(6.minutes)
```

Note that the clock is mutable, and the internal state is changed when you use plus or minus.
