Changelog
=========

This project follows [semantic versioning](http://semver.org/).


Version 3.0.0 - March 29 2018
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

* **Breaking: ProjectConfig**

Project wide config in KotlinTest is controlled by implementing a subclass of `AbstractProjectConfig`. In previous versions you could
call this what you wanted, and place it where you wanted, and `KotlinTest` would attempt to find it and use it. This was the cause of
many bug reports about project start up times and reflection errors. So in version 3.0.x onwards, KotlinTest will 
no longer attempt to scan the classpath.

Instead you must call this class `ProjectConfig` and place it in a package `io.kotlintest.provided`. It must still be a subclass of 
`AbstractProjectConfig` This means kotlintest can do a simple `Class.forName` to find it, and so there is no
 startup penalty nor reflection issues.

Project config now allows you to register multiple types of extensions and listeners, as well as setting parallelism.

* **Breaking: Interceptors have become Extensions and Listeners**

_Extensions_ have been added to replace the previous, and sometimes confusing, interceptors. There are four types of extension - 
_ProjectExtension_, _SpecExtension_, _TestCaseExtension_, and _DiscoveryExtension_. 
Each of these allow you to run code before and after the various
extension point. It is hoped that these interfaces are simpler than dealing directly with functions.

Note: In a future release, these extensions will be expanded to give full control over the test lifecycle.

To use an extension, just create a subclass of the interface that applies.
For example to add some code that is run for every spec you can do:

```kotlin
val mySpecExtension = object : SpecExtension {
    override fun intercept(spec: Spec, process: () -> Unit) {
      println("Before spec!")
      process()
      println("After spec!")
    }
}
```

The advantage of _around advice_ like this is that you can create local variables and use them before and after the spec. If instead
kotlintest used the standard "before" and "after" methods, then you would need to create _vars_ outside the methods and reference those. Something like:

```kotlin
class SpecExtensionExample : WordSpec() {

    var httpServer: HttpServer? = null
    
    override fun before() {
        httpServer = createServer()
        httpServer.start()
    }
    
    override fun after() {
        httpServer.stop()
    }
    
    init {
        // tests here
    }
}
```

But with KotlintTest you can do:

```kotlin
object HttpServerExtension : SpecExtension() {
    override fun intercept(spec: Spec, process: () -> Unit) {
        val httpServer = createServer()
        httpServer.start()
        process()
        httpServer.stop()
    }
}
```

And then you can register this with the test class:

```kotlin
class SpecExtensionTest : WordSpec() {
  override fun extensions(): List<Extension> = listOf(HttpServerExtension)
}
```

 
This choice means kotlintest is more elegant but it can look a bit more confusing at first. `TestCaseExtensions` work in the
same way but for individual test cases rather than specs.

If you want to register an extension for all tests then you can use the methods in `ProjectConfig`. 

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

* **Spring Module**

Spring support has been added via the `kotlintest-extensions-spring` module which you will need to add to your build. This module
provides a _SpringSpecExtension_ which you can register with your project to autowire your tests. You can register this for just some classes
by overriding the `specExtensions` function inside your spec, for example:

```kotlin  
class MySpec : ParentSpec() {
    override fun specExtensions(): List<SpecExtension> = listOf(SpringSpecExtension)
}
```

Or you can register this for all classes by adding it to the kotlintest ProjectConfig. See the section on _ProjectConfig_ for how
to do this.

* **Breaking: Tag System Property Rename**

The system property used to include/exclude tags has been renamed to `kotlintest.tags.include` and `kotlintest.tags.exclude`. Make
sure you update your build jobs to set the right properties as the old ones no longer have any effect. If the old tags are detected
then a warning message will be emitted on startup.

* **New Matchers**

`beInstanceOf<T>` has been added to easily test that a class is an instance of T. This is in addition to the more verbose `beInstanceOf(SomeType::class)`.

The following matchers have been added for maps: `containAll`, `haveKeys`, `haveValues`. These will output helpful error messages showing you
which keys/values or entries were missing.

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

And finally, the `DescribeSpec` is similar to SpekFramework, using `describe` and `it`. This makes it very useful for those people who are looking
to upgrade to KotlinTest.

```kotlin
class DescribeSpecExample : DescribeSpec() {
  init {
    describe("some context") {
      it("test name") {
        // test here
      }
      describe("nested contexts") {
        it("test name") {
          // test here
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
