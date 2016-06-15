# kotlintest

[![Build Status](https://travis-ci.org/kotlintest/kotlintest.svg?branch=master)](https://travis-ci.org/kotlintest/kotlintest) [<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest*.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotlintest) [![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)]()

KotlinTest is a flexible and comprehensive testing tool for the [Kotlin](https://kotlinlang.org/) ecosystem based on and heavily inspired by the superb [Scalatest](http://www.scalatest.org/). KotlinTest provides several ways to lay out your test so that your team can pick the style they are most happy with. It also includes many matchers which allow you to write many different types of assertions easily and in a human readable way. Finally, there's helpers for things like collection testing, and future testing.

### How to use

KotlinTest is published to Maven Central, so to use, simply add the dependency in test scope to your build file. You can get the latest version from the little badge at the top of the readme.

Gradle:

    testCompile 'io.kotlintest:kotlintest:xxx'

Maven:

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest</artifactId>
    <version>xxx</version>
    <scope>test</scope>
</dependency>
```

### Testing Styles

You can choose a testing style by extending WordSpec, FunSpec, ShouldSpec, FlatSpec or FreeSpec in your test class, and writing your tests inside an init {} block. _In ScalaTest, the body of the class is the constructor, so you write tests directly in the class body. The KotlinTest equivalent is the init block._

```kotlin
class MyTests : WordSpec() {
  init {
    // tests here
  }
}
```

#### Flat Spec

Flat spec offers the keywords `should`, and `with`, and allows those to be used inline, as such:

```kotlin
class MyTests : FlatSpec() {
  init {
    "String.length" should "return the length of the string" {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

#### Fun Spec

Fun spec allows you to create tests similar to the junit style. You invoke a method called test, with a string parameter to describe the test, and then the test itself:

```kotlin
class MyTests : FunSpec() {
  init {
    test("String.length should return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

#### Should spec

Should spec is similar to fun spec, but uses the keyword `should` instead of `test`. Eg:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

This can be nested in context strings too, eg

```kotlin
class MyTests : ShouldSpec() {
  init {
    "String.length" {
      should("return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

#### Word Spec

Word spec uses the keyword `should` and uses that to nest test blocks after a context string, eg:

```kotlin
class MyTests : WordSpec() {
  init {
    "String.length" should {
      "return the length of the string" {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

#### Flat Spec

Flat spec allows you to nest arbitary levels of depth using the keywords `-` (minus), as such:

```kotlin
class MyTests : FlatSpec() {
  init {
    "String.length" - {
      "should return the length of the string" {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

### Matchers

KotlinTest has many built in matchers, along a similar line to the popular [hamcrest](http://hamcrest.org/) project. The simplest assertion is that a value should be equal to something, eg: `x shouldBe y` or `x shouldEqual y`. This will also work for null values, eg `x shouldBe null` or `y shouldEqual null`.

#### String Matchers

* To assert that a string starts with a given prefix use `x should start with y`.
* To assert that a string ends with a given suffix use `x should end with y`.
* To assert that a string contains a given substring use `x should have substring y`.
* To assert that a string matches a given regular expression, use `x should match("regex")`.

#### Long / Int Matchers

* To assert that a value is greater than a given value use `x should be gt y`. This is the same as doing `(x > y) shouldBe true`. Choose whatever style you prefer. The same goes for the other operators lt, gte, lte.

#### Collection Matchers

* To assert that a collection has a given size use `col should have size 4`. This is the same as `(col.size == 4) shouldBe true` but more readable.
* To assert that a collection contains a given element use `col should contain element x`.

### Exceptions

To assert that a given block of code throws an exception, one can use the expecting(exception) block. Eg,

```kotlin
expecting(IllegalAccessException::class) {
  // code in here that you expect to throw an IllegalAccessException
}
```

or since 1.1.1
```kotlin
shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
```

You can also check the caught exception:

```kotlin
val exception = shouldThrow<IllegalAccessException> {
  // code in here that you expect to throw an IllegalAccessException
}
exception.message should start with "Something went wrong"
```

### Before and After Each (since 1.1.0)

If you need to run a method before each test and/or after each test (perhaps to reset some values to defaults etc), then simply override the `beforeEach` and `afterEach` methods in your test class, eg:

```kotlin
override fun beforeEach() {
  println("Test starting")
}
```

```kotlin
override fun afterEach() {
  println("Test completed")
}
```


### Before and After All

If you need to run a setup/tear down function before and after all the tests have run, then simply override the `beforeAll` and `afterAll` methods in your test class, eg:

```kotlin
override fun beforeAll() {
  println("Setting up my tests")
}
```

```kotlin
override fun afterAll() {
  println("Cleaning up after my tests")
}
```

### One Instance Per Test (since 1.1.0)

By default a single instance of the test class is created for all the test it contains. However, if you wish to have a fresh instance per test (sometimes its easier to have setup code in the init block instead of resetting after each test) then simply override the `oneInstancePerTest` value and set it to true, eg:

```kotlin
class MyTests : ShouldSpec() {
  override val oneInstancePerTest = true
  init {
    // tests here
  }
}
```

### Testing Config (since 1.2.0)

Each test can be configured with various parameters. After the test block, invoke the config method passing in the parameters you wish to set. The available parameters are:

* `invocations` - the number of times to run this test. Useful if you have a non-deterministic test and you want to run that particular test a set number of times. Defaults to 1.
* `threads` - Allows the invocation of this test to be parallelized by setting the number of threads to use in a thread pool executor for this test. If invocations is 1 (the default) then this parameter will have no effect. Similarly, if you set invocations to a value less than or equal to the number threads, then each invocation will have its own thread.
* `ignored` - If set to true then this test is ignored. Can be useful if a test needs to be temporarily disabled.
* `timeout` - sets a timeout for this test. If the test has not finished in that time then the test fails. Useful for code that is non-deterministic and might not finish. Timeout is of type `Duration` which can be instantiated like `2.seconds`, `3.minutes` and so on.
* `tag` / `tags` - a list of String tags that can be set on a test. Then by invoking the test runner with a system property of testTags, you can control which tests are run. For example, tests that require a linux based O/S might be tagged with "linux" then gradle could be invoked with gradle test -DtestTags=linux. Another example might be tagging database tags that you only want to run on a server that has a database installed. Any test that has no tags is always run.

Examples of setting config:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }.config(invocations=10, threads=2)
  }
}
```

```kotlin
class MyTests : WordSpec() {
  init {
    "String.length" should {
      "return the length of the string" {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }.config(timeout = 2.seconds)
    }
  }
}
```

```kotlin
class FunSpecTest : FunSpec() {
  init {
    test("FunSpec should support config syntax") {
    }.config(tags = listOf("database", "linux"))
  }
}
```

### Inspectors

Inspectors allow us to test elements in a collection. For example, if we had a collection from a method and we wanted to test that every element in the collection passed some assertions, we can do:

```kotlin
val xs = // some collection
forAll(xs) { x =>
  x should have substring "qwerty"
  x should start with "q"
}
```

Similarly, if we wanted to asset that NO elements in a collection passed some assertions, we can do:

```kotlin
val xs = // some collection
forNone(xs) { x =>
  x should have substring "qwerty"
  x should start with "q"
}
```

The full list of inspectors are:

* `forAll` which asserts every element passes the assertions
* `forNone` which asserts no element passes
* `forOne` which asserts only a single element passed
* `forAtMostOne` which asserts that either 0 or 1 elements pass
* `forAtLeastOne` which asserts that 1 or more elements passed
* `forAtLeast(k)` which is a generalization that k or more elements passed
* `forAtMost(k)` which is a generalization that k or fewer elements passed
* `forAny` which is an alias for `forAtLeastOne`
* `forSome` which asserts that between 1 and n-1 elements passed. Ie, if NONE pass or ALL pass then we consider that a failure.
* `forExactly(k)` which is a generalization that exactly k elements passed. This is the basis for the implementation of the other methods

### Eventually

When testing future based code, it's handy to be able to say "I expect these assertions to pass in a certain time". Sometimes you can do a Thread.sleep but this is bad as you have to set a timeout that's high enough so that it won't expire prematurely. Plus it means that your test will sit around even if the code completes quickly. Another common method is to use countdown latches. KotlinTest provides the `Eventually` mixin, which gives you the `eventually` method which will repeatedly test the code until it either passes, or the timeout is reached. This is perfect for nondeterministic code. For example:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("do something") {
      eventually(5, TimeUnit.SECONDS) {
        // code here that should complete in 5 seconds but takes an indetermistic amount of time.
      }
    }
  }
}
```
