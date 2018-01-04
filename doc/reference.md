KotlinTest
==========

How to use
----------

KotlinTest is published to Maven Central, so to use, simply add the dependency in test scope to your build file. You can get the latest version from the little badge at the top of the readme.

Gradle:

    testCompile "io.kotlintest:kotlintest:xxx"

Maven:

```xml
<dependency>
    <groupId>io.kotlintest</groupId>
    <artifactId>kotlintest</artifactId>
    <version>xxx</version>
    <scope>test</scope>
</dependency>
```

Testing Styles<a name="styles"></a>
--------------

You can choose a testing style by extending StringSpec, WordSpec, FunSpec, ShouldSpec, FeatureSpec, BehaviorSpec or FreeSpec in your test class, and writing your tests either inside an `init {}` block or inside a lambda parameter in the class constructor.

```kotlin
import io.kotlintest.specs.StringSpec

// test cases in init block
class MyTests : StringSpec() {
  init {
    // tests here
  }
}

// test cases in lambda expression
class MyTests : StringSpec({
  // tests here
})
```

### String Spec

`StringSpec` reduces the syntax to the absolute minimum. Just write a string followed by a lambda expression with your test code. If in doubt, use this style.

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class MyTests : StringSpec() {
  init {
    "strings.length should return size of string" {
      "hello".length shouldBe 5
    }
  }
}
```

### Fun Spec

`FunSpec` allows you to create tests similar to the junit style. You invoke a method called test, with a string parameter to describe the test, and then the test itself:

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class MyTests : FunSpec() {
  init {
    test("String.length should return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

### Should Spec

`ShouldSpec` is similar to fun spec, but uses the keyword `should` instead of `test`. Eg:

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

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
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

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

### Word Spec

`WordSpec` uses the keyword `should` and uses that to nest test blocks after a context string, eg:

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec

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

### Feature Spec

`FeatureSpec` allows you to use `feature` and `scenario`, as such:

```kotlin
import io.kotlintest.specs.FeatureSpec

class MyTests : FeatureSpec() {
  init {
    feature("the thingy bob") {
      scenario("should explode when I touch it") {
        // test here
      }
      scenario("and should do this when I wibble it") {
        // test heree
      }
    }
  }
}
```

### Behavior Spec

`BehaviorSpec` allows you to use `given`, `when`, `then`, as such:

```kotlin
import io.kotlintest.specs.BehaviorSpec

class MyTests : BehaviorSpec() {
  init {
    given("a broomstick") {
      `when`("I sit on it") {
        then("I should be able to fly") {
          // test code
        }
      }
      `when`("I throw it away") {
        then("it should come back") {
          // test code
        }
      }
    }
  }
}
```

Because `when` is a keyword in Kotlin, we must enclose with backticks. Alternatively, there are title case versions
available if you don't like the use of backticks, eg, `Given`, `When`, `Then`.

### Free Spec

`FreeSpec` allows you to nest arbitary levels of depth using the keyword `-` (minus), as such:

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FreeSpec

class MyTests : FreeSpec() {
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

Property-based Testing <a name="property-based"></a>
----------------------

### Property Testing

To automatically test your code with many combinations of values, you can allow KotlinTest to do the boilerplate
by using property testing with `generators`. You invoke `forAll` or `forNone` and pass in a function, where the function
parameters are populated automatically with many different values. The function must specify explicitly the parameter
types as KotlinTest will use those to determine what types of values to pass in.

For example, here is a property test that checks that for any two Strings, the length of `a + b` 
is the same as the length of `a` plus the length of `b`. In this example KotlinTest would 
execute the test 100 times for random String combinations.

```kotlin
import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec

class PropertyExample: StringSpec() {
  init {

    "String size" {
      forAll({ a: String, b: String ->
        (a + b).length == a.length + b.length
      })
    }

  }
}
```

You can also specify the number of times a test is going to be run. Here is the same test but ran 2300 times.

```kotlin
import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec

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

There are generators for all the common types - String, Ints, Sets, etc. If you need to generate custom types
then you can simply specify the generator manually (and write your own). For example here is the same test again but
with the generators specified.

