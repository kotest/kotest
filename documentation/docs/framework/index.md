---
id: index
title: Test Framework
slug: framework.html
---

![intro_gif](../images/intro_gif.gif)


Test with Style
---------------

Write simple and beautiful tests with the `StringSpec` style:

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

Test Exceptions
---------------

Testing for [exceptions](assertions.md#exceptions) is easy with Kotest:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
   // code in here that you expect to throw an IllegalAccessException
}
exception.message should startWith("Something went wrong")
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

And More ...
------------

This page gives you just a short overview of Kotest. There are many more features:

* Reuse test logic for setup or tear down, with [Listeners](listeners.md).
* Let Kotest [close resources automatically](doc/reference.md#autoclose): `val reader = autoClose(StringReader("xyz"))`
* Create reusable parameterized tests via [test factories](test_factories.md)
* Handle tricky scenarios such as System Environment with [extensions](extensions.md)
* Use the [Spring extension](extensions.md#spring) to automatically inject your spring test classes.
* Test [Arrow](extensions.md#arrow) data types with the Arrow extension.
* Make use of custom plugins for integrations with tools such as [Pitest](plugins.md#pitest)
