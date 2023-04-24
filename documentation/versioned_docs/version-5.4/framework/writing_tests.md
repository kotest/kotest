---
id: writing_tests
title: Writing Tests
slug: writing-tests.html
sidebar_label: Writing Tests
---

By using the language features available in Kotlin, Kotest is able to provide a more powerful and yet simple approach
to defining tests. Gone are the days when tests need to be methods defined in a Java file.

In Kotest a test is essentially just a function `TestContext -> Unit` which contains your test logic.
Any assert statements (_matchers_ in Kotest nomenclature) invoked in this function that throw an exception
will be intercepted by the framework and used to mark that test as failed or success.

Test functions are not defined manually, but instead using the Kotest DSL, which provides several ways in which these functions
can be created and nested. The DSL is accessed by creating a class that extends from a class that implements a particular
[testing style](styles.md).

For example, using the _Fun Spec_ style, we create test functions using the `test` keyword, providing a name, and the
actual test function.

```kotlin
class MyFirstTestClass : FunSpec({

   test("my first test") {
      1 + 2 shouldBe 3
   }

})
```

Note that tests must be defined inside an `init {}` block or an init lambda as in the previous example.

### Nested Tests

Most styles offer the ability to nest tests. The actual syntax varies from style to style,
but is essentially just a different keyword used for the outer tests.

For example, in _Describe Spec_, the outer tests are created using the `describe` function and
inner tests using the `it` function.
JavaScript and Ruby developers will instantly recognize this style as it is commonly used in testing frameworks
for those languages.

```kotlin
class NestedTestExamples : DescribeSpec({

   describe("an outer test") {

      it("an inner test") {
        1 + 2 shouldBe 3
      }

      it("an inner test too!") {
        3 + 4 shouldBe 7
      }
   }

})
```

In Kotest nomenclature, tests that can contain other tests are called _test containers_ and tests
that are terminal or leaf nodes are called _test cases_. Both can contain test logic and assertions.


### Dynamic Tests

Since tests are just functions, they are evaluated at runtime.

This approach offers a huge advantage - tests can be dynamically created. Unlike traditional JVM test frameworks,
where tests are always methods and therefore declared at compile time, Kotest can add tests conditionally at runtime.

For example, we could add tests based on elements in a list.

```kotlin
class DynamicTests : FunSpec({

    listOf(
      "sam",
      "pam",
      "tim",
    ).forEach {
       test("$it should be a three letter name") {
           it.shouldHaveLength(3)
       }
    }
})
```

This would result in three tests being created at runtime. It would be the equivalent to writing:

```kotlin
class DynamicTests : FunSpec({

   test("sam should be a three letter name") {
      "sam".shouldHaveLength(3)
   }

   test("pam should be a three letter name") {
      "pam".shouldHaveLength(3)
   }

   test("tim should be a three letter name") {
     "tim".shouldHaveLength(3)
   }
})
```


### Lifecycle Callbacks

Kotest provides several callbacks which are invoked at various points during a test's lifecycle.
These callbacks are useful for resetting state, setting up and tearing down resources that a test might use, and so on.

As mentioned earlier, test functions in Kotest are labelled either _test containers_ or _test cases_, in addition to
the containing class being labelled a _spec_. We can register callbacks that are invoked before or after any test function, container, test case, or a spec itself.

To register a callback, we just pass a function to one of the callback methods.

For example, we can add a callback before and after any _test case_ using a function literal:

```kotlin
class Callbacks : FunSpec({

   beforeEach {
      println("Hello from $it")
   }

   test("sam should be a three letter name") {
      "sam".shouldHaveLength(3)
   }

   afterEach {
      println("Goodbye from $it")
   }
})
```

Note that the order of the callbacks in the file is not important.
For example, an `afterEach` block can be placed first in the class if you so desired.

If we want to extract common code, we can create a named function and re-use it for multiple files.
For example, say we wanted to reset a database before every test in more than one file, we could do this:

```kotlin
val resetDatabase: BeforeTest = {
  // truncate all tables here
}

class ReusableCallbacks : FunSpec({

   beforeTest(resetDatabase)

   test("this test will have a sparkling clean database!") {
       // test logic here
   }
})
```

For details of all callbacks and when they are invoked, see [here](lifecycle_hooks.md) and [here](extensions/extensions.md).
