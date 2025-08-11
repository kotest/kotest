Kotest
==========

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-core.svg?label=latest%20release"/>](https://search.maven.org/search?q=g:io.kotest) [![GitHub license](https://img.shields.io/github/license/kotest/kotest.svg)]()

This version of the document is for the 4.x releases.
For version 3.3 see [here](reference_3.3.md)

Project Rename!
------

Starting with release 4.0 **KotlinTest** was renamed to **Kotest** to avoid confusion with the Jetbrains provided `kotlin.test` package.

**Note:** All packages are now `io.kotest` instead of `io.kotlintest`. Similarly the modules released to maven are in the form `kotest-xyz`.
There is an upgrade cost. Please be prepared when you upgrade that you will need to do more work than updating the versions in your build file. Mostly this is updating imports across your project, but some function definitions have also changed to support multiplatform builds (for instance Kotlin Durations are now used instead of Java Durations).


Getting Started
----------

Kotest is split into 3 basic sub-projects.
These are provided separately so you can pick and choose which parts to use if you don't want to go _all in_ on Kotest.


|     |     |
| --- | --- |
| **Test Framework**<br/>Layout tests in a fluid way and execute them on the JVM or Javascript. | <img src="https://img.shields.io/maven-central/v/io.kotest/kotest-framework-engine.svg?label=release"/> [<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/) |
| **Assertions Library**<br/>A Kotlin-first multiplatform enabled assertions library. | <img src="https://img.shields.io/maven-central/v/io.kotest/kotest-assertions-core.svg?label=release"/> [<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/) |
| **Property Testing**<br/>An advanced multiplatform property test library with shrinking support. | <img src="https://img.shields.io/maven-central/v/io.kotest/kotest-property.svg?label=release"/> [<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/) |

#### Which subproject(s) to use?

