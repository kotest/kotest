![KotlinTest](doc/logo.png)
==========

[![Join the chat at https://gitter.im/kotlintest/lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kotlintest/lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/kotlintest/kotlintest.svg?branch=master)](https://travis-ci.org/kotlintest/kotlintest) [<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotlintest) [![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)]()

__KotlinTest is a flexible and comprehensive testing tool for [Kotlin](https://kotlinlang.org/).__  
[Full documentation](doc/reference.md)

For latest updates see [Changelog](CHANGELOG.md)

Community
---------
* [Forum](https://groups.google.com/forum/#!forum/kotlintest)
* [Stack Overflow](http://stackoverflow.com/questions/tagged/kotlintest) (don't forget to use the tag "kotlintest".)
* [Contribute](https://github.com/kotlintest/kotlintest/wiki/contribute)

How to use
----------
For latest updates see [Changelog](CHANGELOG.md).



Test with Style
---------------

Write simple and beautiful tests with the StringSpec style:

```kotlin
class MyTests : StringSpec() {
  init {

    "length should return size of string" {
      "hello".length shouldBe 5
    }

  }
}
```

You can choose the [testing style](doc/reference.md#styles) that fits your needs.

Let the Computer Generate Your Test Data
----------------------------------------

Use [property based testing](doc/reference.md#property-based) to test your code with automatically generated test data:

```kotlin
class PropertyExample: StringSpec() {
  init {
    "String size" {
      forAll { a: String, b: String ->
        (a + b).length == a.length + b.length
      }
    }
}
```

Check all the Tricky Cases With Table Testing
--------------------------

Handle even an enormous amount of input parameter combinations easily with [table driven tests](doc/reference.md#table):

```kotlin
class StringSpecExample : StringSpec() {
  init {

    "should add" {
       val myTable = table(
         headers("a", "b", "result"),
         row(1, 2, 3),
         row(1, 1, 2)
       )
       forAll(myTable) { a, b, result ->
         a + b shouldBe result
       }
    }

  }
}
```

Test Exceptions
---------------

Testing for [exceptions](doc/reference.md#exceptions) is easy with KotlinTest:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
exception.message should startWith("Something went wrong")
```

Fine Tune Test Execution
------------------------

You can specify the number of threads, invocations, and a timeout for each test or for all tests. And you can group tests by tags or disable them conditionally. All you need is [`config`](doc/reference.md#config):

```kotlin
class MySpec : StringSpec() {

  override val defaultTestCaseConfig = TestCaseConfig(invocations = 3)

  init {
    "should use config" {
      // ...
    }.config(timeout = 2.seconds, invocations = 10, threads = 2, tags = setOf(Database, Linux))
  }
}
```

And More ...
------------

This page gives you just a short overview over KotlinTest. There are some more useful things:

* Check whole collections with [Inspectors](doc/reference.md#inspectors).
* Write elegant conditions with the [matcher DSL](doc/reference.md#matchers): `"hello" should haveSubstring("ell")`.
* Reuse test logic, e. g. for setup or tear down, with [Interceptors](doc/reference.md#interceptors).
* Let KotlinTest [close resources automatically](doc/reference.md#autoclose): `val reader = autoClose(StringReader("xyz"))`
* Test asynchronous code with [`eventually`](doc/reference.md#eventually).

See [full documentation](doc/reference.md).

Use
---

Gradle:
```
testCompile 'io.kotlintest:kotlintest:xxx'
```

Maven:

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest</artifactId>
    <version>xxx</version>
    <scope>test</scope>
</dependency>
```
