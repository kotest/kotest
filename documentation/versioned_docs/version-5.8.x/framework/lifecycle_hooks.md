---
id: lifecycle_hooks
title: Lifecycle hooks
slug: lifecycle-hooks.html
sidebar_label: Lifecycle hooks
---

It is extremely common in tests to want to perform some action before and after a test, or before and after all tests in the same file.
It is in these _lifecycle hooks_ that you would perform any setup/teardown logic required for a test.

Kotest provides a rich assortment of hooks that can be defined directly inside a spec.
For more advanced cases, such as writing distributable plugins or re-usable hooks, one can use [extensions](extensions/extensions.md).

At the end of this section is a list of the available hooks and when they are executed.

There are several ways to use hooks in Kotest:

#### DSL Methods

The first and simplest, is to use the DSL methods available inside a Spec which create and register a `TestListener` for you. For example, we can invoke `beforeTest` or `afterTest` (and others) directly alongside our tests.

```kotlin
class TestSpec : WordSpec({
  beforeTest {
    println("Starting a test $it")
  }
  afterTest { (test, result) ->
    println("Finished spec with result $result")
  }
  "this test" should {
    "be alive" {
      println("Johnny5 is alive!")
    }
  }
})
```

Behind the scenes, these DSL methods will create an instance of `TestListener`, overriding the appropriate functions, and ensuring that this test listener is registered to run.

You can use `afterProject` as a DSL method which will create an instance of `ProjectListener`, but there is no `beforeProject` because by the time the framework is at this stage of detecting a spec, the project has already started!

#### DSL methods with functions

Since these DSL methods accept functions, we can pull out logic to a function and re-use it in several places. The `BeforeTest` type used on the function definition is an alias
to `suspend (TestCase) -> Unit` to keep things simple. There are aliases for the types of each of the callbacks.

```kotlin
val startTest: BeforeTest = {
   println("Starting a test $it")
}

class TestSpec : WordSpec({

   // used once
   beforeTest(startTest)

   "this test" should {
      "be alive" {
         println("Johnny5 is alive!")
      }
   }
})

class OtherSpec : WordSpec({

   // used twice
   beforeTest(startTest)

   "this test" should {
      "fail" {
         fail("boom")
      }
   }
})
```

#### Overriding callback functions in a Spec

The second, related, method is to override the callback functions in the Spec. This is essentially just a variation on the first method.

```kotlin
class TestSpec : WordSpec() {
    override fun beforeTest(testCase: TestCase) {
        println("Starting a test $testCase")
    }

    init {
        "this test" should {
            "be alive" {
                println("Johnny5 is alive!")
            }
        }
    }
}
```

To understand all callbacks correctly
it's important to have a good understanding of
possible `TestType` values:

- `Container` - a container that can contain other tests,
- `Test` - a leaf test that cannot contain nested tests,
- `Dynamic` - can either be a container or a test
and is used when tests are added dynamically
via functionality such as property tests or data tests.

|Callback|Description|
|--------|-----------|
|beforeContainer|Invoked directly before each test with type `TestType.Container` is executed, with the `TestCase` instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.|
|afterContainer|Invoked immediately after a `TestCase` with type `TestType.Container` has finished, with the `TestResult` of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.<br/><br/>The callback will execute even if the test fails.
|beforeEach|Invoked directly before each test with type `TestType.Test` is executed, with the `TestCase` instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.|
|afterEach|Invoked immediately after a `TestCase` with type `TestType.Test` has finished, with the `TestResult` of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.<br/><br/>The callback will execute even if the test fails.
|beforeAny|Invoked directly before each test with any `TestType` is executed, with the `TestCase` instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.|
|afterAny|Invoked immediately after a `TestCase` with any `TestType` has finished, with the `TestResult` of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.<br/><br/>The callback will execute even if the test fails.
|beforeTest|Invoked directly before each test is executed with the `TestCase` instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.<br/><br/>This callback has the same behavior as `beforeAny`.|
|afterTest|Invoked immediately after a `TestCase` has finished with the `TestResult` of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.<br/><br/>The callback will execute even if the test fails.<br/><br/>This callback has the same behavior as `afterAny`.
|beforeSpec|Invoked after the Engine instantiates a spec to be used as part of a test execution.<br/><br/>The callback is provided with the `Spec` instance that the test will be executed under.<br/><br/>If a spec is instantiated multiple times - for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, then this callback will be invoked for each instance created, just before the first test (or only test) is executed for that spec.<br/><br/>This callback should be used if you need to perform setup each time a new spec instance is created.<br/><br/>If you simply need to perform setup once per class file, then use prepareSpec. This callback runs before any `beforeTest` functions are invoked.<br/><br/> When running in the default `SingleInstance` isolation mode, then this callback and `prepareSpec` are functionally the same since all tests will run in the same spec instance.|
|afterSpec|Is invoked after the `TestCase`s that are part of a particular spec instance have completed.<br/><br/>If a spec is instantiated multiple times - for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, then this callback will be invoked for each instantiated spec, after the tests that are applicable to that spec instance have returned.<br/><br/>This callback should be used if you need to perform cleanup after each individual spec instance. If you need to perform cleanup once per class file, then use `finalizeSpec.`<br/><br/>This callback runs after any `afterTest` callbacks have been invoked.<br/><br/>When running in the default `SingleInstance` isolation mode, then this callback and `finalizeSpec` are functionally the same since all tests will run in the same spec instance.<br/>In case there is any exception in `beforeSpec`, `afterSpec` will be skipped|
|prepareSpec|Called once per spec, when the engine is preparing to execute the tests for that spec. The `KClass` instance of the spec is provided as a parameter.<br/><br/>Regardless of how many times the spec is instantiated, for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, this callback will only be invoked once. If there are no active tests in a spec, then this callback will still be invoked.<br/><br/>When running in the default `SingleInstance` isolation mode, then this callback and `beforeSpec` are functionally the same since all tests will run in the same spec instance.|
|finalizeSpec|Called once per `Spec`, after all tests have completed for that spec.<br/><br/>Regardless of how many times the spec is instantiated, for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, this callback will only be invoked once.<br/><br/>The results parameter contains every `TestCase`, along with the result of that test, including tests that were ignored (which will have a `TestResult` that has `TestStatus.Ignored`).<br/><br/>When running in the default `SingleInstance` isolation mode, then this callback and `afterSpec` are functionally the same since all tests will run in the same spec instance.|
|beforeInvocation|Invoked before each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.<br/><br/>If you are running a test with the default single invocation then this callback is effectively the same as `beforeTest`.<br/><br/>_Note: If you have set multiple invocations _and_ multiple threads, then these callbacks will be invoked concurrently._|
|afterInvocation|Invoked after each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.<br/><br/>If you are running a test with the default single invocation then this callback is effectively the same as `afterTest`.<br/><br/>_Note: If you have set multiple invocations _and_ multiple threads, then these callbacks will be invoked concurrently._|

Notice that as far as `beforeAny` and `beforeTest` are just another name for the same functionality,
`beforeEach` is different.
Each of `beforeAny` and `beforeTest` will be invoked before both `TestType.Container` and `TestType.Test`,
whereas `beforeEach` will be invoked before any `TestType.Test`.
The same applies to `afterAny`, `afterTest` and `afterEach`.
