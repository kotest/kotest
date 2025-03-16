Release 4.2
======

The Kotest team is pleased to announce the release of Kotest 4.2.0.
This minor feature release continues on the excellent work that was included in the 4.1.0 release (which itself was almost as large as the 4.0.0 release!).

In this blog post we'll cover some of the more notable features and changes but for the full list see the [changelog](../changelog.md).

### Module changes

Firstly, the `kotest-runner-console` dependency is no longer required by the intellij plugin, and therefore no longer exists.
So that can be removed completely from your build if you were using it.

Secondly, the `kotest-core` dependency has become `kotest-framework-engine`.

* If you are using JVM tests, you should continue to use the `kotest-runner-junit5-jvm` dependency as before, and no changes are needed.
* If you were explicitly depending on the core module (for JS tests), then
you can add `kotest-framework-engine` to your `commonMain` sourceset or `kotest-framework-engine-js` to your `jsMain` sourceset.

Finally this release of Kotest is fully compatible with Kotlin 1.4.


### Multiplatform improvements

The core assertions library is now published for ios, watchos and tvos. This brings the list of support platforms to:

- linuxX64, mingwX64, macosX64, tvosX64, tvosArm64, watchosX86, watchosArm64, iosX64, iosArm64, iosArm32

### Kotlinx Date/Time Matchers

