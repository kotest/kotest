Listeners
==========

It is a common requirement to execute code before or after tests or specs. For instance, to start (or reset) and shutdown an embedded database required by a test.

Kotest provides multiple callbacks into the test lifecycle which are used for this purpose.

TestListener
------------

The main interface is _TestListener_. The following sections describe the callbacks available on this interface.



#### beforeTest

`beforeTest` is invoked directly before each test is executed with the `TestCase` instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.



#### afterTest

The `afterTest` callback is invoked immediately after a `TestCase` has finished with the `TestResult` of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case. The callback will execute even if the test fails.



#### beforeSpec

The `beforeSpec` callback is invoked after the Engine instantiates a spec to be used as part of a test execution.
The callback is provided with the `Spec` instance that the test will be executed under.

If a spec is instantiated multiple times - for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, then this callback will be invoked for each instance created, just before the first test (or only test) is executed for that spec.

This callback should be used if you need to perform setup each time a new spec instance is created.
If you simply need to perform setup once per class file, then use prepareSpec. This callback runs before any `beforeTest` functions are invoked.

When running in the default `SingleInstance` isolation mode, then this callback and `prepareSpec` are functionally the same since all tests will run in the same spec instance.



#### afterSpec

Is invoked after the `TestCase`s that are part of a particular spec instance have completed.

If a spec is instantiated multiple times - for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, then this callback will be invoked for each instantiated spec, after the tests that are applicable to that spec instance have returned.

This callback should be used if you need to perform cleanup after each individual spec instance. If you need to perform cleanup once per class file, then use `finalizeSpec.`

This callback runs after any `afterTest` callbacks have been invoked.

When running in the default `SingleInstance` isolation mode, then this callback and `finalizeSpec` are functionally the same since all tests will run in the same spec instance.



#### prepareSpec

Called once per spec, when the engine is preparing to execute the tests for that spec. The `KClass` instance of the spec is provided as a parameter.

Regardless of how many times the spec is instantiated, for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, this callback will only be invoked once. If there are no active tests in a spec, then this callback will still be invoked.

When running in the default `SingleInstance` isolation mode, then this callback and `beforeSpec` are functionally the same since all tests will run in the same spec instance.



#### finalizeSpec

Called once per `Spec`, after all tests have completed for that spec.

Regardless of how many times the spec is instantiated, for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, this callback will only be invoked once.

The results parameter contains every `TestCase`, along with the result of that test, including tests that were ignored (which will have a `TestResult` that has `TestStatus.Ignored`).

When running in the default `SingleInstance` isolation mode, then this callback and `afterSpec` are functionally the same since all tests will run in the same spec instance.



#### beforeInvocation

`beforeInvocation` is invoked before each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.
If you are running a test with the default single invocation then this callback is effectively the same as `beforeTest`.

_Note: If you have set multiple invocations _and_ multiple threads, then these callbacks will be invoked concurrently._

#### afterInvocation

`afterInvocation` is invoked after each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.
If you are running a test with the default single invocation then this callback is effectively the same as `afterTest`.

_Note: If you have set multiple invocations _and_ multiple threads, then these callbacks will be invoked concurrently._



How to use a TestListener
------------

There are several ways to use the methods in a test listener.

#### DSL Methods

The first and simplest, is to use the DSL methods available inside a Spec which create and register a test listener for you. For example, we can invoke `beforeTest` alongside our tests.

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
   init {

    override fun beforeTest(testCase: TestCase) {
      println("Starting a test $it")
    }

    "this test" should {
      "be alive" {
        println("Johnny5 is alive!")
      }
    }
  }
}
```

#### Standalone TestListener instances

The next method is to create a standalone implementation of the TestListener interface and register it. This is useful if you want to reuse a listener
that has several dependant functions (such as starting and stopping a resource).

```kotlin
class MyTestListener : TestListener {
   override suspend fun beforeSpec(spec:Spec) {
      // power up kafka
   }
   override suspend fun afterSpec(spec: Spec) {
      // shutdown kafka
   }
}


class TestSpec : WordSpec({
    listener(MyTestListener())
    // tests here
})
```

Any listeners registered on a `Spec` will be used for all tests in that spec (including factory tests and nested tests).

Maybe you want a listener to run for every spec in the entire project. To do that, you would register the listener via project config.
For more information on this see [ProjectConfig](#project-config).

Real Examples
------------

#### System Out Listener

A real example of a listener in example, is our `NoSystemOutListener` which throws an error if any output is written to standard out.

```kotlin
class MyTestSpec : DescribeSpec({

    listener(NoSystemOutListener)

    describe("All these tests should not write to standard out") {
        it("silence in the court") {
          println("boom") // failure
        }
    }
})
```

#### Timer Listener

Another example would be if we wanted to log the time taken for each test case. We can do this by using the `beforeTest` and `afterTest` functions
 as follows:

```kotlin
object TimerListener : TestListener {

  var started = 0L

  override fun beforeTest(testCase: TestCase): Unit {
    started = System.currentTimeMillis()
  }

  override fun afterTest(testCase: TestCase, result: TestResult): Unit {
    println("Duration of ${testCase.description} = " + (System.currentTimeMillis() - started))
  }
}
```

Then we can register like so:

```kotlin
class MyTestClass : FunSpec({
  listeners(TimerListener)
  // tests here
})
```

Or we could register it project wide:

```kotlin
object MyConfig : AbstractProjectConfig() {
    override fun listeners(): List<Listener> = listOf(TimerListener)
}
```


These functions will now be invoked for every test case inside the `MyTestClass` test class. Maybe you want
 this listener to run for every test in the entire project. To do that, you would register the listener with
 the project config singleton. For more information on this see [ProjectConfig](#project-config).
