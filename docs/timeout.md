# Timeouts

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



!!! info
    Timeouts include the time taken for nested tests.






We can specify the timeout at three levels.

## Test Level Timeouts

The most finely grained location for timeouts it on leaf tests directly.

```kotlin
class TimeoutTest : DescribeSpec({

   describe("my test context") {
        it("timeout after 750ms").config(timeout = 750.milliseconds, invocationTimeout = 250.milliseconds) {
        }
   }

})
```


## Spec Level Timeouts


Timeouts can be specified at the spec level for every test in that spec, unless overriden by the test case itself.


```kotlin
class TimeoutTest : DescribeSpec({

   timeout = 1250.milliseconds

   describe("I will timeout in 1250 millis") {
      it("And so will I") { }
      it("But I'm a little faster").config(timeout = 500.milliseconds) { }
   }

})
```


## Global Timeouts

Finally, we can set a global default for both timeout and invocationTimeout inside [project config](project_config.md).


```kotlin
object ProjectConfig : AbstractProjectConfig {
    override val timeout = 2.seconds
    override val invocationTimeout = 1.second
}
```

Global config is overriden by spec level and test case level values.
