---
id: test_case_config
title: Test Case Config
slug: testcaseconfig.html
---


Each test can be configured with various parameters. After the test name, invoke the config function
passing in the parameters you wish to set. The available parameters are:

* `invocations` - The number of times to run this test. Useful if you have a non-deterministic test and you want to run
  that particular test a set number of times to see if it eventually fails. A test will only succeed if all invocations
  succeed. Defaults to 1.
* `enabled` - If set to `false` then this test is [disabled](conditional_evaluation.md). Can be useful if a test needs
  to be temporarily ignored. You can also use this parameter with boolean expressions to run a test only under certain
  conditions.
* `enabledIf` - A function which provides the same ability as `enabled` but is lazily evaluated when the test case is
  due for execution.
* `timeout` - sets a timeout for this test. If the test has not finished in that time then the test fails. Useful for
  code that is non-deterministic and might not finish. Timeout is of type `kotlin.Duration`. Note, if you have multiple
  invocations of this test then this timeout is applied to the sum of all invocations.
* `invocationTimeout` - sets a timeout for each invocation of this test. If the test has not finished in that time then
  the test fails. Useful for code that is non-deterministic and might not finish. Timeout is of type `kotlin.Duration`.
  Note, if you have multiple invocations then this timeout is applied to each invocation separately.
* `tags` - a set of tags that can be used to group tests (see detailed description below).
* `listeners` - register [test listeners](extensions/extensions.md) to run only on this test.
* `extensions` - register extensions to run only on this test.
* `severity` - applies a severity level used by the allure extension and other extensions that support it.
* `failfast` - if set to `true` then the test engine will stop running tests in this spec after the first failure.
* `assertSoftly` - if set to `true` then the test engine will run assertions in soft mode.
  See [soft assertions](../assertions/soft_assertions.md) for more details.
* `blockingTest` - When set to true, execution will switch to a dedicated thread for each test case in this spec,
  therefore allowing the test engine to safely interrupt tests via Thread.interrupt when they time out. This is useful
  if you are testing blocking code and want to use timeouts because coroutine timeouts are cooperative by nature.
* `retries` - The number of times to retry a test if it fails.
* `retryDelay` - The delay between retries. Delay is of type `kotlin.Duration`.
* `coroutineDebugProbes` - If set to `true` then the test engine will enable coroutine debugging probes.
  See [debugging coroutines](./coroutines/coroutine_debugging.md)) for more details.

An example of setting config on a test:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string").config(invocations = 10) {
      "sammy".length shouldBe 5
      "".length shouldBe 0
    }
  }
}
```

```kotlin
class MyTests : WordSpec() {
  init {
    "String.length" should {
      "return the length of the string".config(timeout = 2.seconds) {
        "sammy".length shouldBe 5
        "".length shouldBe 0
      }
    }
  }
}
```

```kotlin
class FunSpecTest : FunSpec() {
  init {
    test("FunSpec should support config syntax").config(tags = setOf(Database, Linux)) {
      // ...
    }
  }
}
```

You can also specify a `DefaultTestConfig` which will be used as the fallback for all test cases in a spec,
unless overridden at the test level.

Set the defaultTestConfig val:

```kotlin
class FunSpecTest : FunSpec() {
  init {

    defaultTestConfig = DefaultTestConfig(enabled = true, invocations = 3)

    test("this test would run 3 times") {
      // ...
    }

    test("this test would run 1 time because it is overriden at the test level").config(invocations = 1) {
      // ...
    }
  }
}
```
