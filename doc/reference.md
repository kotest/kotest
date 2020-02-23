Kotest
==========

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-core.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest) [![GitHub license](https://img.shields.io/github/license/kotest/kotest.svg)]()

This version of the document is for the upcoming 4.0 release.
For previous versions see [here](reference_3.3.md)

**Note:** Release 4.0 is currently early beta and things may still change. Version 3.4.2 is the most recent stable release.

Project Rename!
------

Starting with release 4.0 **KotlinTest** was renamed to **Kotest** to avoid confusion with the Jetbrains provided `kotlin.test` package.

**Note:** All packages are now `io.kotest` instead of `io.kotlintest`. Similarly the modules released to maven are in the form `kotest-xyz`.
There is an upgrade cost. Please be prepared when you upgrade that you will need to do more work than updating the versions in your build file.

There are typealiases for most of the common classes and functions
so that existing imports will continue to work albeit with deprecation warnings. For lesser used functionality, you will need to update the imports.

How to use
----------

Kotest is published to Maven Central so you can get the latest version from the little badge at the top of the readme.

#### Gradle

To use in gradle, configure your build to use the [JUnit Platform](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle). For Gradle 4.6 and higher this is
 as simple as adding `useJUnitPlatform()` inside the `test` block and then adding the Kotest dependency.


<details open>
<summary>Groovy (build.gradle)</summary>

```groovy
test {
  useJUnitPlatform()
}

dependencies {
  testImplementation 'io.kotest:kotest-runner-junit5:<version>'
}
```

</details>


<details open>
<summary>Android Project (Groovy)</summary>

```groovy
android.testOptions {
    unitTests.all {
        useJUnitPlatform()
    }
}

dependencies {
    testImplementation 'io.kotest:kotest-runner-junit5:<version>'
}
```

</details>

If you are using Gradle+Kotlin, this works for both Android and non-Android projects:

<details open>
<summary>Kotlin (build.gradle.kts)</summary>

```kotlin
tasks.withType<Test> {
  useJUnitPlatform()
}

dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:<version>")
}
```

</details>


#### Maven

For maven you must configure the surefire plugin for junit tests.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.1</version>
</plugin>
```

And then add the Kotest JUnit5 runner to your build.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-runner-junit5</artifactId>
    <version><version></version>
    <scope>test</scope>
</dependency>
```










Testing Styles
--------------

Kotest is permissive in the way you can lay out tests, which it calls a testing _style_.
There are [several styles](styles.md) to pick from. There is no functional difference between these -
 it is simply a matter of preference how you structure your tests. It is common to see several styles in one project.

You can choose a testing style by extending StringSpec, WordSpec, FunSpec, ShouldSpec, FeatureSpec, BehaviorSpec, FreeSpec, DescribeSpec, ExpectSpec  or AnnotationSpec in your test class,
 and writing your tests either inside an `init {}` block or inside a lambda parameter in the class constructor.

For example, using a lambda expression in the constructor, with the StringSpec gives us:

```kotlin
class MyTests : StringSpec({
  // tests here
})
```

And using an init block, again with the StringSpec looks like:

```kotlin
class MyTests : StringSpec() {
  init {
    // tests here
  }
}
```

Using the lambda expression avoids another level of indentation but it is purely a matter of preference.

All tests styles have a way to `setup` or `tear down` the tests in a similar way. You can execute a function before each test or after the whole class has completed, for example. Take a look at [Test Listeners](#listeners)

[See an example](styles.md) of each testing style.

Note: Test cases inside each spec will always run in a certain order (either in definition order, or in a random order, see [documentation](/doc/test_ordering.md#test-ordering) on test ordering).










Matchers and Assertions
--------

Matchers are used to assert a variable or function should have a particular value.
Kotest has over 100 built in matchers. Matchers can be used in two styles:

* Extension functions like `a.shouldBe(b)` or `a.shouldStartWith("foo")`
* Infix functions like `a shouldBe b` or `a should startWith("foo")`

Both styles are supported. The advantage of the extension function style is that the IDE can autocomplete for you,
but some people may prefer the infix style as it is slightly cleaner.

Matchers can be negated by using `shouldNot` instead of `should` for the infix style. For example, `a shouldNot startWith("boo")`.
For the extension function style, each function has an equivalent negated version, for example, `a.shouldNotStartWith("boo")`.

Matchers are available in the `kotest-assertions` module, which is usually added to the build
when you add a Kotest test runner to your build (eg, `kotest-runner-junit5`). Of course, you could always add
this to your build explicitly.

The simplest matcher is that a value should be equal to something, eg: `x.shouldBe(y)`.
This will also work for null values, eg `x.shouldBe(null)`. More specialized matchers test for things like string length, file size,
 collection duplicates and so on.

See the [full list of matchers](matchers.md) for more details.

### Custom Matchers

It is easy to add your own matchers. Simply extend the Matcher<T> interface, where T is the type you wish to match against.
The Matcher interface specifies one method, `test`, which you must implement returning an instance of Result.
The Result contains a boolean to indicate if the test passed or failed, and two messages.

The first message should always be in the positive, ie, indicate what "should" happen, and the second message
is used when the matcher is used with _not_.

For example to create a matcher that checks that a string contains the substring "foo", we can do the following:

```kotlin
fun containFoo() = object : Matcher<String> {
  override fun test(value: String) = Result(value.contains("foo"), "String $value should include foo", "String $value should not include foo")
}
```
This matcher could then be used as follows:

```kotlin
"hello foo" should containFoo()
"hello bar" shouldNot containFoo()
```

And we should then create an extension function version, like this:

```kotlin
fun String.shouldContainFoo() = this should containFoo()
fun String.shouldNotContainFoo() = this shouldNot containFoo()
```











Soft Assertions
---------------

Normally, assertions like `shouldBe` throw an exception when they fail.
But sometimes you want to perform multiple assertions in a test, and
would like to see all of the assertions that failed. Kotest provides
the `assertSoftly` function for this purpose.

```kotlin
assertSoftly {
  foo shouldBe bar
  foo should contain(baz)
}
```

If any assertions inside the block failed, the test will continue to
run. All failures will be reported in a single exception at the end of
the block.









Exceptions
----------

To assert that a given block of code throws an exception, one can use the `shouldThrow` function. Eg,

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
exception.message should startWith("Something went wrong")
```

If you want to test that _exactly_ a type of exception is thrown, then use `shouldThrowExactly<E>`.
If you want to test that _any_ exception is thrown, then use `shouldThrowAny`.














Inspectors
----------

Inspectors allow us to test elements in a collection. They are extension functions for collections and arrays that test
that all, none or some of the elements pass the given assertions. For example, to test that all elements in a collection
contain an underscore and start with "aa" we could do:

```kotlin
class StringSpecExample : StringSpec({
  "your test case" {
    val xs = listOf("aa_1", "aa_2", "aa_3")
    xs.forAll {
      it.shouldContain("_")
      it.shouldStartWith("aa")
    }
  }
})
```

Similarly, if we wanted to asset that *no* elements in a collection passed the assertions, we can do:

```kotlin
xs.forNone {
  it.shouldContain("x")
  it.shouldStartWith("bb")
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








Listeners
---------

It is a common requirement to run setup or teardown code before and after a test, or before and after all tests in a Spec class. Or sometimes
 before and after the entire project. For this Kotest provides the `TestListener` interface. Instances of this interface can be registered
 with a `Spec` class or project wide by using [ProjectConfig](#project-config).

 This interface contains several functions,
 such as `beforeTest`, `afterTest`, `beforeSpec` and so on, which are used to hook into the lifecycle of the test engine.

Let's say we want to log the time taken for each test case. We can do this by using the `beforeTest` and `afterTest` functions
 as follows:

```kotlin
object TimerListener : TestListener {

  var started = 0L

  override fun beforeTest(testCase: TestCase): Unit {
    started = System.currentTimeMillis()
  }

  override fun afterTest(testCase: TestCase, result: TestResult): Unit {
    println("Duration of ${testCase.description} = " + (System.currentTimeMillis() - started))
  }
}
```

Then we can register this with a particular Spec, like so:

```kotlin
class MyTestClass : WordSpec() {

  override fun listeners(): List<TestListener> = listOf(TimerListener)

  // tests here

}
```

It's also important to notice that every `Spec` is also a `TestListener`, therefore you may override these functions directly in `Spec`.

```kotlin
class MyTestClass : WordSpec() {

    override fun beforeTest(testCase: TestCase) {
      // BeforeTest here
    }

}

```

These functions will now be invoked for every test case inside the `MyTestClass` test class. Maybe you want
 this listener to run for every test in the entire project. To do that, you would register the listener with
 the project config singleton. For more information on this see [ProjectConfig](#project-config).

The full list of the functions in the `TestListener` interface is as follows:

|Function|Purpose|
|--------|-------|
|beforeTest|Is invoked each time before a Test Case is executed. If the test is marked as Ignored, this won't execute.|
|afterTest|Is invoked each time after a Test Case is executed. If the test is marked as Ignored, this won't execute. This will execute even if the test fails |
|beforeSpec|Is invoked each time a Spec is started, before any `beforeTest` functions are invoked. |
|afterSpec|Is invoked each time a Spec completes, after all `afterTest` functions are invoked. |
|beforeProject|Is invoked as soon as the Test Engine is started.|
|afterProject|Is invoked as soon as the Test Engine has finished.|
|afterDiscovery|Is invoked after all the Spec classes have been discovered, but before any `beforeSpec` functions are called, and before any specs are instantiated by the Test Engine. |








Project Config
--------------

Kotest is flexible and has many ways to configure tests.
 Project-wide configuration is used by creating a special singleton object
 which is loaded at runtime by Kotest.

To do this, create an object that is derived from `AbstractProjectConfig`, name this object `ProjectConfig`
and place it in a package called `io.kotest.provided`. Kotest will detect it's presence and use any configuration
defined there when executing tests.

Some of the configuration available in `ProjectConfig` includes parallelism of tests, executing code before and after
 all tests, and re-usable listeners or extensions.

###  Executing Code Before and After a Whole Project

To execute some logic before the very first test case and/or after the very last test case of your project, you can
 override `beforeAll` and `afterAll` in the `ProjectConfig` singleton.

Example:

```kotlin
package io.kotest.provided

object ProjectConfig : AbstractProjectConfig() {

  private var started: Long = 0

  override fun beforeAll() {
    started = System.currentTimeMillis()
  }

  override fun afterAll() {
    val time = System.currentTimeMillis() - started
    println("overall time [ms]: " + time)
  }
}
```






### Parallelism

Kotest supports running specs in parallel to take advantage of modern cpus with several cores. To do this, override
 the `parallelism` function inside the project config.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
   override fun parallelism(): Int = 2
}
```

By default the value is 1, which will run each spec serially.






### Discovery Extensions

Kotest allows developers to configure how test classes are discovered. By default classes are scanned from the classpath
but this extension allows developers to inject classes from any source. For full details see [here](discovery_extension.md)





Property-based Testing <a name="property-based"></a>
----------------------

### Property Testing

Regular unit tests work by the developer setting up an example and providing assertions on what that example
should evaluate to. For instance, `"ko" + "test" should have length 6` is a single example based test on string concatenation.

A more powerful approach is to allow a test framework to generate the examples for you, randomly or exhaustively,
and the developer provides _properties_ which should always be _true_ or _false_ given the inputs.

Kotest has a comprehesive and powerful property support out of the box which is described in detail [here](property_testing.md).




### Custom Generators

To write your own generator for a type T, you just implement the interface `Gen<T>`.

```kotlin
interface Gen<T> {
  fun constants(): Iterable<T>
  fun random(): Sequence<T>
}
```

The first function, `constants` returns values that should _always_ be included
 in the test inputs. This is typically used for common edge case values. For example, the `Int` generator implements
 `constants` to return 0, Int.MIN_VALUE and Int.MAX_VALUE as these are values that are often overlooked.

The second function is `random` which returns a lazy list of random values, which is the bread and butter of a generator.

For example you could write a `Gen` that supports a custom class called `Person`.
 In this case there are no real edge case values for a `Person` instance so we can leave `constants` as an empty list.

```kotlin
data class Person(val name: String, val age: Int)
class PersonGenerator : Gen<Person> {
    override fun constants() = emptyList<Person>()
    override fun random() = generateSequence {
        Person(Gen.string().random().first(), Gen.int().random().first())
    }
}
```









Data-driven Testing
--------------------

To test your code with different parameter combinations, you can use a table of values as input for your test
cases. This is called _data driven testing_ also known as _table driven testing_.

Invoke the `forAll` or `forNone` function, passing in one or more `row` objects, where each row object contains
the values to be used be a single invocation of the test. After the `forAll` or `forNone` function, setup your
actual test function to accept the values of each row as parameters.

The row object accepts any set of types, and the type checker will ensure your types are consistent with the parameter
types in the test function.

```kotlin
"square roots" {
  forall(
      row(2, 4),
      row(3, 9),
      row(4, 16),
      row(5, 25)
  ) { root, square ->
    root * root shouldBe square
  }
}
```

For more details see the [data driven testing page](data_driven_testing.md).




Isolation Modes
---------------

In Kotest one instance of the Spec class is created and then each test case is executed until they all complete.
This is different to the JUnit default where a new class is instantiated for every test.

However sometimes it may be desirable for each test scope - or each outer test scope - to be executed in a different
instance of the Spec class, much like JUnit. In this case, you will want to change what is called the _isolation mode_.

All specs allow you to control the isolation mode. Full instructions can be found [here](isolation_mode.md)





Test Case Config
------------------------------

Each test can be configured with various parameters. After the test name, invoke the config function
 passing in the parameters you wish to set. The available parameters are:

* `invocations` - The number of times to run this test. Useful if you have a non-deterministic test and you want to run that particular test a set number of times to see if it eventually fails. A test will only succeed if all invocations succeed. Defaults to 1.
* `threads` - Allows the invocation of this test to be parallelized by setting the number of threads. If invocations is 1 (the default) then this parameter will have no effect. Similarly, if you set invocations to a value less than or equal to the number threads, then each invocation will have its own thread.
* `enabled` - If set to `false` then this test is disabled. Can be useful if a test needs to be temporarily ignored. You can also use this parameter with boolean expressions to run a test only under certain conditions.
* `timeout` - sets a timeout for this test. If the test has not finished in that time then the test fails. Useful for code that is non-deterministic and might not finish. Timeout is of type `Duration` which can be instantiated like `2.seconds`, `3.minutes` and so on.
* `tags` - a set of tags that can be used to group tests (see detailed description below).

Examples of setting config:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string").config(invocations = 10, threads = 2) {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

```kotlin
class MyTests : WordSpec() {
  init {
    "String.length" should {
      "return the length of the string".config(timeout = 2.seconds) {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

```kotlin
class FunSpecTest : FunSpec() {
  init {
    test("FunSpec should support config syntax").config(tags = setOf(Database, Linux)) {
      // ...
    }
  }
}
```

You can also specify a default TestCaseConfig for all test cases of a Spec:

```kotlin
class MySpec : StringSpec() {

  override val defaultTestCaseConfig = TestCaseConfig(invocations = 3)

  init {
    // your test cases ...
  }
}
```








Disabling Test Cases and Running Test Cases Conditionally
---------------------------------------------------------

Sometimes we want to temporarily disable some tests in of a test suite.
Perhaps we’re experimenting with some API changes and don’t want to have to keep changing all the tests until we’re happy with the new API.
Or perhaps we’re debugging and want to reduce the noise in the output.

Kotest has many options for disabling/enable tests at runtime. See this [page](conditional_evaluation.md) for full details.







Grouping Tests with Tags
------------------------

Sometimes you don't want to run all tests all the time and Kotest provides _tags_ to be able to select
only a subset of tests to run. Tags are added to tests and then one or more tag can be included or excluded
from a test run. For full details read this [page](tags.md).





Closing resource automatically
--------------------------------------------

You can let Kotest close resources automatically after all tests have been run:

```kotlin
class StringSpecExample : StringSpec() {

  val reader = autoClose(StringReader("xyz"))

  init {
    "your test case" {
      // use resource reader here
    }
  }
}
```

Resources that should be closed this way must implement [`java.lang.AutoCloseable`](https://docs.oracle.com/javase/7/docs/api/java/lang/AutoCloseable.html). Closing is performed in
reversed order of declaration after the return of the last spec interceptor.








Futures
-------

When testing future based code, it's useful to have a test run as soon as a future has completed, rather than blocking and waiting.
Kotest allows you to do this, by using the `future.whenReady(fn)` extension function.

```kotlin
class MyTests : StringSpec({

    "test a future" {
        val f: CompletableFuture<String> = someFuture()
        f.whenReady {
            it shouldBe "wibble"
        }
    }
})
```



Non-determinstic Tests
----------------------

Sometimes you have to work with code that are non-deterministic in nature. This is never ideal, but if you have no choice then
Kotest has this covered with two functions called `eventually` and `continually`.

Eventually will repeatedly run a code block either it either succeeds or the given duration has expired.
Continually is kind of the opposite - it will repeatedly run a code block requiring that it suceeds every time until the given duration has expired.

See full docs [here](nondeterministic.md)




Extensions
----------

Kotest provides you with several extensions and listeners to test execution out of the box.

Some of them provide unique integrations with external systems, such as [Spring Boot](extensions.md#Spring) and [Arrow](extensions.md#Arrow).
Some others provides helpers to tricky System Testing situations, such as `System Environment`, `System Properties`, `System Exit` and `System Security Manager`.

We also provide a `Locale Extension`, for locale-dependent code, and `Timezone Extension` for timezone-dependent code.

Take a better look at all the extensions available in the [extensions-reference](extensions.md)


Plugins
----------

Sometimes it's not enough to use Extensions or Listeners to integrat with external systems or tools, and for this we use custom Plugins, available at `kotest-plugins` module.

Integrations such as `Pitest` require a more complex solution, and thus the plugins module was necessary.

For more information on plugins, take a look at the [plugins reference](plugins.md)
