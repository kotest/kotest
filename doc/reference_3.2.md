KotlinTest
==========

[<img src="https://img.shields.io/maven-central/v/io.kotlintest/kotlintest-core.svg?label=latest%20release"/>](https://search.maven.org/search?q=g:io.kotlintest) [![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)]()

This version of the document is for version 3.2+.
For docs for earlier versions see [here](reference_3.1.md)

How to use
----------

KotlinTest is published to Maven Central so you can get the latest version from the little badge at the top of the readme.

#### Gradle

To use in gradle, configure your build to use the [JUnit Platform](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle). For Gradle 4.6 and higher this is
 as simple as adding `useJUnitPlatform()` inside the `test` block and then adding the KotlinTest dependency.

```groovy
test {
  useJUnitPlatform()
}

dependencies {
  testImplementation 'io.kotlintest:kotlintest-runner-junit5:3.2.1'
}
```

#### Maven

For maven you must configure the surefire plugin for junit tests.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.1</version>
    <dependencies>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-surefire-provider</artifactId>
            <version>1.2.0</version>
        </dependency>
    </dependencies>
</plugin>
```

And then add the KotlinTest JUnit5 runner to your build.

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest-runner-junit5</artifactId>
    <version>3.1.8</version>
    <scope>test</scope>
</dependency>
```










Testing Styles
--------------

KotlinTest is permissive in the way you can lay out tests, which it calls a testing _style_.
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

Using the lambda expression avoids another level of indentation and looks neater,
 but it means you cannot override methods in the parent class such as `beforeTest` and `afterTest`.

All tests styles have a way to `setup` or `tear down` the tests in a similar way. You can execute a function before each test or after the whole class has completed, for example. Take a look at [Test Listeners](#listeners)

[See an example](styles.md) of each testing style.

Note: Test cases inside each spec will always run in a certain order (either in definition order, or in a random order, see [documentation](/doc/test_ordering.md#test-ordering) on test ordering).











Matchers and Assertions
--------

Matchers are used to assert a variable or function should have a particular value.
KotlinTest has over 100 built in matchers. Matchers can be used in two styles:

* Extension functions like `a.shouldBe(b)` or `a.shouldStartWith("foo")`
* Infix functions like `a shouldBe b` or `a should startWith("foo")`

Both styles are supported. The advantage of the extension function style is that the IDE can autocomplete for you,
but some people may prefer the infix style as it is slightly cleaner.

Matchers can be negated by using `shouldNot` instead of `should` for the infix style. For example, `a shouldNot startWith("boo")`.
For the extension function style, each function has an equivalent negated version, for example, `a.shouldNotStartWith("boo")`.

Matchers are available in the `kotlintest-assertions` module, which is usually added to the build
when you add a KotlinTest test runner to your build (eg, `kotlintest-runner-junit5`). Of course, you could always add
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
would like to see all of the assertions that failed. KotlinTest provides
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
exception.message should start with "Something went wrong"
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
 before and after the entire project. For this KotlinTest provides the `TestListener` interface. Instances of this interface can be registered
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
    println("Duration of ${testCase.descriptor} = " + (System.currentTimeMillis() - started))
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
|beforeSpecClass|Is invoked when the engine is preparing the spec to be executed. It will be executed only once, regardless of how many times the [Spec is instantiated](isolation_mode.md)
|afterSpecClass|Is invoked once all tests for a `Spec` have completed, regardless of how many times the [Spec is instantiated](isolation_mode.md)
|beforeProject|Is invoked as soon as the Test Engine is started.|
|afterProject|Is invoked as soon as the Test Engine has finished.|
|afterDiscovery|Is invoked after all the Spec classes have been discovered, but before any `beforeSpec` functions are called, and before any specs are instantiated by the Test Engine. |


Project Config
--------------

KotlinTest is flexible and has many ways to configure tests.
 Project-wide configuration is used by creating a special singleton object
 which is loaded at runtime by KotlinTest.

To do this, create an object that is derived from `AbstractProjectConfig`, name this object `ProjectConfig`
and place it in a package called `io.kotlintest.provided`. KotlinTest will detect it's presence and use any configuration
defined there when executing tests.

Some of the configuration available in `ProjectConfig` includes parallelism of tests, executing code before and after
 all tests, and re-usable listeners or extensions.

###  Executing Code Before and After a Whole Project

To execute some logic before the very first test case and/or after the very last test case of your project, you can
 override `beforeAll` and `afterAll` in the `ProjectConfig` singleton.

Example:

```kotlin
package io.kotlintest.provided

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

### Project Extensions
_(Project Extensions are DEPRECATED in favour of Test Listeners.)_

Many types of reusable extensions can be registered in the `ProjectConfig`. Where appropriate these will be executed for all
 test cases and specs. Test level extensions will be covered in the next section.

For example, to extract logic for beforeAll and afterAll into a separate class you can implement the interface `ProjectExtension`.

```kotlin
class TimerExtension: ProjectExtension {

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

This extension can then be registered with the project config.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
  override val extensions = listOf(TimerExtension)
}
```

### Parallelism

KotlinTest supports running specs in parallel to take advantage of modern cpus with several cores. To do this, override
 the `parallelism` function inside the project config.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
   override fun parallelism(): Int = 2
}
```

By default the value is 1, which will run each spec serially.

### Discovery Extension

_Advanced Feature_

Another type of extension that can be used inside `ProjectConfig` is the `DiscoveryExtension`. This extension is designed
 to allow customisation of the way spec classes are discovered and instantiated. There are two functions of interest that
 can be overridden.

The first is `afterScan` which accepts a list of Spec classes that were discovered by KotlinTest during the _discovery_ phase
 of the test engine. This function then returns a list of the classes that should actually be instantiated and executed. By
 overriding this function, you are able to filter which classes are used, or even add in extra classes not originally discovered.

The second function is `instantiate` which accepts a `KClass<Spec>` and then attempts to create an instance of this Spec class in order
 to then run the test cases defined in it. By default, Spec classes are assumed to have a zero-arg primary constructor.
 If you wish to use non-zero arg primary constructors this function can be implemented with logic on how to instantiate a test class.

An implementation can choose to create a new instance, or it can choose to return null if it wishes to pass control to the next
extension (or if no more extensions, then back to the Test Engine itself).

By overriding this function, extensions are able to customize the way classes are created, to support things like constructors
with parameters, or classes that require special initialization logic. This type of extension is how the Spring Constructor Injection
add-on works for example.







Property-based Testing <a name="property-based"></a>
----------------------

### Property Testing

To automatically test your code with many combinations of values, you can allow KotlinTest to do the boilerplate
by using property testing with `generators`. You invoke `assertAll` or `assertNone` and pass in a lambda, where the lambda
parameters are populated automatically with many different values. The lambda must specify explicitly the parameter
types as KotlinTest will use those to determine what types of values to pass in.

For example, here is a property test that checks that for any two Strings, the length of `a + b`
is the same as the length of `a` plus the length of `b`. In this example KotlinTest would
execute the test 1000 times for random String combinations.

```kotlin
class PropertyExample: StringSpec() {
  init {

    "String size" {
      assertAll({ a: String, b: String ->
        (a + b).length shouldBe a.length + b.length
      })
    }

  }
}
```

You can also specify the number of times a test is going to be run. Here is the same test but this time it will run 2300 times.

```kotlin
class PropertyExample: StringSpec() {
  init {

    "String size" {
      forAll(2300) { a: String, b: String ->
        (a + b).length == a.length + b.length
      }
    }

  }
}
```

There are generators defined for all the common base types - String, Ints, UUIDs, etc. If you need to generate custom types
then you can simply specify the generator manually (or write your own). For example here is the same test again but
with the generators explicitly specified.

```kotlin
class PropertyExample: StringSpec() {
  init {

    "String size" {
      forAll(Gen.string(), Gen.string(), { a: String, b: String ->
        (a + b).length == a.length + b.length
      })
    }

  }
}
```


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









Table-driven Testing
--------------------

To test your code with different parameter combinations, you can use a table of values as input for your test
cases. This is sometimes called _data driven testing_ and other times called _table driven testing_.

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

In the above example, the `root` and `square` parameters are automatically inferred to be integers.

If there is an error for any particular input row, then the test will fail and KotlinTest will automatically
match up each input to the corresponding parameter names. For example, if we change the previous example to include the row `row(5,55)`
then the test will be marked as a failure with the following error message.

```
Test failed for (root, 5), (square, 55) with error expected: 55 but was: 25
```

Table testing can be used within any spec. Here is an example using `StringSpec`.

```kotlin
class StringSpecExample : StringSpec({
  "string concat" {
    forall(
      row("a", "b", "c", "abc"),
      row("hel", "lo wo", "rld", "hello world"),
      row("", "z", "", "z")
    ) { a, b, c, d ->
      a + b + c shouldBe d
    }
  }
})
```

It may be desirable to have each row of data parameters as an individual test. To generating such individual tests follow a similar pattern for each spec style. An example in the `FreeSpec` is below.

```kotlin
class IntegerMathSpec : FreeSpec({
    "Addition" - {
        listOf(
            row("1 + 0", 1) { 1 + 0 },
            row("1 + 1", 2) { 1 + 1 }
        ).map { (description: String, expected: Int, math: () -> Int) ->
            description {
                math() shouldBe expected
            }
        }
    }
    // ...
    "Complex Math" - {
        listOf(
            row("8/2(2+2)", 16) { 8 / 2 * (2 + 2) },
            row("5/5 + 1*1 + 3-2", 3) { 5 / 5 + 1 * 1 + 3 - 2 }
        ).map { (description: String, expected: Int, math: () -> Int) ->
            description {
                math() shouldBe expected
            }
        }
    }
})
```

Produces 4 tests and 2 parent descriptions:

```txt
IntegerMathSpec
  ✓ Addition
    ✓ 1 + 0
    ✓ 1 + 1
  ✓ Complex Math
    ✓ 8/2(2+2)
    ✓ 5/5 + 1*1 + 3-2
```






Isolation Modes
---------------

Note: Isolation modes replace _One Instance Per Test_ which was a setting in version 3.1 and earlier.

By default, one instance of the Spec class is created and then each test case is executed until they all complete.
This is different to the default in JUnit where a new class is instantiated for every test.

However sometimes it may be desirable for each test - or each outer test - to be executed in a different
instance of the Spec class, much like JUnit. In this case, you will want to change the isolation mode.

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

  override fun defaultTestCaseConfig() = TestCaseConfig(invocations = 3)

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

There are several ways to disable tests.

### By Config

You can disable a test case simply by setting the config parameter `enabled` to `false`.
If you're looking for something like JUnit's `@Ignore`, this is for you.

```kotlin
"should do something".config(enabled = false) {
  ...
}
```

You can use the same mechanism to run tests only under certain conditions.
 For example you could run certain tests only on Linux systems using
 [`SystemUtils.IS_OS_LINUX`](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SystemUtils.html#IS_OS_LINUX) from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/).

```kotlin
"should do something".config(enabled = IS_OS_LINUX) {
  ...
}
```

### Focus

KotlinTest supports isolating a single top level test by preceding the test name with `f:`.
Then only that test (and any subtests defined inside that scope) will be executed, with the rest being skipped.

For example, in the following snippet only the middle test will be executed.

```kotlin
class FocusExample : StringSpec({
    "test 1" {
     // this will be skipped
    }

    "f:test 2" {
     // this will be executed
    }

    "test 3" {
     // this will be skipped
    }
})
```


### Bang

The opposite of focus is possible, which is to prefix a test with an exclamation mark `!` and then that test (and any subtests defined inside that scope) will be skipped.
In the next example we’ve disabled the first test by adding the “!” prefix.

```kotlin
class BangExample : StringSpec({

  "!test 1" {
    // this will be ignored
  }

  "test 2" {
    // this will run
  }

  "test 3" {
    // this will run too
  }
})
```



Grouping Tests with Tags
------------------------

Sometimes you don't want to run all tests and KotlinTest provides tags to be able to run only
certain tests. Tags are objects inheriting from `io.kotlintest.Tag`.

To group tests by operating system you could define the following tags:

```kotlin
object Linux : Tag()
object Windows: Tag()
```

Test cases are marked with tags with the `config` function:

```kotlin
import io.kotlintest.specs.StringSpec

class MyTest : StringSpec() {
  init {
    "should run on Windows".config(tags = setOf(Windows)) {
      // ...
    }

    "should run on Linux".config(tags = setOf(Linux)) {
      // ...
    }

    "should run on Windows and Linux".config(tags = setOf(Windows, Linux)) {
      // ...
    }
  }
}
```

Then by invoking the test runner with a system property of `kotlintest.tags.include` and/or `kotlintest.tags.exclude`, you
can control which tests are run:

* If no `kotlintest.tags.include` and/or `kotlintest.tags.exclude` are specified, all tests (both tagged and untagged ones) are run.
* If only `kotlintest.tags.include` are specified, only tests with that tag are run (untagged test are *not* run).
* If only `kotlintest.tags.exclude` are specified, only tests without that tag are run (untagged tests *are* run).
* If you provide more than one tag for `kotlintest.tags.include` or `kotlintest.tags.exclude`, a test case with at least one of the given tags is included/excluded.

Provide the simple names of tag object (without package) when you run the tests.
Please pay attention to the use of upper case and lower case! If two tag objects have the same simple name (in different name spaces) they are treated as the same tag.

Example: To run only test tagged with `Linux`, but not tagged with `Database`, you would invoke
Gradle like this:

```
gradle test -Dkotlintest.tags.include=Linux -Dkotlintest.tags.exclude=Database
```

If you use `kotlintest.tags.include` and `kotlintest.tags.exclude` in combination, only the tests tagged with a tag from
`kotlintest.tags.include` but not tagged with a tag from `kotlintest.tags.exclude` are run. If you use only `kotlintest.tags.exclude`
all tests but the tests tagged with the given tags are are run.





**A special attention is needed in your gradle configuration**

To use System Properties (-Dx=y), your gradle must be configured to propagate them to the test executors, and an extra configuration must be added to your tests:

Groovy:
```
test {
    //... Other configurations ...
    systemProperties = System.properties
}
```

Kotlin Gradle DSL:
```
val test by tasks.getting(Test::class) {
    // ... Other configurations ...
    systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
}
```

This will guarantee that the system property is correctly read by the JVM







Closing resource automatically
--------------------------------------------

You can let KotlinTest close resources automatically after all tests have been run:

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
KotlinTest allows you to do this, by using the `whenReady(future, fn)` construct.

```kotlin
class MyTests : StringSpec({

    "test a future" {
        val f: CompletableFuture<String> = someFuture()
        whenReady(f) {
            it shouldBe "wibble"
        }
    }
})
```

### Eventually <a name="eventually"></a>

When testing non-deterministic code, it's handy to be able to say "I expect these assertions to pass in a certain time".
Sometimes you can do a Thread.sleep but this is bad as you have to set a timeout that's high enough so that it won't expire prematurely.
Plus it means that your test will sit around even if the code completes quickly. Another common method is to use countdown latches.
KotlinTest provides the `Eventually` mixin, which gives you the `eventually` function which will repeatedly test the code until it either passes,
or the timeout is reached. This is perfect for nondeterministic code. For example:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("do something") {
      eventually(5.seconds) {
        // code here that should complete in 5 seconds but takes an non-deterministic amount of time.
      }
    }
  }
}
```




Extensions
----------

KotlinTest provides you with several extensions and listeners to test execution out of the box.

Some of them provide unique integrations with external systems, such as [Spring Boot](extensions.md#Spring) and [Arrow](extensions.md#Arrow).
Some others provides helpers to tricky System Testing situations, such as `System Environment`, `System Properties`, `System Exit` and `System Security Manager`.

We also provide a `Locale Extension`, for locale-dependent code, and `Timezone Extension` for timezone-dependent code.

Take a better look at all the extensions available in the [extensions-reference](extensions.md)
