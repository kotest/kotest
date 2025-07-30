---
id: concurrency6
title: Concurrency
slug: concurrency6.html
---

:::note
This document describes the new concurrency features introduced in Kotest 6.0.
If you are using an earlier version of Kotest, please refer to the [previous concurrency documentation](https://kotest.io/docs/framework/concurrency.html).
:::

Concurrency is at the heart of Kotlin, with compiler support for continuations (suspend functions), enabling
the powerful coroutines library, in addition to the standard Java concurrency tools.

So it is expected that a Kotlin test framework should offer full support for executing tests concurrently,
whether that is through traditional blocking calls or suspendable functions.

Kotest offers the following features:

* The ability to launch specs and tests concurrently.
* The ability to specify the coroutine dispatcher used to execute tests.
* The ability to run tests that use blocking APIs on a separate thread just for that test.

These features are orthogonal but complimentary.

By default, Kotest will execute each test case sequentially using `Dispatchers.Default`.
This means if a test suspends or blocks, the whole test suite will suspend or block until that test resumes.

This is the safest default to use, since it places no burden or expectation on the user to write thread-safe tests.
For example, tests can share state or use instance fields which are not thread safe. It won't subject your tests to
race conditions or require you to know Java's memory model. Specs can use before and after methods confidently knowing
they won't interfere with each other.

However, some users will want to run tests concurrently to reduce the total execution time of their test suite.
This is especially true when testing code that suspends or blocks - the performance gains from allowing tests to run
concurrently can be significant.

## Concurrency Modes

:::note
The concurrency modes described below are only available on the JVM platform.
On other platforms, tests will always run sequentially.
:::


Kotest provides two types of concurrency modes:

1. **Spec Concurrency Mode** - Controls how specs (test classes) are executed in relation to each other
2. **Test Concurrency Mode** - Controls how root tests within a spec are executed in relation to each other.

### Spec Concurrency Mode

Spec concurrency mode determines whether multiple specs can be executed at the same time. There are three options:

* **Sequential** - All specs are executed sequentially (default mode)
* **Concurrent** - All specs are executed concurrently
* **LimitedConcurrency(max: Int)** - Specs are executed concurrently up to a given maximum number

You can configure the spec concurrency mode in your project config:

```kotlin
class MyProjectConfig : AbstractProjectConfig() {
    override val specExecutionMode = SpecExecutionMode.Concurrent
}
```

Or for limited concurrency:

```kotlin
class MyProjectConfig : AbstractProjectConfig() {
    override val specExecutionMode = SpecExecutionMode.LimitedConcurrency(4) // Run up to 4 specs concurrently
}
```

### Test Concurrency Mode

Test concurrency mode determines whether multiple root tests within a spec can be executed at the same time.
Note that nested tests (tests defined within other tests) are not affected by this setting; they will always run sequentially.

There are three options:

* **Sequential** - All tests are executed sequentially (default mode)
* **Concurrent** - All tests are executed concurrently
* **LimitedConcurrency(max: Int)** - Tests are executed concurrently up to a given maximum number

You can configure the test concurrency mode at different levels:

#### Project-wide configuration

This will apply for all specs and tests in the project unless overridden at a lower level.

```kotlin
class MyProjectConfig : AbstractProjectConfig() {
    override val testExecutionMode = TestExecutionMode.Concurrent
}
```

#### Package-level configuration

Package-level configuration allows you to set the test execution mode for all specs in a specific package,
and is only available on the JVM platform.

```kotlin
class MyPackageConfig : AbstractPackageConfig() {
    override val testExecutionMode = TestExecutionMode.Concurrent
}
```

#### Spec-level configuration

You can configure test concurrency mode for a specific spec in two ways:

1. By overriding the `testExecutionMode()` function:

```kotlin
class MySpec : StringSpec() {
    override fun testExecutionMode() = TestExecutionMode.Concurrent

    // tests...
}
```

2. By setting the `testExecutionMode` property:

```kotlin
class MySpec : StringSpec() {
    init {
        testExecutionMode = TestExecutionMode.Concurrent

        // tests...
    }
}
```

## Examples

### Example: Running tests within a spec concurrently

```kotlin
class ConcurrentTestsSpec : StringSpec({

    // Configure this spec to run tests concurrently
    testExecutionMode = TestExecutionMode.Concurrent

    "test 1" {
        // This test will run concurrently with other tests
        delay(1000)
        // assertions...
    }

    "test 2" {
        // This test will run concurrently with other tests
        delay(500)
        // assertions...
    }

    "test 3" {
        // This test will run concurrently with other tests
        delay(200)
        // assertions...
    }
})
```

### Example: Limited concurrency for tests

```kotlin
class LimitedConcurrencySpec : StringSpec({
    // Configure this spec to run up to 2 tests concurrently
    testExecutionMode = TestExecutionMode.LimitedConcurrency(2)

    // tests...
})
```

### Example: Combining spec and test concurrency modes

```kotlin
class MyProjectConfig : AbstractProjectConfig() {
    // Run up to 3 specs concurrently
    override val specExecutionMode = SpecExecutionMode.LimitedConcurrency(3)

    // By default, run tests sequentially within each spec
    override val testExecutionMode = TestExecutionMode.Sequential
}

// Override the test execution mode for a specific spec
class ConcurrentTestsSpec : StringSpec({
    // This spec will run its tests concurrently
    testExecutionMode = TestExecutionMode.Concurrent

    // tests...
})
```

## Coroutine Dispatcher Factory

Kotest allows you to customize the coroutine dispatcher used for executing specs and tests through the
`CoroutineDispatcherFactory` feature. This gives you fine-grained control over the execution context of your tests.

The `CoroutineDispatcherFactory` interface provides methods to switch the `CoroutineDispatcher` used for:

1. Spec callbacks (like `beforeSpec` and `afterSpec`)
2. Test case execution

### How It Works

The `CoroutineDispatcherFactory` interface has two main methods:

```kotlin
interface CoroutineDispatcherFactory {
  // For spec callbacks
  suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T

  // For test case execution
  suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T

  // Closes resources when the test engine completes
  fun close() {}
}
```

When a `CoroutineDispatcherFactory` is configured, Kotest will use it to determine which dispatcher to use when
executing specs and tests.

### Configuration Options

You can configure a `CoroutineDispatcherFactory` at different levels:

#### Project-wide configuration

```kotlin
class MyProjectConfig : AbstractProjectConfig() {
    override val coroutineDispatcherFactory = ThreadPerSpecCoroutineContextFactory
}
```

#### Spec-level configuration

```kotlin
class MySpec : StringSpec() {
    // Option 1: Using property
    init {
        coroutineDispatcherFactory = ThreadPerSpecCoroutineContextFactory

        // tests...
    }

    // Option 2: Using function
    override fun coroutineDispatcherFactory() = ThreadPerSpecCoroutineContextFactory
}
```

### Built-in Implementations

Kotest provides a built-in implementation called `ThreadPerSpecCoroutineContextFactory` that creates a dedicated thread
per spec.

This implementation:
- Creates a dedicated thread for each spec
- Uses that thread as the coroutine dispatcher for the spec and all its tests
- Shuts down the thread when the spec completes

### Custom Implementation Example

You can create your own custom implementation to suit your specific needs:

```kotlin
object CustomDispatcherFactory : CoroutineDispatcherFactory {

   // A fixed thread pool with 4 threads
   private val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

   override suspend fun <T> withDispatcher(spec: Spec, f: suspend () -> T): T {
      return withContext(dispatcher) {
         f()
      }
   }

   override suspend fun <T> withDispatcher(testCase: TestCase, f: suspend () -> T): T {
      return withContext(dispatcher) {
         f()
      }
   }

   override fun close() {
      dispatcher.close()
   }
}
```

### Use Cases

The `coroutineDispatcherFactory` feature is useful for:

1. **Performance optimization**: Using a dedicated thread per spec can improve performance by reducing context switching
2. **Resource isolation**: Ensuring each spec runs on its own thread can help isolate tests from each other
3. **Custom threading models**: Implementing specific threading strategies for your test suite
4. **Testing with specific dispatchers**: Testing code that behaves differently on different dispatchers

## Blocking Test Mode

When working with blocking code in tests, you may encounter issues with timeouts not working as expected.
This is because coroutine timeouts are cooperative by nature, meaning they rely on the coroutine
to yield control back to the scheduler.

To address this issue, Kotest provides a `blockingTest` mode that can be enabled on a per-test basis:

```kotlin
"test with blocking code" {
    // Enable blocking test mode for this test
    blockingTest = true

    // Your test with blocking code...
    Thread.sleep(1000) // Example of blocking code
}
```

When `blockingTest` is set to true:
* Execution will switch to a dedicated thread for the test case
* This allows the test engine to safely interrupt tests via Thread.interrupt when they time out
* Other tests can continue running concurrently if configured to do so

### Example: Using blockingTest mode with timeouts

```kotlin
class BlockingTestSpec : StringSpec({
    "test with timeout and blocking code".config(blockingTest = true, timeout = 500.milliseconds) {
        // This blocking call would normally prevent the timeout from working
        // With blockingTest = true, the test will be interrupted after 500ms
        Thread.sleep(1000)
    }
})
```

:::tip
The `blockingTest` mode is only necessary when you're using blocking calls in your tests.
For tests that use suspending functions, the regular timeout mechanism works fine without needing to enable this mode.
:::
