---
id: home
---

<img src="/img/logo_with_text.png" alt="kotest logo" width="700"/>


<img src="/img/intro_gif.gif"/>

**Kotest is a flexible and comprehensive testing project for Kotlin with multiplatform support.**

For latest updates see [Changelog](changelog.md).<br/>
See our [quick start](quick_start.mdx) guide to get up and running.

[![GitHub stars](https://img.shields.io/github/stars/kotest/kotest.svg?style=social&label=Star&maxAge=2592000)](https://GitHub.com/kotest/kotest/stargazers/)
[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-framework-api-jvm.svg?label=latest%20release"/>](https://search.maven.org/search?q=g:io.kotest)
![GitHub](https://img.shields.io/github/license/kotest/kotest)
[![kotest @ kotlinlang.slack.com](https://img.shields.io/static/v1?label=kotlinlang&message=kotest&color=blue&logo=slack)](https://kotlinlang.slack.com/archives/CT0G9SD7Z)

Community
---------
* [Stack Overflow](https://stackoverflow.com/questions/tagged/kotest) (don't forget to use the tag "kotest".)
* [Kotest channel](https://kotlinlang.slack.com/messages/kotest) in the Kotlin Slack (get an invite [here](https://slack.kotlinlang.org/))
* [Contribute](https://github.com/kotest/kotest/wiki/contribute)

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

Kotest comes with several [testing styles](framework/styles.md) so you can choose one that fits your needs.

Multitude of Matchers
---------------------

Use over 300 provided matchers to test assertions on many different types:

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

Matchers are extension methods and so your IDE will auto complete. See the [full list of matchers](assertions/matchers.md) or write your own.

Let the Computer Generate Your Test Data
----------------------------------------

Use [property based testing](framework/index.md) to test your code with automatically generated test data:

```kotlin
class PropertyExample: StringSpec({
  "String size" {
    checkAll<String, String> { a, b ->
      (a + b) shouldHaveLength a.length + b.length
    }
  }
})
```

Check all the Tricky Cases With Data Driven Testing
--------------------------

Handle even an enormous amount of input parameter combinations easily with [data driven tests](framework/data_driven_testing.md):

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

Testing for [exceptions](assertions/index.md#exceptions) is easy with Kotest:

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
All you need is [`config`](framework/project_config.md):

```kotlin
class MySpec : StringSpec({
  "should use config".config(timeout = 2.seconds, invocations = 10, threads = 2, tags = setOf(Database, Linux)) {
    // test here
  }
})
```