A new [assertions module](https://search.maven.org/artifact/io.kotest/kotest-assertions-kotlinx-time) has been created `kotest-assertions-kotlinx-time`
which contains matchers for the new [Kotlinx Datetime library](https://github.com/Kotlin/kotlinx-datetime).
Since the datetime library has an _incubating_ status, this assertions module may require breaking changes in the future if the date/time API mandates it.

This assertions module is multiplatform and is released for the JVM, JS, Linux, Mac and Windows targets.

An example assertion is checking that a date time has a given hour.

```kotlin
val date = LocalDateTime(2019, 2, 15, 12, 10, 0, 0)
date.shouldHaveHour(12)
```

For the full list of matchers, see the [source code](https://github.com/kotest/kotest/tree/master/kotest-assertions/kotest-assertions-kotlinx-time/src/commonMain/kotlin/io/kotest/matchers/kotlinx/datetime).



### Multiple Project Configs

Kotest supports customizing test plans by extending the `AbstractProjectConfig` class and placing it in your classpath somewhere. From 4.2.0, you can
now create more than one and all will be detected and configs merged. This is really nice if you want to have some shared config for all your tests in
a root module, and then customize with more finer details per module.

In the case of clashes, one value will be arbitrarily picked, so it is not recommended to add competing settings to different configs.


### Extended Callbacks

Kotest has always had `beforeTest` / `afterTest` callbacks which run before / after any 'test scope'. However sometimes you need a way to run setup/teardown code only before leaf
test scopes (called tests in Kotest) or branch test scopes (called containers in Kotest).

So in 4.2.0 we've introduced `beforeEach`, `afterEach`, `beforeContainer`, and `afterContainer`. The `xxEach` functions are invoked only for leaf level test scopes.
The `xxContainer` functions are invoked only for branch level test scopes.

This distinction is only relevant to [test styles](../framework/styles.md) that support nested scopes.

For example:

```kotlin
class CallbacksTest : DescribeSpec({

   beforeEach {
      println("Test: " + it.displayName)
   }

   beforeContainer {
      println("Container: " + it.displayName)
   }

   beforeTest {
      println("All: " + it.displayName)
   }

   describe("I am a container scope") {
      it("And I am a test scope") { }
   }
})
```

The output you would receive is:

```
Container: I am a container scope
All: I am a container scope

Test: And I am a test scope
All: And I am a test scope
```



### Spec Ordering

Kotest previously allowed the execution order of Specs to be decided randomly, discovery order (the default), or lexicographically. Now, there is support for an
annotation based approach. By selecting this, and annotating your Specs with `@Order(int)` you can specify any order you wish, with the specs with
the lowest int values executing first.

Any spec without an `@Order` annotation is considered "last". Any specs that tie will be executed arbitrarily.



### Tag Expressions

Tests and Specs can be tagged with `Tag` objects and then at runtime, tests can be enabled or disabled by specifying which tags to use. Previously, you
could do this by specifying which tags to include and which tags to exclude but nothing more advanced.

Now, you are able to specfify full boolean expressions using the `kotest.tags` system property, for example:

`gradle test -Dkotest.tags="Linux & !Database"`.

Expressions can be nested using parenthesis and can be arbitrarily complex. For full details see [Tags](../framework/tags.md).

Note: Existing system properties `kotest.tags.include` and `kotest.tags.exclude` are still supported, but the new functionality supersedes this.



### Spec level Timeout Overrides

It has always been possible to add a timeout to a test at the global level or via test case config for each specific test:

```kotlin
 test("my test").config(timeout = 20.seconds) { }
```

But it has not previously been possible to override this as the spec level for all tests in that spec. Now you can.

```kotlin
class TimeoutTest : DescribeSpec({

   timeout = 1000

   describe("I will timeout in 1000 millis") {
      it("And so will I") { }
      it("But I'm a little faster").config(timeout = 500.milliseconds) { }
   }

})
```

Note: You can apply a spec level timeout and then override this per test case, as you can see in the example above.
The same functionality exists for invocation timeouts.

### Exhaustive Specific forAll / checkAll

When property testing, if you are using only `exhaustive` generators, then the `forAll` / `checkAll` methods will now ensure that the number of iterations
is equal to the number of combinations in the exhaustives, and that all combinations are executed.

As a contrived example, consider this:

```
 val context = checkAll(
    Exhaustive.ints(0..5),
    Exhaustive.ints(0..5),
    Exhaustive.ints(0..5)
 ) { ... }
```
Here, the number of iterations is 6 * 6 * 6 = 216 and each tuple combination of (0-5, 0-5, 0-5) will be executed. The first will be (0, 0, 0) and the
last wil be (5, 5, 5) with every combination in between.


### Generic Contracts in Matchers

When using shouldBeInstanceOf<T\> or shouldBeTypeOf<T\>, the assertions can now use generic contracts to smart case down to generic instances.

For example, consider the following example where we are given an Any. After invoking `shouldBeTypeOf` with a generic type, the type is smart
casted if the assertion passes.

```kotlin
val list: Any = arrayListOf(1, 2, 3)
list.shouldBeTypeOf<ArrayList<Int>>()
list[0] shouldBe 1 // can only work with a smart case
```


### Kotest Plugin Updates

The Kotest Intellij Plugin is released on a separate cadence from Kotest itself, but here are some notable changes since Kotest 4.1.0.

* No extra dependencies are needed to use the plugin - the plugin ships with all required libraries.
* The new extended callbacks are recognized in the Kotest tool window.
* Runnning single tests is now supported in `AnnotationSpec`.
* Separate builds for Android Studio / Intellij 2019 to address some minor incompatibilities.
* Added inspection for using focus mode in nested tests
* Added implicit usage provider for object based project config



### Improved JUnit XML Report

The Junit XML report (what JUnit refers to as the legacy XML report because it existed prior to JUnit5) has no concept of nested tests.
Therefore, if you are using a spec style that supports nested tests, the gradle report generator will only use the leaf test name.
This can be confusing if you are expecting the full test path for context.

In 4.2.0 Kotest has it's own implementation of this XML report that contains options to a) include the full test path and / or b) ignore parent tests completely.

Example usage from within project config:

```kotlin
class ProjectConfig : AbstractProjectConfig() {

   override fun listeners(): List<Listener> = listOf(
     JunitXmlReporter(
        includeContainers = true, // write out status for all tests
        useTestPathAsName = true // use the full test path (ie, includes parent test names)
     )
   )
}
```


### Spring Listener Warning

If you are using the spring support and are using a final class, you will receive a warning from Kotest:

_Using SpringListener on a final class. If any Spring annotation fails to work, try making this class open_

You can disable this warning by setting the system property `kotest.listener.spring.ignore.warning` to true.


### Thanks

Huge thanks to all who contributed to this release.

Alberto Ballano, Ali Albaali, amollberg, Ashish Kumar Joy, Christian Stoenescu, Cleidiano Oliveira ,Daniel Asztalos,
fauscik, Juanjo Aguililla, Justin, Leonardo Colman, Matthew Mikolay, Neenad Ingole, Shane Lathrop, sksamuel, Timothy Lusk
