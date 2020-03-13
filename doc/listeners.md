Listeners
==========

It is a common requirement to execute code before or after tests or test specs. For instance, starting and resetting an embedded database for each test.

Kotest provides several interfaces which provide callbacks around the test lifecycle which are used for this purpose.

TestListener
------------

The main interface is _TestListener_. The following table lists the methods this interface provides and when they are executed.

|Function|Parameters|Purpose|
|--------|-------|-------|
|beforeTest|`TestCase`|Is invoked each time before a Test Case is executed. If the test is marked as Ignored, this won't execute.|
|afterTest|`TestCase`, `TestResult`|Is invoked after each test with the test case that completed, and the result of that test. If the test is marked as Ignored, this won't execute. This will execute even if the test fails. |
|beforeSpec|`Spec`|Is invoked each time a Spec is started, before any `beforeTest` functions are invoked. |
|afterSpec|Is invoked each time a Spec completes, after all `afterTest` functions are invoked. |
|beforeProject|Is invoked as soon as the Test Engine is started.|
|afterProject|Is invoked as soon as the Test Engine has finished.|
|afterDiscovery|Is invoked after all the Spec classes have been discovered, but before any `beforeSpec` functions are called, and before any specs are instantiated by the Test Engine. |







  before and after the entire project. For this Kotest provides the `TestListener` interface. Instances of this interface can be registered
 with a `Spec` class or project wide by using [ProjectConfig](#project-config).

 This interface contains several functions,
 such as `beforeTest`, `afterTest`, `beforeSpec` and so on, which are used to hook into the lifecycle of the test engine.

Let's say we want to log the time taken for each test case. We can do this by using the `beforeTest` and `afterTest` functions
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

Then we can register this with a particular Spec, like so:

```kotlin
class MyTestClass : WordSpec() {

  override fun listeners(): List<TestListener> = listOf(TimerListener)

  // tests here

}
```

It's also important to notice that every `Spec` is also a `TestListener`, therefore you may override these functions directly in `Spec`.

```kotlin
class MyTestClass : WordSpec() {

    override fun beforeTest(testCase: TestCase) {
      // BeforeTest here
    }

}

```

These functions will now be invoked for every test case inside the `MyTestClass` test class. Maybe you want
 this listener to run for every test in the entire project. To do that, you would register the listener with
 the project config singleton. For more information on this see [ProjectConfig](#project-config).

The full list of the functions in the `TestListener` interface is as follows:

|Function|Purpose|
|--------|-------|
|beforeTest|Is invoked each time before a Test Case is executed. If the test is marked as Ignored, this won't execute.|
|afterTest|Is invoked each time after a Test Case is executed. If the test is marked as Ignored, this won't execute. This will execute even if the test fails |
|beforeSpec|Is invoked each time a Spec is started, before any `beforeTest` functions are invoked. |
|afterSpec|Is invoked each time a Spec completes, after all `afterTest` functions are invoked. |
|beforeSpecClass|Is invoked when the engine is preparing the spec to be executed. It will be executed only once, regardless of how many times the [Spec is instantiated](isolation_mode.md)
|afterSpecClass|Is invoked once all tests for a `Spec` have completed, regardless of how many times the [Spec is instantiated](isolation_mode.md)
|beforeProject|Is invoked as soon as the Test Engine is started.|
|afterProject|Is invoked as soon as the Test Engine has finished.|
|afterDiscovery|Is invoked after all the Spec classes have been discovered, but before any `beforeSpec` functions are called, and before any specs are instantiated by the Test Engine. |



