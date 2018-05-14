Extensions <a name="interceptors"></a>
------------

If you need to execute some logic before and/or after each test case, then you can use an interceptor. This is for example useful to cleanup a database after the test have run.


In a spec class you can intercept the spec execution by overriding the `interceptors` property and providing a list of interceptors or by overriding `interceptSpec`.

A single test case can be intercepted by overriding `interceptTestCase` or by providing a list of interceptors in the `defaultTestCaseConfig` or in the `config` of a test case.

Interceptors replace `beforeEach`, `afterEach`, `beforeAll`, and `afterAll` functions from KotlinTest 1.x.

### Interceptor Execution Order

There are several points where you can hook in the test execution.

* ProjectConfig.extensions beforeAll
  * ProjectConfig.beforeAll
    * Spec.interceptors
      * Spec.interceptSpec
        * test case
      * Spec.interceptSpec (interceptor from above continued)
    * Spec.interceptors (interceptors from above continued)
  * ProjectConfig.afterAll
* ProjectConfig.extensions afterAll

The general philoshopy here, is that the closer an interceptor is to a test case, the closer it is to the test case in the execution order.

The execution order within an interceptor collection (`ProjectConfig.extentions`, `Spec.interceptors`) is from left to right.

### Intercepting a Test Case

Override `interceptTestCase` in a spec class to provide logic that should be called before and after each test case.

You could for example create a stopwatch by overriding `interceptTestCase`:

```kotlin
override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
  // before
  val started = System.currentTimeMillis()

  test() // don't forget to call test()!

  // after
  val finished = System.currentTimeMillis()
  val time = finished - started
  println("time [ms]: $time")
}
```
**Attention: Don't forget to call `test()` in your interceptor! Otherwise the test case wouldn't be called.**

As you can see, you can keep some state, since an interceptor is really just a function and all variables are kept in this scope for the duration of the execution.

You can even use interceptors to catch exceptions:

```kotlin
override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
  try {
    test()
  }
  catch (exception: SomeException) {
    // ok
  }
  catch (exception: Exception) {
    throw exception
  }
}
```

If you define a separate interceptor function, you add it to the `defaultTestCaseConfig` or to the `config` of a test case:

```kotlin
  val interceptorA: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
    println("A before")
    testCase()
    println("A after")
  }

  val interceptorB: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
    println("B before")
    testCase()
    println("B after")
  }

class MySpec : StringSpec {

  override val defaultTestCaseConfig = TestCaseConfig(interceptors = listOf(interceptorA, interceptorB))

  init {
    "should do something" {
      ...
    }.config(interceptors = listOf(interceptorA)) // overrides the interceptors from above
  }
}
```

### Intercepting a Spec

To run logic before and after a spec, you can override `interceptSpec`. The principle is the same as above:

```kotlin
protected fun interceptSpec(context: Spec, spec: () -> Unit) {
  println("before spec")
  spec() // don't forget to call spec()!
  println("after spec")
}
```

### Reusable Interceptors

Interceptors are just functions and can be reused between specs or even between projects. Just pass interceptors to the `config` on test case or spec level.

```kotlin
"should do it correctly" {
  ...
}.config(interceptors = listOf(myTestCaseInterceptor))
```

An interceptor would look like this:

```kotlin
val myTestCaseInterceptor: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
  println("before")
  testCase() // Don't forget to call testCase()!
  println("after")
}
```


The `beforeAll` methods of the extensions are executed in the order of extensions (from left to right). The `afterAll` methods are executed in reversed order (from right to left). If you had two extensions `listOf(A, B)` the order of execution would be:

* `A.beforeAll`
  * `B.beforeAll`
    * test execution
  * `B.afterAll`
* `A.afterAll`.

A `ProjectExtension` implementation would look like this:

```kotlin
object TestExtension : ProjectExtension {
  override fun beforeAll() {
    println("before all extension")
  }

  override fun afterAll() {
    println("after all extension")
  }
}
```
