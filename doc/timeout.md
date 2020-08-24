# Timeouts

Tests can have timeouts applied in several ways.

TODO

It has always been possible to add a timeout to a test at the global level or via test case config for each specific test:

```kotlin
 test("my test").config(timeout = 20.seconds) { }
```

But it has not previously been possible to override this as the spec level for all tests in that spec. Now you can.

```kotlin
class TimeoutTest : DescribeSpec({

   timeout = 1000

   describe("I will timeout in 1000 millis") {
      it("And so will I") { }
      it("But I'm a little faster").config(timeout = 500.milliseconds) { }
   }

})
```

Note: You can apply a spec level timeout and then override this per test case, as you can see in the example above.
The same functionality exists for invocatoin timeouts.
