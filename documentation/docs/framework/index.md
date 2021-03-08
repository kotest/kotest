---
id: index
title: Test Framework
slug: framework.html
---

![intro_gif](../images/intro_gif.gif)

[![version badge](https://img.shields.io/maven-central/v/io.kotest/kotest-framework-engine.svg?label=release)](https://search.maven.org/search?q=kotest)
[![version badge](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot)](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/)

Test with Style
---------------

Write simple and beautiful tests using one of the available styles:

```kotlin
class MyTests : StringSpec({
   "length should return size of string" {
      "hello".length shouldBe 5
   }
   "startsWith should test for a prefix" {
      "world" should startWith("wor")
   }
})
```

Kotest comes with several [testing styles](styles.md) so you can choose one that fits your needs.

Check all the Tricky Cases With Data Driven Testing
--------------------------

Handle even an enormous amount of input parameter combinations easily with [data driven tests](data_driven_testing.md):

```kotlin
class StringSpecExample : StringSpec({
   "maximum of two numbers" {
      forAll(
         row(1, 5, 5),
         row(1, 0, 1),
         row(0, 0, 0)
      ) { a, b, max ->
         Math.max(a, b) shouldBe max
      }
   }
})
```



Fine Tune Test Execution
------------------------

You can specify the number of invocations, parallelism, and a timeout for each test or for all tests. And you can group
tests by tags or disable them conditionally. All you need is [`config`](project_config.md):

```kotlin
class MySpec : StringSpec({
   "should use config".config(timeout = 2.seconds, invocations = 10, threads = 2, tags = setOf(Database, Linux)) {
      // test here
   }
})
```

