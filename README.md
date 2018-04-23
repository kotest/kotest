![KotlinTest](doc/logo4.png)
==========

[![Join the chat at https://gitter.im/kotlintest/lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kotlintest/lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/kotlintest/kotlintest.svg?branch=master)](https://travis-ci.org/kotlintest/kotlintest) 
[![Build status](https://ci.appveyor.com/api/projects/status/sr26tg49fk66yd82?svg=true)](https://ci.appveyor.com/project/sksamuel/kotlintest)
[<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest-core.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotlintest) [![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)]()

__KotlinTest is a flexible and comprehensive testing tool for [Kotlin](https://kotlinlang.org/).__  
[Full documentation](doc/reference.md)


For latest updates see [Changelog](CHANGELOG.md)

Community
---------
* [Forum](https://groups.google.com/forum/#!forum/kotlintest)
* [Stack Overflow](http://stackoverflow.com/questions/tagged/kotlintest) (don't forget to use the tag "kotlintest".)
* [Contribute](https://github.com/kotlintest/kotlintest/wiki/contribute)

Test with Style
---------------

Write simple and beautiful tests with the StringSpec style:

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

You can choose the [testing style](doc/reference.md#styles) that fits your needs.

Multitude of Matchers
---------------------

Use over 100 provided matchers to test assertions on many different types:

```kotlin
"substring" should include("str")

user.email should beLowerCase()

myImmgeFile should haveExtension(".jpg")

cityMap should haveKey("London")
```

See the [full list of matchers](doc/matchers.md) or write your own.

Let the Computer Generate Your Test Data
----------------------------------------

Use [property based testing](doc/reference.md#property-based) to test your code with automatically generated test data:

```kotlin
class PropertyExample: StringSpec() {
  init {
    "String size" {
      assertAll { a: String, b: String ->
        (a + b) should haveLength(a.length + b.length)
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

You can specify the number of threads, invocations, and a timeout for each test or for all tests. And you can group tests by tags or disable them conditionally. 
All you need is [`config`](doc/reference.md#config):

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
* Reuse test logic, e. g. for setup or tear down, with [Listeners](doc/reference.md#listeners).
* Let KotlinTest [close resources automatically](doc/reference.md#autoclose): `val reader = autoClose(StringReader("xyz"))`
* Test asynchronous code with [`whenReady`](doc/reference.md#whenReady) and [`eventually`](doc/reference.md#eventually).
* Use the [Spring extension](doc/reference.md#spring) to automatically inject your spring test classes.

See [full documentation](doc/reference.md).

Use
---

Gradle:
```
buildscript {
    dependencies {
        classpath "org.junit.platform:junit-platform-gradle-plugin:1.1.0"
    }
}
// This allows gradle to execute _jUnit-platform-5_ based tests (which KotlinTest builds upon). 
// Note: Gradle says that this is **not** required as of 4.6 but even with 4.6 it seems to be required.
apply plugin: 'org.junit.platform.gradle.plugin'

testCompile 'io.kotlintest:kotlintest-runner-junit5:3.0.4'
```

Maven:

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest-runner-junit5</artifactId>
    <version>3.0.4</version>
    <scope>test</scope>
</dependency>
```
