---
id: lifecycle_hooks
title: Lifecycle hooks
slug: lifecycle-hooks.html
sidebar_label: Lifecycle hooks
---

It is extremely common in tests to want to perform some action before and after a test, or before and after all tests in the same file.
It is in these _lifecycle hooks_ that you would perform any setup/teardown logic required for a test.

Kotest provides a rich assortment of hooks that can be defined directly inside a spec.
At the end of this section is a list of the available hooks and when they are executed.

:::tip[Advanced Cases]
For more advanced cases, such as writing distributable plugins, re-usable hooks, or for events that take place outside
a spec (such as project-started or project-finished) take a look at [extensions](extensions/extensions.md).
:::

There are generally two ways to define these hooks in Kotest that are functionally equivalent but different in style.
Pick whichever you and your team prefer.

## DSL Methods

The first is to use the DSL methods available inside a Spec that accept a lambda for the hook logic.
For example, we can invoke `beforeTest` or `afterTest` (or others) directly alongside our tests.

```kotlin
class TestSpec : FreeSpec() {
  init {

    beforeTest {
      println("Starting a test $it")
    }

    afterTest { (test, result) ->
      println("Finished spec with result $result")
    }

    "this test" - {
      "be alive" {
        println("Johnny5 is alive!")
      }
    }
  }
}
```

:::note
You can use `afterProject` as a DSL method but there is no equivalent `beforeProject`, because by the time the
framework is at the stage of executing a spec, the project has already started!
:::

### Lambda Type-aliases

Since these DSL methods accept functions, we can pull out logic to a function and re-use it in several places. The
`beforeTest` hook accepts a function of type `suspend (TestCase) -> Unit`. There are typealiases for each
of the function signatures to keep your code simple.

For example, to create a re-usable `beforeTest` lambda:

```kotlin
val startTest: BeforeTest = {
   println("Starting a test $it")
}

class TestSpec : FreeSpec({

   // used once
   beforeTest(startTest)

   "test1" { }
})

class OtherSpec : FreeSpec({

   // used again
   beforeTest(startTest)

   "test2" { }
})
```

## Method Overrides

The second way to create hooks is to override the appropriate function in the Spec. For example, to add
a before-test hook, we can override the `beforeTest` function:

```kotlin
class TestSpec : FreeSpec() {

    override suspend fun beforeTest(testCase: TestCase) {
        println("Starting a test $testCase")
    }

    init {
        "this test" - {
            "be alive" {
                println("Johnny5 is alive!")
            }
        }
    }
}
```

## Available Hooks

Kotest provides callbacks for various test and spec events.

To understand all callbacks correctly it's important to have a good understanding of the two possible `TestType` values:

- `Container` - a container that can contain other tests
- `Test` - a leaf test that cannot contain nested tests

### Test Lifecycle Hooks

| Callback          | Description                                                                                                                                                                                                                                                                                                                                                   |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| before-container  | Invoked directly before each test with type `TestType.Container`, with the `TestCase` instance as a parameter. If the test is marked is skipped, then this callback won't be invoked.                                                                                                                                                                         |
| after-container   | Invoked immediately after a `TestCase` with type `TestType.Container`, with the `TestResult` of that test. If a test was skipped then this callback will not be invoked.<br/><br/>The callback will execute even if the test fails.                                                                                                                           |
| before-each       | Invoked directly before each test with type `TestType.Test`, with the `TestCase` instance as a parameter. If the test is marked is skipped, then this callback won't be invoked.                                                                                                                                                                              |
| after-each        | Invoked immediately after a `TestCase` with type `TestType.Test`, with the `TestResult` of that test. If the test was skipped then this callback will not be invoked.<br/><br/>The callback will execute even if the test fails.                                                                                                                              |
| before-any        | Invoked directly before each test with any `TestType`, with the `TestCase` instance as a parameter. If the test is marked is skipped, then this callback won't be invoked.                                                                                                                                                                                    |
| after-any         | Invoked immediately after a `TestCase` with any `TestType`, with the `TestResult` of that test. If the test was skipped then this callback will not be invoked.<br/><br/>The callback will execute even if the test fails.                                                                                                                                    |
| before-test       | This callback is an alias for `beforeAny`.                                                                                                                                                                                                                                                                                                                    |
| after-test        | This callback is an alias for `afterAny`.                                                                                                                                                                                                                                                                                                                     |
| before-invocation | Invoked before each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.<br/><br/>If you are running a test with the default single invocation then this callback is effectively the same as `beforeTest`. |
| after-invocation  | Invoked after each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.<br/><br/>If you are running a test with the default single invocation then this callback is effectively the same as `afterTest`.   |
| after-scope       | Invoked after that particular scope has finished executing. This hook is only invoked once all the nested tests in that scope have completed and will not be executed after each individual test in that scope.                                                                                                                                               |                                                                                                                                              |                                                                                                                                                                                                                              |

Notice that `before-each` and `before-container` are constrained to a particular test-type (leaf or container), whereas `before-any` will be invoked for both.
The same applies to `after-each`, `after-container` and `after-any`.

### Spec Lifecycle Hooks

| Callback    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|-------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| before-spec | Invoked after the Engine instantiates a spec to be used as part of a test execution.<br/><br/>The callback is provided with the `Spec` instance that the test will be executed under.<br/><br/>If a spec is instantiated multiple times - for example, if `InstancePerRoot` isolation mode is used, then this callback will be invoked for each instance created, just before the first test is executed for that spec.<br/><br/>This callback should be used if you need to perform setup each time a new spec instance is created.<br/><br/>This callback runs before any test level hooks functions are invoked.                                                                                                                                                                                              |
| after-spec  | Is invoked after all the tests that are part of a particular spec instance have completed.<br/><br/>If a spec is instantiated multiple times - for example, if `InstancePerRoot` isolation mode is used, then this callback will be invoked for each instantiated spec, after the tests that are applicable to that spec instance have returned.<br/><br/>This callback should be used if you need to perform cleanup after each individual spec instance.<br/><br/>This callback runs after any test level callbacks have been invoked.<br/><br/>In case there is any exception in `beforeSpec`, `afterSpec` will be skipped.                                                                                                                                                                                   |