```kotlin
import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec

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

To write your own generator for a type T, you just implement the interface `Gen<T>`. For example you could write
a `Gen` that supports a custom class called `Person`:

```kotlin
data class Person(val name: String, val age: Int)
class PersonGenerator : Gen<Person> {
  override fun generate(): Person = Person(Gen.string().generate(), Gen.int().generate())
}
```

### Table-driven Testing <a name="table"></a>

To test your code with different parameter combinations, you can use tables as input for your test 
cases. 

Your test class should import `io.kotlintest.properties.*` for table testing support. Create a table
with the `table` function and pass a header and one or more row objects. You create the headers with
the `headers` function, and a row with the `row` function. A row can have up to 22 entries. Headers
and and rows must all have the same number of entries.

To use the table, you invoke `forAll(table)` inside a test plan and pass a closure with the actual test code.
The entries of the rows are passed as parameters to the closure.

Table testing can be used with any spec. Here is an example using `StringSpec`.

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec

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

Matchers <a name="matchers"></a>
--------

KotlinTest has many built in matchers, along a similar line to the popular [hamcrest](http://hamcrest.org/) project. The simplest assertion is that a value should be equal to something, eg: `x shouldBe y` or `x shouldEqual y`. This will also work for null values, eg `x shouldBe null` or `y shouldEqual null`. See the [full list of matchers](matchers.md).

Just import the matchers package to use them:

```kotlin
import io.kotlintest.matchers.*
```

Custom Matchers
--------------

It is easy to add your own matchers. Simply extend the Matcher<T> interface, where T is the type you wish to match again.
For example to add a matcher that checks that a string contains a substring, we can do the following:

```kotlin
fun hasSubstring(substr: String) = object : Matcher<String> {
  override fun test(value: String) = Result(value.contains(substr), "String $value should include substring $substr")
}
```

The Matcher interface specifies one method, `test`, which you must implement returning an instance of Result. The Result contains a boolean to indicate if the test passed or failed, and a message. The message should always be in the positive, ie, indicate what "should" happen.
This matcher could then be used as follows:

```kotlin
"hello" should haveSubstring("ell")
"hello" shouldNot haveSubstring("olleh")
```

Exceptions <a name="exceptions"></a>
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

Interceptors <a name="interceptors"></a>
------------

If you need to execute some logic before and/or after each test case, then you can use an interceptor. This is for example useful to cleanup a database after the test have run. 

In the `ProjectConfig` (see below) you can override `beforeAll` and `afterAll` or add extentions with such methods to the ProjectConfig. Logic in theses methods will be executed before and/or after the first/last test of the project.

In a spec class you can intercept the spec execution by overriding the `interceptors` property and providing a list of interceptors or by overriding `interceptSpec`.

A single test case can be intercepted by overriding `interceptTestCase` or by providing a list of interceptors in the `defaultTestCaseConfig` or in the `config` of a test case.

Interceptors replace `beforeEach`, `afterEach`, `beforeAll`, and `afterAll` functions from KotlinTest 1.x.

### Interceptor Execution Order

There are several points where you can hook in the test execution. 

* ProjectConfig.extensions beforeAll
  * ProjectConfig.beforeAll
    * Spec.interceptors
      * Spec.interceptSpec
        * test case
      * Spec.interceptSpec (interceptor from above continued)
    * Spec.interceptors (interceptors from above continued)
  * ProjectConfig.afterAll
* ProjectConfig.extensions afterAll

The general philoshopy here, is that the closer an interceptor is to a test case, the closer it is to the test case in the execution order.

The execution order within an interceptor collection (`ProjectConfig.extentions`, `Spec.interceptors`) is from left to right.

### Intercepting a Test Case

Override `interceptTestCase` in a spec class to provide logic that should be called before and after each test case. 

You could for example create a stopwatch by overriding `interceptTestCase`:

```kotlin
override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
  // before
  val started = System.currentTimeMillis()

  test() // don't forget to call test()!

  // after
  val finished = System.currentTimeMillis()
  val time = finished - started
  println("time [ms]: $time")
} 
```
**Attention: Don't forget to call `test()` in your interceptor! Otherwise the test case wouldn't be called.**

As you can see, you can keep some state, since an interceptor is really just a function and all variables are kept in this scope for the duration of the execution.

You can even use interceptors to catch exceptions:

```kotlin
override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
  try {
    test()
  } 
  catch (exception: SomeException) {
    // ok
  }
  catch (exception: Exception) {
    throw exception
  }
} 
```

If you define a separate interceptor function, you add it to the `defaultTestCaseConfig` or to the `config` of a test case:

```kotlin
  val interceptorA: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
    println("A before")
    testCase()
    println("A after")
  }

  val interceptorB: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
    println("B before")
    testCase()
    println("B after")
  }

class MySpec : StringSpec {

  override val defaultTestCaseConfig = TestCaseConfig(interceptors = listOf(interceptorA, interceptorB))

