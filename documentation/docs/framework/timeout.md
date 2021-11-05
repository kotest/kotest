---
title: Timeouts
slug: timeouts.html
---


Tests have two types of timeout that can be applied. The first is the overall time for all invocations of a test. This is just called _timeout_.
The second is per individual run of a test, and this is called _invocation timeout_.

Kotest can be configured to invoke a test multiple times. For example,

```kotlin
class TimeoutTest : DescribeSpec({

   describe("my test context") {
        it("run me three times").config(invocations = 3) {
            // some slow network test that takes 1500 millis
        }
   }

})
```

In this case, a _timeout_ of 2000 millis would cause the test to fail, because the total run time would be 4500 millis.
Whereas an _invocation timeout_ of 2000 millis would not cause the test to fail, because each individual run is 1500 millis.




:::note
The time taken for a test includes the execution time taken for nested tests, so factor this into your timeouts.
:::





We can specify the timeout at three levels.

## Test Level Timeouts

The most finely grained location for timeouts it on leaf tests directly.

```kotlin
class TimeoutTest : DescribeSpec({

   describe("my test context") {
        it("timeout after 750ms").config(timeout = Duration.milliseconds(750), invocationTimeout = Duration.milliseconds(250)) {
        }
   }

})
```


## Spec Level Timeouts


Timeouts can be specified at the spec level for every test in that spec, unless overriden by the test case itself.


```kotlin
class TimeoutTest : DescribeSpec({

   timeout = Duration.milliseconds(1250)

   describe("I will timeout in 1250 millis") {
      it("And so will I") { }
      it("But I'm a little faster").config(timeout = Duration.milliseconds(500)) { }
   }

})
```


## Global Timeouts

We can set global config in two ways - via system properties or by project config.



### System Property

To set the global timeout or invocation timeout at the command line, use the system property `kotest.framework.timeout` and `kotest.framework.invocation.timeout` with a value in milliseconds.



### Project Config


We can set a global default for both timeout and invocationTimeout inside [project config](project_config.md).


```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val timeout = Duration.seconds(2)
    override val invocationTimeout = Duration.seconds(1)
}
```

Global config is overridden by spec level and test case level values.
