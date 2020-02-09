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
 [SystemUtils](http://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SystemUtils.html#IS_OS_WINDOWS).IS_OS_LINUX from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/).

```kotlin
"should do something".config(enabled = IS_OS_LINUX) {
  ...
}
```

`isLinux` and `isPostgreSQL` in the example are just expressions (values, variables, properties, function calls) that evaluate to `true` or `false`.


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

### SkipTestException
Sometimes you want to interrupt a test in runtime, as perhaps you don't know at compile-time if the test should be executed. For this, KotlinTest provides a way to interrupt it by throwing an exception: The `SkipTestException`.

```kotlin
class SkipTestExceptionExample : StringSpec({

  "Test should be skipped" {
    if(isLocalEnvironment()) {
      throw SkipTestException("Cannot run this test in local environment.")
    }
  }

})
```

`SkipTestException` is an open class, so you may extend it and customize it if you need it.
