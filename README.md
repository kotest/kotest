![KotlinTest](doc/logo4.png)
==========

[![Build Status](https://travis-ci.org/kotlintest/kotlintest.svg?branch=master)](https://travis-ci.org/kotlintest/kotlintest) 
[![Build status](https://ci.appveyor.com/api/projects/status/sr26tg49fk66yd82?svg=true)](https://ci.appveyor.com/project/sksamuel/kotlintest)
[<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest-core.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotlintest) [![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)]()
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotlintest/kotlintest-core.svg?label=latest%20snapshot&style=plastic"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotlintest/)

__KotlinTest is a flexible and comprehensive testing tool for [Kotlin](https://kotlinlang.org/).__  
[Full documentation](doc/reference.md)


For latest updates see [Changelog](CHANGELOG.md)

Community
---------
* [Stack Overflow](http://stackoverflow.com/questions/tagged/kotlintest) (don't forget to use the tag "kotlintest".)
* [KotlinTest channel](https://kotlinlang.slack.com/messages/kotlintest) in the Kotlin Slack
* [Contribute](https://github.com/kotlintest/kotlintest/wiki/contribute)

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

KotlinTest comes with several [testing styles](doc/reference.md#testing-styles) so you can choose one that fits your needs.

Multitude of Matchers
---------------------

Use over 120 provided matchers to test assertions on many different types:

```kotlin
"substring".shouldContain("str")

user.email.shouldBeLowerCase()

myImageFile.shouldHaveExtension(".jpg")

cityMap.shouldContainKey("London")
```

The `withClue` and `asClue` helpers can add extra context to assertions so failures are self explanatory:

```kotlin
withClue("Name should be present") { user.name shouldNotBe null }

data class HttpResponse(val status: Int, body: String)
val response = HttpResponse(200, "the content")
response.asClue {
    it.status shouldBe 200
    it.body shouldBe "the content"
}
```

Nesting is allowed in both cases and will show all available clues.

Matchers are extension methods and so your IDE will auto complete. See the [full list of matchers](doc/matchers.md) or write your own.

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

Check all the Tricky Cases With Data Driven Testing
--------------------------

Handle even an enormous amount of input parameter combinations easily with [data driven tests](doc/reference.md#table-driven-testing):

```kotlin
class StringSpecExample : StringSpec({
  "maximum of two numbers" {
    forall(
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

Testing for [exceptions](doc/reference.md#exceptions) is easy with KotlinTest:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
exception.message should startWith("Something went wrong")
```

Fine Tune Test Execution
------------------------

You can specify the number of invocations, parallelism, and a timeout for each test or for all tests.
And you can group tests by tags or disable them conditionally.
All you need is [`config`](doc/reference.md#config):

```kotlin
class MySpec : StringSpec() {

  override val defaultTestCaseConfig = TestCaseConfig(invocations = 3)

  init {
    "should use config".config(timeout = 2.seconds, invocations = 10, threads = 2, tags = setOf(Database, Linux)) {
      // ...
    }
  }
}
```

And More ...
------------

This page gives you just a short overview of KotlinTest. There are many more features:

* Test whole collections with [Inspectors](doc/reference.md#inspectors).
* Write elegant conditions with the [matcher DSL](doc/reference.md#matchers-and-assertions): `"hello".shouldHaveSubstring("ell")`.
* Reuse test logic for setup or tear down, with [Listeners](doc/reference.md#listeners).
* Test asynchronous code with [`whenReady`](doc/reference.md#whenReady) and non-deterministic code with [`eventually`](doc/nondeterministic.md) or [`continually`](doc/nondeterministic.md)
* Let KotlinTest [close resources automatically](doc/reference.md#autoclose): `val reader = autoClose(StringReader("xyz"))`
* Handle tricky scenarios such as System Environment with [extensions](doc/extensions.md)
* Use the [Spring extension](doc/extensions.md#Spring) to automatically inject your spring test classes.
* Test [Arrow](doc/extensions.md#Arrow) data types with the Arrow extension.

See [full documentation](doc/reference.md).

Use
---

#### Gradle

To use in gradle, configure your build to use the [JUnit Platform](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle). For Gradle 4.6 and higher this is
 as simple as adding `useJUnitPlatform()` inside the `test` block and then adding the KotlinTest dependency.

```groovy
test {
  useJUnitPlatform()
}

dependencies {
  testImplementation 'io.kotlintest:kotlintest-runner-junit5:3.3.2'
}
```

or with Gradle Kotlin DSL

```kotlin
val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

dependencies {
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}
```

or with gradle and Android

```groovy
android {
...
    testOptions {
        unitTests.all {
            useJUnitPlatform()
        }
    }
}

dependencies {
  testImplementation 'io.kotlintest:kotlintest-runner-junit5:3.3.2'
}
```

#### Maven

For maven you must configure the surefire plugin for junit tests.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
</plugin>
```

And then add the KotlinTest JUnit5 runner to your build.

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest-runner-junit5</artifactId>
    <version>3.3.2</version>
    <scope>test</scope>
</dependency>
```
