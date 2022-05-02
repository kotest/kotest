---
id: focus_and_bang
title: Conditional tests with focus and bang
slug: conditional-tests-with-focus-and-bang.html
sidebar_label: Focus and Bang
---


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

The focus on a parent allows nested tests to execute:

```kotlin
class FocusExample : FunSpec({
   context("test 1") {
      // this will be skipped
      test("foo") {
         // this will be skipped
      }
   }

   context("f:test 2") {
      // this will be executed
      test("foo") {
         // this will be executed
      }
   }

   context("test 3") {
      // this will be skipped
      test("foo") {
         // this will be skipped
      }
    }
})
```

:::caution
The focus flag **does not** work if placed on nested tests due to the fact that nested tests are only discovered once the parent test has executed. So there would be no way for the test engine to know that a nested test has the f: prefix without first executing all the parents.
:::


### Bang

The opposite of focus is to prefix a test with an exclamation mark `!` and then that test (and any subtests defined inside that scope) will be skipped.
In the next example we’ve disabled only the first test by adding the “!” prefix.

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
If you want to disable the use of ! (and allow it to be used as the first character in enabled test names) then set the system property `kotest.bang.disable` to `true`.
:::
