Changelog
=========

#### 3.1.6

* Specs now support co-routines #332
* Extension function version of inspectors.
* Inspectors for arrow NonEmptyLists
* New style of data driven tests with parameter name detection
* Extension function style of assert all for property testing
* Updated string matchers to show better error when input is null or empty string
* Allow nullable arguments to more matcher functions. #350
* Added extension functions for table tests #349

#### 3.1.5

* Fix for bug in gradle which doesn't support parallel test events
* Bring back Duration extension properties #343
* Added fix for gradle 4.7 issues #336
* shouldBe does not handle java long  #346
* Fixing function return type in documentation for forAll() (#345)
* Fixing typos in reference.md (#344)
* Make the Table & Row data classes covariant (#342)
* Fixing argument names in ReplaceWith of deprecated matchers (#341)

#### 3.1.4

* Fix eventually nanos conversion (#340)
* Improve array shouldBe overloads (#339)

#### 3.1.3

* Added workaround for gradle 4.7/4.8 error #336
* Fix URI path and URI parameter matchers (#338)

#### 3.1.2

* Added arrow NonEmptyList isUnique matchers
* Added Float and List Shrinker
* Added inspecting and extracting helper functions. (#334)
* Allow tags to be added to specs for all test cases #333
* Support randomized order of top level tests #328

#### 3.1.1

* Focus option for top level tests #329
* Improve shrinkage #331
* Updated readme for custom generators #313
* Added generator for UUIDs
* Fixed bug with auto-close not being called. Deprecated ProjectExtension in favour of TestListener.
* Added a couple of edge case matchers to the arrow extension; added arrow matchers for lists.

Version 3.1.0
----------

* **Simplified Setup**

In KotlinTest 3.1.x it is sufficent to enable JUnit in the test block of your gradle build
 instead of using the gradle junit plugin. This step is the same as for any test framework
 that uses the JUnit Platform.

Assuming you have gradle 4.6 or above, then setup your test block like this:

```groovy
test {
    useJUnitPlatform()
}
```

You can additionally enable extra test logging:

```groovy
test {
    useJUnitPlatform()
    testLogging {
        events "PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR"
    }
}
```

* **Instance Per Test for all Specs**

In the 3.0.x train, the ability to allow an instance per test was removed from some spec styles due to
implementation difficulties. This has been addressed in 3.1.x and so all spec styles now allow instance
per test as in the 2.0.x releases. Note: The default value is false, so tests will use a single shared
instance of the spec for all tests unless the `isInstancePerTest()` function is overriden to return true.

* **Breaking Change: Config Syntax**

The syntax for config has now changed. Instead of a function call after the test has been defined, it is
now specified after the name of the test.

So, instead of:

```kotlin
"this is a test" {
}.config(...)
```

You would now do:

```kotlin
"this is a test".config(...) {
}
```

* **Matchers as extension functions**

All matchers can now be used as extension functions. So instead of:

```kotlin
file should exist()

or

listOf(1, 2) should containNull()
```

You can do:

```kotlin
file.shouldExist()

or

listOf(1, 2).shouldContainNull()
```

Note: The infix style is **not** deprecated and will be supported in future releases, but the extension function
is intended to be the preferred style moving forward as it allows discovery in the IDE.

* **Dozens of new Matchers**

_even_ and _odd_

Tests that an Int is even or odd:

```kotlin
4 should beEven()
3 shouldNot beEven()

3 should beOdd()
4 shouldNot beOdd()
```

_beInRange_

Asserts that an int or long is in the given range:

```kotlin
3 should beInRange(1..10)
4 should beInRange(1..3)
```

_haveElementAt_

Checks that a collection contains the given element at a specified index:

```kotlin
listOf("a", "b", "c") should haveElementAt(1, "b")
listOf("a", "b", "c") shouldNot haveElementAt(1, "c")
```

Help out the type inferrer when using nulls:

```kotlin
listOf("a", "b", null) should haveElementAt<String?>(2, null)
```

_readable_, _writeable_, _executable_ and _hidden_

Tests if a file is readable, writeable, or hidden:

```kotlin
file should beRadable()
file should beWriteable()
file should beExecutable()
file should beHidden()
```

_absolute_ and _relative_

Tests if a file's path is relative or absolute.

```kotlin
File("/usr/home/sam") should beAbsolute()
File("spark/bin") should beRelative()
```

_startWithPath(path)_

Tests if a file's path begins with the specified prefix:

```kotlin
File("/usr/home/sam") should startWithPath("/usr/home")
File("/usr/home/sam") shouldNot startWithPath("/var")
```

_haveSameHashCodeAs(other)_

Asserts that two objects have the same hash code.

```kotlin
obj1 should haveSameHashCodeAs(obj2)
"hello" shouldNot haveSameHashCodeAs("world")
```

_haveSameLengthAs(other)_

Asserts that two strings have the same length.

```kotlin
"hello" should haveSameLengthAs("world")
"hello" shouldNot haveSameLengthAs("you")
```

_haveScheme, havePort, haveHost, haveParameter, havePath, haveFragment_

Matchers for URIs:

```kotlin
val uri = URI.create("https://localhost:443/index.html?q=findme#results")
uri should haveScheme("https")
uri should haveHost("localhost")
uri should havePort(443)
uri should havePath("/index.html")
uri should haveParameter("q")
uri should haveFragment("results")
```

* Date matchers - before / after / haveSameYear / haveSameDay / haveSameMonth / within
* Collections - containNull, containDuplicates
* Futures - completed, cancelled
* String - haveLineCount, contain(regex)
* Types - haveAnnotation(class)

* **Arrow matcher module**

A new module has been added which includes matchers for [Arrow](http://arrow-kt.io) - the popular and awesome
 functional programming library for Kotlin. To include this module add `kotlintest-assertions-arrow` to your build.

The included matchers are:

_Option_ - Test that an `Option` has the given value or is a `None`. For example:

```kotlin
val option = Option.pure("foo")
option should beSome("foo")

val none = None
none should beNone()
```

_Either_- Test that an `Either` is either a `Right` or `Left`. For example:

```kotlin
Either.right("boo") should beRight("boo")
Either.left("boo") should beLeft("boo")
```

_NonEmptyList_- A collection (no pun intended) of matchers for Arrow's `NonEmptyList`.
These mostly mirror the equivalent `Collection` matchers but for NELs. For example:

```kotlin
NonEmptyList.of(1, 2, null).shouldContainNull()
NonEmptyList.of(1, 2, 3, 4).shouldBeSorted<Int>()
NonEmptyList.of(1, 2, 3, 3).shouldHaveDuplicates()
NonEmptyList.of(1).shouldBeSingleElement(1)
NonEmptyList.of(1, 2, 3).shouldContain(2)
NonEmptyList.of(1, 2, 3).shouldHaveSize(3)
NonEmptyList.of(1, 2, 3).shouldContainNoNulls()
NonEmptyList.of(null, null, null).shouldContainOnlyNulls()
NonEmptyList.of(1, 2, 3, 4, 5).shouldContainAll(3, 2, 1)
```

_Try_ - Test that a `Try` is either `Success` or `Failure`.

```kotlin
Try.Success("foo") should beSuccess("foo")
Try.Failure<Nothing>(RuntimeException()) should beFailure()
```

_Validation_ - Asserts that a `Validation` is either `Valid` or an `Invalid`

```kotlin
Valid("foo") should beValid()
Invalid(RuntimeException()) should beInvalid()
```

* **Generator Bind**

A powerful way of generating random class instances from primitive generators is to use the new `bind` function.
A simple example is to take a data class of two fields, and then use two base generators and bind them to create
random values of that class.

```kotlin
data class User(val email: String, val id: Int)

val userGen = Gen.bind(Gen.string(), Gen.positiveIntegers(), ::User)

assertAll(userGen) {
  it.email shouldNotBe null
  it.id should beGreaterThan(0)
}
```

* **Property Testing: Classify**

When using property testing, it can be useful to see the distribution of values generated, to ensure you're getting
 a good spread of values and not just trival ones. For example, you might want to run a test on a String and you want to
 ensure you're getting good amounts of strings with whitespace.

To generate stats on the distribution, use classify with a predicate, a label if the predicate passes, and a label
 if the predicate fails. For example:

```kotlin
assertAll(Gen.string()) { a ->
    classify(a.contains(" "), "has whitespace", "no whitespace")
    // some test
}
```

And this will output something like:

```
63.70% no whitespace
36.30% has whitespace
```

So we can see we're getting a good spread of both types of value.

You don't have to include two labels if you just wish to tag the "true" case, and you can include more than one
 classification. For example:

```kotlin
forAll(Gen.int()) { a ->
    classify(a == 0, "zero")
    classify(a % 2 == 0, "even number", "odd number")
    a + a == 2 * a
}
```

This will output something like:

```
51.60% even number
48.40% odd number
0.10% zero
```

* **Property Testing: Shrinking**

* **Tag Extensions**

A new type of extension has been added called `TagExtension`. Implementations can override the `tags()` function
defined in this interface to dynamically return the `Tag` instances that should be active at any moment. The existing
system properties `kotlintest.tags.include` and `kotlintest.tags.exclude` are still valid and are not deprecated, but
adding this new extension means extended scope for more complicated logic at runtime.

An example might be to disable any Hadoop tests when not running in an environment that doesn't have the hadoop
home env variable set. After creating a `TagExtension` it must be registered with the project config.

```kotlin
object Hadoop : Tag()

object HadoopTagExtension : TagExtension {
  override fun tags(): Tags =
      if (System.getenv().containsKey("HADOOP_HOME")) Tags.include(Hadoop) else Tags.exclude(Hadoop)
}

object MyProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<Extension> = listOf(HadoopTagExtension)
}

object SimpleTest : StringSpec({
  "simple test" {
    // this test would only run on environments that have hadoop configured
  }.config(tags = setOf(Hadoop))
})
```

* **Discovery Extensions: instantiate()**

Inside the `DiscoveryExtension` interface the function `fun <T : Spec> instantiate(clazz: KClass<T>): Spec?` has been added which
allows you to extend the way new instances of `Spec` are created. By default, a no-args constructor is assumed. However, if this
function is overridden then it's possible to support `Spec` classes which have other constructors. For example, the Spring module
now supports constructor injection using this extension. Other use cases might be when you want to always inject some config class,
or if you want to ensure that all your tests extend some custom interface or superclass.

As a reminder, `DiscoveryExtension` instances are added to Project config.

* **System out / error extensions**

An extension that allows you to test for a function that writes to System.out or System.err. To use this extension add
 the module `kotlintest-extensions-system` to your build.

By adding the `NoSystemOutListener` or `NoSystemErrListener` to your config or spec classes, anytime a function tries to write
 to either of these streams, a `SystemOutWriteException` or `SystemErrWriteException` will be raised with the string that
 the function tried to write. This allows you to test for the exception in your code.

For example:

```kotlin
class NoSystemOutOrErrTest : StringSpec() {

  override fun listeners() = listOf(NoSystemOutListener, NoSystemErrListener)

  init {

    "System.out should throw an exception when the listener is added" {
      shouldThrow<SystemOutWriteException> {
        System.out.println("boom")
      }.str shouldBe "boom"
    }

    "System.err should throw an exception when the listener is added" {
      shouldThrow<SystemErrWriteException> {
        System.err.println("boom")
      }.str shouldBe "boom"
    }
  }
}
```

* **System.exit extension**

Another extension that is part of the `kotlintest-extensions-system` module. This extension will allow you to test
 if `System.exit(Int)` is invoked in a function. It achieves this by intercepting any calls to System.exit and instead
 of terminating the JVM, it will throw a `SystemExitException` with the exit code.

For example:

```kotlin
class SystemExitTest : StringSpec() {

  override fun listeners() = listOf(SpecSystemExitListener)

  init {

    "System.exit should throw an exception when the listener is added" {
      shouldThrow<SystemExitException> {
        System.exit(123)
      }.exitCode shouldBe 123
    }
  }
}
```

* **Spring Module Updates**

The spring extension module `kotlintest-extensions-spring` has been updated to allow for constructor injection.
This new extension is called `SpringAutowireConstructorExtension` and must be added to your `ProjectConfig.
Then you can use injected dependencies directly in the primary constructor of your test class.

For example:

```kotlin
@ContextConfiguration(classes = [(Components::class)])
class SpringAutowiredConstructorTest(service: UserService) : WordSpec({
  "SpringListener" should {
    "have autowired the service" {
      service.repository.findUser().name shouldBe "system_user"
    }
  }
})
```

* **JUnit 4 Runner**

A JUnit 4 runner has been added which allows KotlinTest to run using the legacy JUnit 4 platform.
To use this, add `kotlintest-runner-junit4` to your build instead of `kotlintest-runner-junit5`.

Note: This is intended for use when junit5 cannot be used.
It should not be the first choice as functionality is restricted.

Namely:

* In intellij, test output will not be nested
* Project wide beforeAll/afterAll cannot be supported.

Version 3.0.x - March 29 2018
-------------

* **Module split out**

KotlinTest has been split into multiple modules. These include core, assertions, the junit runner, and extensions such as spring,
allure and junit-xml.

The idea is that in a future release, further runners could be added (TestNG) or for JS support (once multi-platform Kotlin is out of beta). 
When upgrading you will typically want to add the  `kotlintest-core`,  `kotlintest-assertions` and `kotlintest-runner-junit5` to your build 
rather than the old `kotlintest` module which is now defunct. When upgrading, you might find that you need to update imports
to some matchers.

```
testCompile 'io.kotlintest:kotlintest-core:3.0.0'
testCompile 'io.kotlintest:kotlintest-assertions:3.0.0'
testCompile 'io.kotlintest:kotlintest-runner-junit5:3.0.0'
```

Gradle Users:

Also you _must_ include `apply plugin: 'org.junit.platform.gradle.plugin'` in your project and 
`classpath "org.junit.platform:junit-platform-gradle-plugin:1.1.0"` to the `dependencies` section of your `buildscript` 
or tests will not run (or worse, will hang). This allows gradle to execute
_jUnit-platform-5_ based tests (which KotlinTest builds upon). Note: Gradle says that this is **not** required as of 4.6 but even 
with 4.6 it seems to be required.

Maven users:

You need to include the following in your plugins:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.19.1</version>
    <dependencies>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-surefire-provider</artifactId>
            <version>1.1.0</version>
        </dependency>
    </dependencies>
</plugin>
```
            
And you must include 

```xml
        <dependency>
            <groupId>io.kotlintest</groupId>
            <artifactId>kotlintest-runner-junit5</artifactId>
            <version>${kotlintest.version}</version>
            <scope>test</scope>
        </dependency>
```

as a regular dependency.                     

* **Breaking: ProjectConfig**

Project wide config in KotlinTest is controlled by implementing a subclass of `AbstractProjectConfig`. In previous versions you could
call this what you wanted, and place it where you wanted, and `KotlinTest` would attempt to find it and use it. This was the cause of
many bug reports about project start up times and reflection errors. So in version 3.0.x onwards, KotlinTest will 
no longer attempt to scan the classpath.

Instead you must call this class `ProjectConfig` and place it in a package `io.kotlintest.provided`. It must still be a subclass of 
`AbstractProjectConfig` This means kotlintest can do a simple `Class.forName` to find it, and so there is no
 startup penalty nor reflection issues.

Project config now allows you to register multiple types of extensions and listeners, as well as setting parallelism.

* **Breaking: Interceptors have been deprecated and replaced with Listeners**

The previous `inteceptors` were sometimes confusing. You had to invoke the continuation function or the spec/test
would not execute. Not invoking the function didn't mean the spec/test was skipped, but that it would hang.

So interceptors are deprecated, and in some places removed. Those are not removed are now located in classes called
`SpecExtension` and `TestCaseExtension` and those interfaces should be used rather than functions directly.

Here is an example of a migrated interceptor.

```kotlin
val mySpecExtension = object : SpecExtension {
    override fun intercept(spec: Spec, process: () -> Unit) {
      println("Before spec!")
      process()
      println("After spec!")
    }
}
```

As a replacement, in 3.0.0 we've added the `TestListener` interface which is the more traditional before/after style callbacks.
In addition, these methods include the result of the test (success, fail, error, skipped) which gives you more
context in writing plugins. The `TestListener` interface offers everything the old interceptors could do, and more.

Here is an example of a simple listener.

```kotlin
object TimeTracker : TestListener {

  var started = 0L

  override fun beforeTest(description: Description) {
    TimeTrackerTest.started = System.currentTimeMillis()
  }

  override fun afterTest(description: Description, result: TestResult) {
    val duration = System.currentTimeMillis() - TimeTrackerTest.started
    println("Test ${description.fullName()} took ${duration}ms")
  }
}
```

If you want to use these methods in a Spec itself, then you can just override the functions
 directly because a Spec is already a TestListener.

```kotlin
object TimeTracker : WordSpec() {

  var started = 0L

  override fun beforeTest(description: Description) {
    started = System.currentTimeMillis()
  }

  override fun afterTest(description: Description, result: TestResult) {
    val duration = System.currentTimeMillis() - started
    println("Test ${description.fullName()} took ${duration}ms")
  }

  init {
    "some test" should {
      "be timed" {
        // test here
      }
    }
  }
}
```

Listeners can be added project wide by overriding `listeners()` in the `ProjectConfig`.

Note: In the next release, new `Extension` functions will be added which will be similar to the old interceptors, but with
 complete control over the lifecycle. For instance, a future intercept method will enforce that the user skip, run or abort a test
 in the around advice. They will be more complex, and so suited to more advanced use cases. The new `TestListener` interface will remain of course, and is the preferred option.

* **Parallelism**

If you want to run more than one spec class in parallel, you can by overriding `parallelism` inside your projects
`ProjectConfig` or by supplying the system property `kotlintest.parallelism`.

Note the system property always takes precedence over the config.

* **Futures Support**

Test cases now support waiting on futures in a neat way. If you have a value in a `CompletableFuture` that you want
to test against once it completes, then you can do this like this:

```kotlin
val stringFuture: CompletableFuture<String> = ...

"My future test" should {
  "support CompletableFuture<T>" {
    whenReady(stringFuture) {
      it shouldBe "wibble"
    }
  }
}
```

* **Breaking: Exception Matcher Changes**

The `shouldThrow<T>` method has been changed to also test for subclasses. For example, `shouldThrow<IOException>` will also match
exceptions of type `FileNotFoundException`. This is different to the behavior in all previous KotlinTest versions. If you wish to 
have functionality as before - testing exactly for that type - then you can use the newly added `shouldThrowExactly<T>`.

* **JUnit XML Module**

Support for writing out reports in junit-format XML has added via the `kotlintest-extensions-junitxml` module which you will need to add to your build. This module
provides a `JUnitXmlListener` which you can register with your project to autowire your tests. You can register this by overriding
`listeners()` in `ProjectConfig`.

```kotlin  
class ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(JUnitXmlListener)
}
```

* **Spring Module**

Spring support has been added via the `kotlintest-extensions-spring` module which you will need to add to your build. This module
provides a `SpringListener` which you can register with your project to autowire your tests. You can register this for just some classes
by overriding the `listeners()` function inside your spec, for example:

```kotlin  
class MySpec : ParentSpec() {
    override fun listeners() = listOf(SpringListener)
}
```

Or you can register this for all classes by adding it to the `ProjectConfig`. See the section on _ProjectConfig_ for how
to do this.

* **Breaking: Tag System Property Rename**

The system property used to include/exclude tags has been renamed to `kotlintest.tags.include` and `kotlintest.tags.exclude`. Make
sure you update your build jobs to set the right properties as the old ones no longer have any effect. If the old tags are detected
then a warning message will be emitted on startup.

* **New Matchers**

`beInstanceOf<T>` has been added to easily test that a class is an instance of T. This is in addition to the more verbose `beInstanceOf(SomeType::class)`.

The following matchers have been added for maps: `containAll`, `haveKeys`, `haveValues`. These will output helpful error messages showing you
which keys/values or entries were missing.

New matchers added for Strings: `haveSameLengthAs(other)`, `beEmpty()`, `beBlank()`, `containOnlyDigits()`, `containADigit()`, `containIgnoringCase(substring)`,
`lowerCase()`, `upperCase()`.

New matchers for URIs: `haveHost(hostname)`, `havePort(port)`, `haveScheme(scheme)`.

New matchers for collections: `containNoNulls()`, `containOnlyNulls()`

* **Breaking: One instance per test changes**

One instance per test is no longer supported for specs which offer _nested scopes_. For example, `WordSpec`. This is because of the tricky
nature of having nested closures work across fresh instances of the spec. When using one instance per test, a fresh spec class is required
for each test, but that means selectively executing some closures and not others in order to ensure the correct state. This has proved
the largest source of bugs in previous versions.

KotlinTest 3.0.x takes a simplified approach. If you want the flexibilty to lay out your tests with nested scopes, then all tests will
execute in the same instance (like Spek and ScalaTest). If you want each test to have it's own instance (like jUnit) then you can either
split up your tests into multiple files, or use a "flat" spec like `FunSpec` or `StringSpec`.

This keeps the implementation an order of magnitude simplier (and therefore less likely to lead to bugs) while offering a pragmatic approach
to keeping both sets of fans happy.

* **New Specs**

Multiple new specs have been added. These are: `AnnotationSpec`, `DescribeSpec` and `ExpectSpec`. Expect spec allows you to use the `context`
and `expect` keywords in your tests, like so:

```kotlin
class ExpectSpecExample : ExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      context("nested context even") {
        expect("some test") {
          // test here
        }
      }
    }
  }
}
```

The `AnnotationSpec` offers functionality to mimic jUnit, in that tests are simply functions annotated with `@io.kotlintest.specs.Test`. For example:

```kotlin
class AnnotationSpecExample : AnnotationSpec() {

  @Test
  fun test1() {

  }

  @Test
  fun test2() {

  }
}
```

And finally, the `DescribeSpec` is similar to SpekFramework, using `describe`, `and`, and `it`. This makes it very useful for those people who are looking
to migrate to KotlinTest from SpekFramework.

```kotlin
class DescribeSpecExample : DescribeSpec() {
  init {
    describe("some context") {
      it("test name") {
        // test here
      }
      describe("nested contexts") {
        and("another context") {
          it("test name") {
            // test here
          }
        }
      }
    }
  }
}
```

* **Property Testing with Matchers**

The ability to use matchers in property testing has been added. Previously property testing worked only with functions that returned a Boolean, like:

```kotlin
"startsWith" {
  forAll(Gen.string(), Gen.string(), { a, b ->
    (a + b).startsWith(a)
  })
} 
```

But now you can use `assertAll` and `assertNone` and then use regular matchers inside the block. For example:

```kotlin
"startsWith" {
  assertAll(Gen.string(), Gen.string(), { a, b ->
    a + b should startWith(a)
  })
} 
```

This gives you the ability to use multiple matchers inside the same block, and not have to worry about combining all possible errors
into a single boolean result.

* **Generator Edge Cases**

Staying with property testing - the _Generator_ interface has been changed to now provide two types of data.
 
The first are values that should always be included - those edge cases values which are common sources of bugs.
For example, a generator for Ints should always include values like zero, minus 1, positive 1, Integer.MAX_VALUE and Integer.MIN_VALUE. 
Another example would be for a generator for enums. That should include _all_ the values of the enum to ensure
each value is tested.

The second set of values are random values, which are used to give us a greater breadth of values tested.
The Int generator should return random ints from across the entire integer range.

Previously generators used by property testing would only include random values, which meant you were very unlikely to see the
edge cases that usually cause issues - like the aforementioned Integer MAX / MIN. Now you are guaranteed to get the edge
cases first and the random values afterwards.

* **Breaking: MockitoSugar removed**

This interface added a couple of helpers for Mockito, and was used primarily before Kotlin specific mocking libraries appeared.
Now there is little value in this mini-wrapper so it was removed. Simply add whatever mocking library you like to your build
and use it as normal.
 
* **CsvDataSource** 

This class has been added for loading data for table testing. A simple example:

```kotlin
class CsvDataSourceTest : WordSpec() {
  init {
  
    "CsvDataSource" should {
      "read data from csv file" {
      
        val source = CsvDataSource(javaClass.getResourceAsStream("/user_data.csv"), CsvFormat())
        
        val table = source.createTable<Long, String, String>(
            { it: Record -> Row3(it.getLong("id"), it.getString("name"), it.getString("location")) },
            { it: Array<String> -> Headers3(it[0], it[1], it[2]) }
        )
        
        forAll(table) { a, b, c ->
          a shouldBe gt(0)
          b shouldNotBe null
          c shouldNotBe null
        }
      }
    }
  }
}
```

* **Matcher Negation Errors**

All matchers now have the ability to report a better error when used with `shouldNot` and `shouldNotBe`. Previously a generic error
was generated - which was usually the normal error but with a prefix like "NOT:" but now each built in matcher will provide a full message, for example: `Collection should not contain element 'foo'`


Version 2.0.0, released 2017-03-26
----------------------------------

[Closed Issues](https://github.com/kotlintest/kotlintest/milestone/4?closed=1)

### Added

* You can write tests alternatively into a lambda parameter in the class constructor, eg:

```kotlin
class StringSpecExample : StringSpec({
  "strings.size should return size of string" {
    "hello".length shouldBe 5
    "hello" should haveLength(5)
  }
})
```

* Added `forNone` for table tests, eg

```kotlin
val table = table(
    headers("a", "b"),
    row(0L, 2L),
    row(2L, 2L),
    row(4L, 5L),
    row(4L, 6L)
)

forNone(table) { a, b ->
  3 shouldBe between(a, b)
}
```

* Interceptors have been added. Interceptors allow code to be executed before and after a test. See the main readme for more info.

* Simplified ability to add custom matchers. Simple implement `Matcher<T>` interface. See readme for more information.

* Added `shouldNot` to invert matchers. Eg, `"hello" shouldNot include("hallo")`

* Deprecated matchers which do not implement Matcher<T>. Eg, `should have substring(x)` has been deprecated in favour of `"hello" should include("l")`. This is because instances of Matcher<T> can be combined with `or` and `and` and can be negated with `shouldNot`.

* Added `between` matcher for int and long, eg

```3 shouldBe between(2, 5)```

* Added `singleElement` matcher for collections, eg

```x shouldBe singleElement(y)```

* Added `sorted` matcher for collections, eg

```listOf(1,2,3) shouldBe sorted<Int>()```

* Now supports comparsion of arrays #116

* Added Gen.oneOf<Enum> to create a generator that returns one of the values for the given Enum class.

### Changed

* Tags are objects derived from `Tag` class now.
* Tags can now be included and/or exluded. It is no longer the case that all untagged tests are
always executed.
* Fixed bugs with parenthesis breaking layout in Intellij #112

### Removed

* FlatSpec was removed because it has an irregular syntax with `config` and is essentially the same
as StringSpec, but more complicated.
* Deprecated method overloads with `duration: Long, unit: TimeUnit`
* `expecting` for testing exceptions (use shouldThrow now)


Version 1.3.2, released 2016-07-05
----------------------------------

### Changed

* Added `a shouldBe exactly(b)` matcher for doubles

* `kotlintest` only pulls in `mockito-core` now instead of `mockito-all`


Version 1.3.1, released 2016-07-03
----------------------------------

### Changed

* Bumped Kotlin version to 1.0.3

Version 1.3.0, released 2016-07-03
----------------------------------

[Closed Issues](https://github.com/kotlintest/kotlintest/issues?utf8=%E2%9C%93&q=is%3Aclosed+milestone%3A2.0)

### Added

* StringSpec. You can use simply use Strings as the basis for tests, eg:

```kotlin
class StringSpecExample : StringSpec() {
  init {
    "strings.size should return size of string" {
      "hello".length shouldBe 5
      "hello" should haveLength(5)
    }

    "strings should support config" {
      "hello".length shouldBe 5
    }.config(invocations = 5)
  }
}
```

* Table Tests. Tables allow you to manually specific combinations of values that should be used, and are useful for 
edge cases and other specific values you want to test. The headers are used for when values fail, 
the output can show you what inputs were used for what labels. An example of using a table consisting of two-value tuples:

```kotlin
class TableExample : StringSpec(), TableTesting {
  init {
    "numbers should be prime" {
      val table = table(
          headers("a", "b"),
          row(5, 5),
          row(4, 6),
          row(3, 7)
      )
      forAll(table) { a, b ->
        a + b == 10
      }
    }
  }
}
```

* Property tests. Property tests automatically generate values for testings. You provide, or have KotlinTest provide for you, `generators`, which will generate a set of values and the unit test will be executed for each of those values. An example using two strings and asserting that the lengths are correct:

```kotlin
class PropertyExample: StringSpec() {

  "String size" {
    forAll({ a: String, b: String ->
      (a + b).length == a.length + b.length
    })
  }

}
```

That test will be executed 100 times with random values in each test. See more in the readme.

* autoClose. Fields of type `Closeable` can be registered for automatic resource closing:

```kotlin
class StringSpecExample : StringSpec() {
  val reader = autoClose(StringReader("xyz"))
  
  ...
}
```

* `haveLength` matcher. You can now write for strings:

```kotlin
someString should haveLength(10)
```


* `haveSize` matcher. You can now write for collections:

```kotlin
myCollection should haveSize(4)
```

* `contain` matcher. You can now write

```kotlin
val col = listOf(1,2,3,4,5)
col should contain(4)
```

* `containInAnyOrder` matcher. You can now write

```kotlin
val col = listOf(1,2,3,4,5)
col should containInAnyOrder(4,2,3)
```

* `haveKey` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should haveKey(1)
```

* `haveValue` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should haveValue("a")
```

* `contain` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should contain(1, "a")
```

* `beTheSameInstanceAs` reference matcher. This is an alias for `x should be theSameInstanceAs(y)`, allowing `x should beTheSameInstanceAs(y)` which fits in with new matcher style.

### Changed

#### Replaced `timeout` + `timeUnit` with `Duration` ([#29](https://github.com/kotlintest/kotlintest/issues/29))

You can now write `config(timeout = 2.seconds)` instead of 
`config(timeout = 2, timeoutUnit = TimeUnit.SECONDS)`.

### Deprecated

nothing

### Removed

nothing

### Fixed

* Ignored tests now display properly. https://github.com/kotlintest/kotlintest/issues/43
* Failing tests reported as a success AND a failure https://github.com/kotlintest/kotlintest/issues/42
