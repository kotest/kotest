---
id: conditional_evaluation
title: Conditional Evaluation
slug: conditional-evaluation.html
---


There are several ways to disable tests. Some of these are hardcoded in your test, others are evaluated at runtime.

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
 [SystemUtils](http://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SystemUtils.html#IS_OS_WINDOWS) .IS_OS_LINUX from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/).

```kotlin
"should do something".config(enabled = IS_OS_LINUX) {
  ...
}
```

`isLinux` and `isPostgreSQL` in the example are just expressions (values, variables, properties, function calls) that evaluate to `true` or `false`.


If you want to use a function that is based on the test rather than a value, then you can use `enabledIf`.

For example, if we wanted to disable all tests that begin with the word "danger" unless we were executing on Linux, then we could do this:

```kotlin
val disableDangerOnWindows: EnabledIf = { !it.name.startsWith("danger") || IS_OS_LINUX }

"danger will robinson".config(enabledIf = disableDangerOnWindows) {
  // test here
}

"very safe will".config(enabledIf = disableDangerOnWindows) {
 // test here
}
```


### Focus

Kotest supports isolating a single **top level** test by preceding the test name with `f:`.
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

:::note
Focus mode **does not** work for nested tests due to the fact that nested tests are only discovered once the parent test has executed.
:::


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

:::tip
If you want to disable the use of ! then set the system property `kotest.bang.disable` to `true`.
:::



### X-Methods

Many spec styles offer variants of their keywords that begin with `x` to disable execution.
This is a popular approach with Javascript testing frameworks. The idea is you can quickly add the x character
to the test declaration to (temporarily) disable it.

For example, with describe spec we can do this:

```kotlin
class XMethodsExample : DescribeSpec({

  xdescribe("this block and it's children are now disabled") {
    it("will not run") {
      // disabled test
    }
  }

})
```

See which specs support this, and the syntax required on the [specs styles guide](styles.md).




### @Ignored

If you wish to disable all tests in a Spec, we may use the @Ignored annotation. Then the spec will be skipped, and not even instantiated.

```kotlin
@Ignored
class IgnoredSpec : FunSpec() {
  init {
    error("boom") // spec will not be created so this error will not happen
  }
}
```

:::note
This is only available on the JVM target.
:::


### @EnabledIf

Similar to @Ignored, we can use a function to determine if a spec should be created.
The @EnabledIf annotation requires a class that implements `EnabledCondition`.

For example, we may wish to only execute a test on Linux platforms if the name contains Linux.

```kotlin
class LinuxOnlyCondition : EnabledCondition() {
   override fun enabled(specKlass: KClass<out Spec>): Boolean =
      if (specKlass.simpleName?.contains("Linux") == true) IS_OS_LINUX else true
}
```

Then we can apply that to one or more specs

```kotlin
@EnabledIf(LinuxOnlyCondition::class)
class MyLinuxTest1 : FunSpec() {
  ..
}

@EnabledIf(LinuxOnlyCondition::class)
class MyLinuxTest2 : DescribeSpec() {
  ..
}
```

:::note
This is only available on the JVM target.
:::



## Gradle Test Filtering

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

:::note
Because gradle's test support is method/class based, we cannot filter tests down to the individual test level, only the class level.
:::