  init {
    "should do something" {
      ...
    }.config(interceptors = listOf(interceptorA)) // overrides the interceptors from above
  }
}
```

### Intercepting a Spec

To run logic before and after a spec, you can override `interceptSpec`. The principle is the same as above:

```kotlin
protected fun interceptSpec(context: Spec, spec: () -> Unit) {
  println("before spec")
  spec() // don't forget to call spec()!
  println("after spec")
}
```

### Resusable Interceptors

Interceptors are just functions and can be reused between specs or even between projects. Just pass interceptors to the `config` on test case or spec level.

```kotlin
"should do it correctly" {
  ...
}.config(interceptors = listOf(myTestCaseInterceptor))
```

An interceptor would look like this:

```kotlin
val myTestCaseInterceptor: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
  println("before")
  testCase() // Don't forget to call testCase()!
  println("after")
}
```

###  Executing Code Before and After a Whole Project

To run some logic before the very first test case or after the very last test case of you your project you can define a ProjectConfig singleton object derived from `ProjectConfig` somewhere in your test folder (preferably in the top-level test folder, but it will be found anywhere on the class path).

Override `beforeAll` and/or `afterAll`, to provide logic to be run before or after all tests of the project.

Example:

```kotlin
object DemoConfig : ProjectConfig() {

  override val extensions = listOf(TestExtension)

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

To provide resuable beforeAll and afterAll callbacks you can implement the interface `ProjectExtension`:

```kotlin
interface ProjectExtension {
  fun beforeAll() {}
  fun afterAll() {}
}
```

Extensions are registered with the project config:

```kotlin
object DemoConfig : ProjectConfig() {
  override val extensions = listOf(MyProjectExtension)
}
```

The `beforeAll` methods of the extensions are executed in the order of extensions (from left to right). The `afterAll` methods are executed in reversed order (from right to left). If you had two extensions `listOf(A, B)` the order of execution would be:

* `A.beforeAll`
  * `B.beforeAll`
    * test execution
  * `B.afterAll`
* `A.afterAll`.

A `ProjectExtension` implementation would look like this:

```kotlin
object TestExtension : ProjectExtension {
  override fun beforeAll() {
    println("before all extension")
  }

  override fun afterAll() {
    println("after all extension")
  }
}
```

One Instance Per Test (since 1.1.0)
-----------------------------------

By default a single instance of the test class is created for all the test it contains. However, if you wish to have a fresh instance per test (sometimes its easier to have setup code in the init block instead of resetting after each test) then simply override the `oneInstancePerTest` value and set it to true, eg:

```kotlin
class MyTests : ShouldSpec() {
  override val oneInstancePerTest = true
  init {
    // tests here
  }
}
```

Test Case Config (since 1.2.0) <a name="config"></a>
------------------------------

Each test can be configured with various parameters. After the test block, invoke the config method passing in the parameters you wish to set. The available parameters are:

* `invocations` - the number of times to run this test. Useful if you have a non-deterministic test and you want to run that particular test a set number of times. Defaults to 1.
* `threads` - Allows the invocation of this test to be parallelized by setting the number of threads to use in a thread pool executor for this test. If invocations is 1 (the default) then this parameter will have no effect. Similarly, if you set invocations to a value less than or equal to the number threads, then each invocation will have its own thread.
* `enabled` - If set to `false` then this test is disabled. Can be useful if a test needs to be temporarily ignored. You can also use this parameter with boolean expressions to run a test only under certain conditions.
* `timeout` - sets a timeout for this test. If the test has not finished in that time then the test fails. Useful for code that is non-deterministic and might not finish. Timeout is of type `Duration` which can be instantiated like `2.seconds`, `3.minutes` and so on.
* `tags` - a set of tags that can be used to group tests (see detailed description below).

Examples of setting config:

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class MyTests : ShouldSpec() {
  init {
    should("return the length of the string") {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }.config(invocations = 10, threads = 2)
  }
}
```

```kotlin
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec

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
import io.kotlintest.specs.FunSpec

class FunSpecTest : FunSpec() {
  init {
    test("FunSpec should support config syntax") {
      // ...
    }.config(tags = setOf(Database, Linux))
  }
}
```

You can also specify a default TestCaseConfig for all test cases of a Spec:

```kotlin
import io.kotlintest.specs.StringSpec

class MySpec : StringSpec() {

  override val defaultTestCaseConfig = TestCaseConfig(invocations = 3)

