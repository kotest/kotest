# Welcome to Kotest

![Kotest](images/logo_with_text.png)

For latest updates see [Changelog](CHANGELOG.md).<br/>
Read [full documentation](doc/reference.md) or a [quick start](doc/reference.md#getting-started) guide.

Community
---------
* [Stack Overflow](http://stackoverflow.com/questions/tagged/kotest) (don't forget to use the tag "kotest".)
* [Kotest channel](https://kotlinlang.slack.com/messages/kotest) in the Kotlin Slack (get an invite [here](http://slack.kotlinlang.org/))
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

Kotest comes with several [testing styles](doc/reference.md#testing-styles) so you can choose one that fits your needs.

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

Matchers are extension methods and so your IDE will auto complete. See the [full list of matchers](doc/matchers.md) or write your own.

Let the Computer Generate Your Test Data
----------------------------------------

Use [property based testing](doc/property_testing.md) to test your code with automatically generated test data:

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

Handle even an enormous amount of input parameter combinations easily with [data driven tests](doc/data_driven_testing.md):

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

Testing for [exceptions](doc/reference.md#exceptions) is easy with Kotest:

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
class MySpec : StringSpec({
  "should use config".config(timeout = 2.seconds, invocations = 10, threads = 2, tags = setOf(Database, Linux)) {
    // test here
  }
})
```

And More ...
------------

This page gives you just a short overview of Kotest. There are many more features:

* Test whole collections with [Inspectors](doc/reference.md#inspectors).
* Write elegant conditions with the [matcher DSL](doc/reference.md#matchers-and-assertions): `"hello".shouldHaveSubstring("ell")`.
* Reuse test logic for setup or tear down, with [Listeners](doc/reference.md#listeners).
* Test asynchronous code with [`whenReady`](doc/reference.md#whenReady) and non-deterministic code with [`eventually`](doc/nondeterministic.md) or [`continually`](doc/nondeterministic.md)
* Let Kotest [close resources automatically](doc/reference.md#autoclose): `val reader = autoClose(StringReader("xyz"))`
* Create reusable parameterized tests via [test factories](doc/reference.md#test-factories)
* Handle tricky scenarios such as System Environment with [extensions](doc/extensions.md)
* Use the [Spring extension](doc/extensions.md#Spring) to automatically inject your spring test classes.
* Test [Arrow](doc/extensions.md#Arrow) data types with the Arrow extension.
* Make use of custom plugins for integrations with tools such as [Pitest](doc/plugins.md#Pitest)

See our [getting started](doc/reference.md#getting-started) section or [full documentation](doc/reference.md).

Read more about Kotest from third party [blogs and articles](doc/blogs.md).
