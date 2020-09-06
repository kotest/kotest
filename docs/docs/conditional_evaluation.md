Conditional Evaluation
======================

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
 [SystemUtils](http://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SystemUtils.html#IS_OS_WINDOWS) .IS_OS_LINUX from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/).

```kotlin
"should do something".config(enabled = IS_OS_LINUX) {
  ...
}
```

`isLinux` and `isPostgreSQL` in the example are just expressions (values, variables, properties, function calls) that evaluate to `true` or `false`.


If you want to use a function that is based on the test rather than a value, then you can use `enabledIf`.

For example, if we wanted to disable all tests that begin with the word "danger" when executing on Windows we could do this:

```kotlin
val disableDangerOnWindows: EnabledIf = { it.name.startsWith("danger") && IS_OS_LINUX }

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

Note again that this **does not** work for nested tests.

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

See which specs support this and the syntax required on the [specs styles guide](styles.md).


