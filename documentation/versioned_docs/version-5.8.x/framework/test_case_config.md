---
id: test_case_config
title: Test Case Config
slug: testcaseconfig.html
---


Each test can be configured with various parameters. After the test name, invoke the config function
 passing in the parameters you wish to set. The available parameters are:


* `invocations` - The number of times to run this test. Useful if you have a non-deterministic test and you want to run that particular test a set number of times to see if it eventually fails. A test will only succeed if all invocations succeed. Defaults to 1.
* `threads` - Allows the invocation of this test to be parallelized by setting the number of threads. Value must be less or equal of invocations value. Similarly, if you set invocations to a value equal to the number threads, then each invocation will have its own thread.
* `enabled` - If set to `false` then this test is [disabled](conditional_evaluation.md). Can be useful if a test needs to be temporarily ignored. You can also use this parameter with boolean expressions to run a test only under certain conditions.
* `enabledIf` - A function which provides the same ability as `enabled` but is lazily evaluated when the test case is due for execution.
* `timeout` - sets a timeout for this test. If the test has not finished in that time then the test fails. Useful for code that is non-deterministic and might not finish. Timeout is of type `kotlin.Duration` which can be instantiated like `2.seconds`, `3.minutes` and so on.
* `tags` - a set of tags that can be used to group tests (see detailed description below).
* `listeners` - register [test listeners](extensions/extensions.md) to run only on this test.
* `extensions` - register extensions to run only on this test.

An example of setting config on a test:

```kotlin
class MyTests : ShouldSpec() {
  init {
    should("return the length of the string").config(invocations = 10, threads = 2) {
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

You can also specify a default TestCaseConfig for all test cases of a Spec:

Overriding the defaultTestCaseConfig function:

```kotlin
class MySpec : StringSpec() {

  override fun defaultTestCaseConfig() = TestCaseConfig(invocations = 3)

  init {
    // your test cases ...
  }
}
```

Or via assignment to the defaultTestConfig val:

```kotlin
class FunSpecTest : FunSpec() {
  init {

    defaultTestConfig = TestCaseConfig(enabled = true, invocations = 3)

    test("FunSpec should support Spec config syntax in init{} block") {
      // ...
    }
  }
}
```