  init {
    // your test cases ...
  }
}
```


Disabling Test Cases and Running Test Cases Conditionally
---------------------------------------------------------

You can disable a test case simply by setting the config parameter `enabled` to `false`. If you're looking for something like JUnit's `@Ignore`, this is for you.

```kotlin
"should do something" {
  ...
}.config(enabled = false)
```

You can use the same mechanism to run tests only under certain conditions. For example you could run certain tests only on Linux systems using [SystemUtils](http://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SystemUtils.html#IS_OS_WINDOWS).IS_OS_LINUX from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/).

```kotlin
"should do something" {
  ...
}.config(enabled = IS_OS_LINUX)
```

`isLinux` and `isPostgreSQL` in the example are just expressions (values, variables, properties, function calls) that evaluate to `true` or `false`.


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
    "should run on Windows" {
      // ...
    }.config(tags = setOf(Windows))

    "should run on Linux" {
      // ...
    }.config(tags = setOf(Linux))

    "should run on Windows and Linux" {
      // ...
    }.config(tags = setOf(Windows, Linux))
  }
}
```

Then by invoking the test runner with a system property of `includeTags` and/or `excludeTags`, you
can control which tests are run:

* If no `includeTags` and/or `excludeTags` are specified, all tests (both tagged and untagged ones) are run.
* If only `includeTags` are specified, only tests with that tag are run (untagged test are *not* run).
* If only `excludeTags` are specified, only tests without that tag are run (untagged tests *are* run).
* If you provide more than one tag for `includeTags` or `excludeTags`, a test case with at least one of the given tags is included/excluded.

Provide the simple names of tag object (without package) when you run the tests. Please pay attention to the use of upper case and lower case! If two tag objects have the same simple name (in different name spaces) they are treated as the same tag.

Example: To run only test tagged with `Linux`, but not tagged with `Database`, you would invoke
Gradle like this:

```
gradle test -DincludeTags=Linux -DexcludeTags=Database
```

If you use `includeTags` and `excludeTags` in combination, only the tests tagged with a tag from
`includeTags` but not tagged with a tag from `excludeTags` are run. If you use only `excludeTags`
all tests but the tests tagged with the given tags are are run.

Closing resource automatically (since 1.3.0) <a name="autoclose"></a>
--------------------------------------------

You can let KotlinTest close resources automatically after all tests have been run:

```kotlin
import io.kotlintest.specs.StringSpec
import java.io.StringReader

class StringSpecExample : StringSpec() {

  val reader = autoClose(StringReader("xyz"))

  init {
    "your test case" {
      // use resource reader here
    }
  }
}
```

Resources that should be closed this way must implement [`java.io.Closeable`](http://docs.oracle.com/javase/6/docs/api/java/io/Closeable.html). Closing is performed in  
reversed order of declaration after the return of the last spec interceptor.

Inspectors <a name="inspectors"></a>
----------

Inspectors allow us to test elements in a collection. For example, if we had a collection from a method and we wanted to test that every element in the collection passed some assertions, we can do:

```kotlin
import io.kotlintest.matchers.*
import io.kotlintest.specs.StringSpec

class StringSpecExample : StringSpec() {

  init {
    "your test case" {
      val xs = listOf("aasdf", "basdf", "casdf")
          forAll(xs) { x ->
            x should include("as")
            x should startWith("q")
          }
    }
  }
}
```

Similarly, if we wanted to asset that NO elements in a collection passed some assertions, we can do:

```kotlin
val xs = // some collection
forNone(xs) { x ->
  x should include("qwerty")
  x should startWith("q")
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

Eventually <a name="eventually"></a>
----------

When testing future based code, it's handy to be able to say "I expect these assertions to pass in a certain time". Sometimes you can do a Thread.sleep but this is bad as you have to set a timeout that's high enough so that it won't expire prematurely. Plus it means that your test will sit around even if the code completes quickly. Another common method is to use countdown latches. KotlinTest provides the `Eventually` mixin, which gives you the `eventually` method which will repeatedly test the code until it either passes, or the timeout is reached. This is perfect for nondeterministic code. For example:

```kotlin
import io.kotlintest.specs.ShouldSpec

class MyTests : ShouldSpec() {
  init {
    should("do something") {
      eventually(5.seconds) {
        // code here that should complete in 5 seconds but takes an indetermistic amount of time.
      }
    }
  }
}
```

Migrating from KotlinTest 1.x
-----------------------------

### Migrating beforeAll, beforeEach, afterEach, afterAll

Each `Spec` class like `StringSpec` inherited the following callback methods from `TestBase`:

```kotlin
protected open fun beforeEach(): Unit
protected open fun afterEach(): Unit
protected open fun beforeAll(): Unit
protected open fun afterAll(): Unit
```

By overriding them, you could add behavior that should be executed before or after a single test or all tests of the spec.

This mechanism is superseded by: 

```kotlin
protected open fun interceptTestCase(context: TestCaseContext, test: () -> Unit)
protected open fun interceptSpec(context: Spec, spec: () -> Unit)
```

The before/after each pair can be replaced with `interceptTestCase`. An example makes it more clear:

```kotlin
override protected fun beforeEach(): Unit {
  println("before")
}

protected open fun afterEach(): Unit {
  println("after)
} 
```

Gets transformed to an interceptor that is around (before and after) the test case call:

```kotlin
override protected fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
  println("before")
  test() // Don't forget to call the test itself!
  println("after")
}
```

The principle applies also to beforeAll and afterAll what has to be transformed to `interceptSpec`.

### Migrating `ignored` config parameter

`config` has been changed to `enabled` and the logic was inverted accordingly. If you have a disabled test like this:

```kotlin
"should do something" {
  ...
}.config(ignored = true)
```

You need to change it to:

```kotlin
"should do something" {
  ...
}.config(enabled = false)
```
