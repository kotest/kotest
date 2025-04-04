---
id: test_timeouts
title: Test Timeouts
slug: test-timeouts.html
sidebar_label: Test Timeouts
---


Kotest supports two types of test timeout. The first is the overall time for all invocations of a test. This is just called _timeout_.
The second is per individual run of a test, and this is called _invocation timeout_.


### Test Timeout


To set a test timeout, we can use test config:

```kotlin
class TimeoutTest : FunSpec({
   test("this test will timeout quickly!").config(timeout = 100.milliseconds) {
      // test here
   }
})
```

Alternatively, we can apply a test timeout for all tests in a spec file:

```kotlin
class TimeoutTest : FunSpec({

   timeout = 100.milliseconds

   test("this test will timeout quickly!") {
      // test here
   }

   test("so will this one!") {
      // test here
   }
})
```


:::note
The time taken for a test includes the execution time taken for nested tests, so factor this into your timeouts.
:::




### Invocation Timeout


Kotest can be configured to invoke a test multiple times. For example:

```kotlin
class TimeoutTest : DescribeSpec({

   describe("my test context") {
        it("run me three times").config(invocations = 3) {
            // this test will be invoked three times
        }
   }

})
```


We can then apply a timeout _per invocation_ using the `invocationTimeout` property.


```kotlin
class TimeoutTest : DescribeSpec({

   describe("my test context") {
        it("run me three times").config(invocations = 3, invocationTimeout = 60.milliseconds) {
            // this test will be invoked three times and each has a timeout of 60 milliseconds
        }
   }

})
```

In the previous example, each invocation must complete in 60 milliseconds or less. We can combine this with an overall
test timeout:


```kotlin
class TimeoutTest : DescribeSpec({

   describe("my test context") {
        it("run me three times").config(timeout = 100.milliseconds, invocations = 3, invocationTimeout = 60.milliseconds) {
            // this test will be invoked three times
        }
   }

})
```

Here we want all three tests to complete in 100 milliseconds or less, but allow any particular invocation to extend
up to 60 milliseconds.


We can apply invocation timeouts at the spec level just like test timeouts:



```kotlin
class TimeoutTest : FunSpec({

   invocationTimeout = 25.milliseconds

   test("foo") {
      // test here
   }

   test("bar") {
      // test here
   }
})
```


### Project wide settings

We can apply a test and/or invocation timeout for all tests in a module using project config.


```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val timeout = 100.milliseconds
    override val invocationTimeout = 33.milliseconds
}
```

These values will take affect unless overriden at either the spec or the test level.


:::tip
You can set a project wide timeout for tests and then override it per spec or per test
:::


### System Properties

Both test timeout and invocation timeouts can be set using system properties, with values in milliseconds.

* `kotest.framework.timeout` sets the combined test timeout
* `kotest.framework.invocation.timeout` sets the invocation test timeouts.