* If you want to lay out tests in a [fluid way](styles.md); with built in coroutine support at every level; the ability to use [functions as test lifecycle callbacks](/doc/listeners.md#dsl-methods-with-functions); with extensive extension points; with advanced [conditional evaluation](conditional_evaluation.md); and execute these tests on the JVM and/or Javascript, then build your test classes using the Kotest Test Framework.

* If you want a Kotlin focused multiplatform enabled assertions library; with over [300 rich assertions](matchers.md); with support for [inspectors](inspectors.md); helpers for [non-deterministic tests](nondeterministic.md); powerful [data driven testing](data_driven_testing.md); modules for [arrow](https://arrow-kt.io/), json and more, then opt to use the Kotest assertions library.

* If you want a powerful multiplatform enabled [property test](property_testing.md) library, with over 50 [built in generators](generators.md); the ability to easily compose new generators; with failure shrinking; with exhaustive checks; with coverage metrics; then choose the Kotest property test module.


The following instructions give you the batteries included setup in gradle or maven. Omit any modules you don't wish to use.

#### Gradle

To use in gradle, configure your build to use the [JUnit Platform](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle). For Gradle 4.6 and higher this is
 as simple as adding `useJUnitPlatform()` inside the tasks with type `Test` and then adding the Kotest dependency.

<details open>
<summary>Groovy (build.gradle)</summary>

```groovy
test {
  useJUnitPlatform()
}

dependencies {
  testImplementation 'io.kotest:kotest-runner-junit5:<version>' // for kotest framework
  testImplementation 'io.kotest:kotest-assertions-core:<version>' // for kotest core jvm assertions
  testImplementation 'io.kotest:kotest-property:<version>' // for kotest property test
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
    testImplementation 'io.kotest:kotest-runner-junit5:<version>' // for kotest framework
    testImplementation 'io.kotest:kotest-assertions-core:<version>' // for kotest core jvm assertions
    testImplementation 'io.kotest:kotest-property:<version>' // for kotest property test
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
  testImplementation("io.kotest:kotest-runner-junit5:<version>") // for kotest framework
  testImplementation("io.kotest:kotest-assertions-core:<version>") // for kotest core jvm assertions
  testImplementation("io.kotest:kotest-property:<version>") // for kotest property test
}
```

</details>


#### Maven

For maven you must configure the surefire plugin for junit tests.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
</plugin>
```

And then add the Kotest JUnit5 runner to your build to use the framework product.


```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-runner-junit5</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

:::info
Kotest is a [multiplatform project](https://kotlinlang.org/docs/reference/multiplatform.html).
If you are unfamiliar with this, then Kotlin compiles to different targets - JVM, JS, Native, iOS and so on.
Since version 5.9.0, Kotest includes a workaround so that the `kotest-runner-junit5` module can be used directly, but
for older versions you need to explicitly depend on the modules that end with -JVM, such as `kotest-property-jvm`_
:::

For using kotest assertions add the following configuration.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-assertions-core</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

And for using kotest property testing add the following configuration.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-property</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

#### Snapshots

Snapshot are automatically published on each commit to master.
If you want to test the latest snapshot build, setup the same way described above, change the version to the current snapshot version and add the following repository to your `repositories` block:

```kotlin
repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}
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

All tests styles have a way to `setup` or `tear down` the tests in a similar way. You can execute a function before each test or after the whole class has completed, for example. Take a look at [Test Listeners](listeners.md)

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
  override fun test(value: String) = MatcherResult(value.contains("foo"), "String $value should include foo", "String $value should not include foo")
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

Another version of `assertSoftly` takes an test target and lambda with test target as its receiver.

```kotlin
assertSoftly(foo) {
    shouldNotEndWith("b")
    length shouldBe 3
}

```









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
that all, none or some of the elements pass the given assertions. For example, to test that exactly 3 elements in a collection
contain an underscore and start with "aa" we could do:

```kotlin
class StringSpecExample : StringSpec({
  "your test case" {
    val xs = listOf("aa_1", "aa_2", "aa_3")
    xs.forExactly(3) {
      it.shouldContain("_")
      it.shouldStartWith("aa")
    }
  }
})
```

For further information on the available inspectors see [Inspector Documentation](inspectors.md).





Listeners
---------

It is a common requirement to run setup / teardown code before and after a test, or before and after all tests in a Spec class. For this Kotest provides the `TestListener` and `ProjectListener` interfaces.

These interfaces contains several functions, such as `beforeTest`, `afterTest`, `beforeSpec`, `beforeProject`, and so on, which are used to hook into the lifecycle of the test engine.

For full details on how to use these features see [Listener Documentation](listeners.md).








Project Config
--------------

Kotest is flexible and has many ways to configure tests, such as configuring the order of tests inside a spec, setting the parallelism level, and failing builds if ignored tests are used.
Sometimes you may want to set these values at a global level and for that you need to use [project-level-config](project_config.md).













Property-based Testing <a name="property-based"></a>
----------------------

### Property Testing

Regular unit tests work by the developer setting up an example and providing assertions on what that example
should evaluate to. For instance, `"ko" + "test" should have length 6` is a single example based test on string concatenation.

A more powerful approach is to allow a test framework to generate the examples for you, randomly or exhaustively,
and the developer provides _properties_ which should always be _true_ or _false_ given the inputs.

Kotest has a comprehensive and powerful property support out of the box which is described in detail [here](property_testing.md).









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




Clues
------

Sometimes a failed assertion does not convey enough information to be immediately useful.

For example `user.name shouldNotBe null` would output simply `<null> should not equal <null>`.

By using [clues](clues.md) we can add extra contextual information.


Mocking
--------------

Kotest itself has no mock features. However, you can plug-in your favourite mocking library with ease!

Let's take for example [mockk](https://mockk.io):

```kotlin
class MyTest : FunSpec({

    val repository = mockk<MyRepository>()
    val target = MyService(repository)

    test("Saves to repository") {
        every { repository.save(any()) } just Runs
        target.save(MyDataClass("a"))
        verify(exactly = 1) { repository.save(MyDataClass("a")) }
    }

})
```

Sometimes you might need some extra configurations (such as setup mocks, restart their counter, etc). For that, please
check [mock documentation](mocks.md)





Temporary File
---------------

Sometimes it is required for a test to create a file and delete it after test, deleting it manually may lead to flaky
test.
For example you may be trying to delete temporary file after some assertion, if the assertion passes all good, file
is got deleted and next time you run your tests you create a fresh file.
But in case the assertion fails or any other error occurs, due to which file did not deleted, you will get a stale file
which might effect your test.

Kotest provide a function ```tempfile()``` which you can use in your Spec to create a temporary file for your tests,
here Kotest takes the responsibility of deleting the temporary file after running all test in the Spec. That way your
tests does not have to worry about clear the temporary file.

```kotlin
class MySpec : FunSpec({

   val file = tempfile()

   test("a temporary file dependent test") {
      //...
   }
})

```



Test Case Config
------------------------------


Each test can be configured with various parameters. After the test name, invoke the config function
 passing in the parameters you wish to set. The available parameters include `enabled`, `timeout`, `threads`, `invocations`, `tags` and more.

An example of using config to run a test 10 times, over 2 threads, is like this:

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

All the test config settings are enumerated on this [page](test_case_config.md).






Disabling Test Cases and Running Test Cases Conditionally
---------------------------------------------------------

Sometimes we want to temporarily disable some tests in of a test suite.
Perhaps we’re experimenting with some API changes and don’t want to have to keep changing all the tests until we’re happy with the new API.
Or perhaps we’re debugging and want to reduce the noise in the output.

Kotest has many options for disabling/enable tests at runtime. See this [page](conditional_evaluation.md) for full details.






Test Filtering via Gradle
-------------------------

When running Kotest via the JUnit Platform runner through gradle, Kotest supports the standard gradle syntax for
test filtering. You can enable filtering either in the build script or via the --tests command-line option.

For example, in the build script:

```groovy
tasks.test {
    filter {
        //include all tests from package
        includeTestsMatching("com.sksamuel.somepackage.*")
    }
}
```

Or via the command line:

```gradle test --tests 'com.sksamuel.somepackage*'```

```gradle test --tests '*IntegrationTest'```

See full Gradle documentation [here](https://docs.gradle.org/6.2.2/userguide/java_testing.html#test_filtering).






Grouping Tests with Tags
------------------------

Sometimes you don't want to run all tests all the time and Kotest provides _tags_ to be able to select
only a subset of tests to run. Tags are added to tests and then one or more tag can be included or excluded
from a test run. For full details read this [page](tags.md).





Test Factories
-------------

Kotest supports fully reusable tests via _test factories_. These are functions which return a set of dynamic tests
which can be included in one or more specs. Each factory can be parameterized by simply adding parameters to the
function that defines the factory.

Each included test appears in the test output and report as if it was individually defined.

Read more about [test factories](testfactories.md)



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



Non-deterministic Tests
----------------------

Sometimes you have to work with code that are non-deterministic in nature. This is never ideal, but if you have no choice then
Kotest has this covered with two functions called `eventually` and `continually`.

Eventually will repeatedly run a code block either it either succeeds or the given duration has expired.
Continually is kind of the opposite - it will repeatedly run a code block requiring that it succeeds every time until the given duration has expired.

See full docs [here](nondeterministic.md)




Extensions
----------

Kotest provides you with several extensions and listeners to test execution out of the box.

Some of them provide unique integrations with external systems, such as [Spring Boot](extensions.md#Spring), [MockServer](mockserver.md), and [Arrow](extensions.md#Arrow).
Some others provides helpers to tricky System Testing situations, such as `System Environment`, `System Properties`, `System Exit` and `System Security Manager`.

We also provide a `Locale Extension`, for locale-dependent code, and `Timezone Extension` for timezone-dependent code.

Take a better look at all the extensions available in the [extensions-reference](extensions.md)




Plugins
----------

Sometimes it's not enough to use Extensions or Listeners to integrate with external systems or tools, and for this we use custom Plugins, available at `kotest-plugins` module.

Integrations such as `Pitest` require a more complex solution, and thus the plugins module was necessary.

For more information on plugins, take a look at the [plugins reference](plugins.md)
