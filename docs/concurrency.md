Concurrency
===========

Concurrency is at the heart of Kotlin, with the compiler support for continuations (suspend functions), enabling
the powerful coroutines library, in addition to the standard Java concurrency tools.

So it is expected that a Kotlin test framework should offer full support for executing tests concurrently.
Kotest offers the following features:

* The ability to launch tests in separate coroutines to support context switching when using suspending functions.
* The ability to configure multiple threads to take advantage of multi-core environments.

These two features are orthogonal but complimentary.


By default, Kotest will execute each test case sequentially using a single coroutine.
This means if a test inside a spec suspends or blocks, the whole test run will suspend or block until that test case resumes.

This is the safest default to use, since it places no burden or expectation on the user to write thread-safe tests. For example,
tests can share state or use instance fields which are not thread safe. It won't subject your tests to race conditions or require you to know Java's memory model.

However, it is understandable that many users will want to run tests concurrently to reduce the total execution time of their test suite.
Especially when testing code that suspends or blocks, the performance gains from allowing tests to run concurrently can be significant.



### Concurrency Mode

The first feature Kotest offers for concurrency is the ability to launch each specs and tests in separate coroutines.
Then, whenever a test suspends, another coroutine can be swapped onto that thread.

For example, if we were testing some kind of retry function, which sleeps for a period of time between invocations, the
test engine would simply be idling while the retry was suspended unless there were other coroutines that could be swapped onto the thread.

We can configure this feature by using the configuration flag `concurrencyMode` or the system property `kotest.framework.concurrency.mode`.

There are three modes: `Isolated`, `SpecConcurrent`, `SpecAndTestConcurrent`.
These essentially boil down to - _run everything sequentially_, _run specs concurrently but tests sequentially_, and _run specs and tests concurrently_.

!!! note "Caveats"
    This feature doesn't change the thread count, but allows for co-operative concurrency when using suspendable functions.
    If you are testing code that does not use coroutines or uses frequent blocking calls then this setting alone is not sufficient.
    See the section on multiple threads.

!!! warning "Blocking Methods"
    Usage of blocking calls - such as those from Java's IO Api or Thread.sleep - will block the thread and not allow that
    thread for other coroutines regardless of this setting.

    One can either try to avoid the use of blocking calls in favour of suspendable equivalents or wrap blocking calls
    inside a `withContext` block to move the offending code onto another dispatcher, such as `Dispatchers.IO`.

!!! tip "Recommended setting"
    The safest setting is the default `Isolated` mode as it requires no special consideration.
    `SpecConcurrent` offers the best bang for your buck in terms of speed increase vs extra effort.
    `SpecAndTestConcurrent` should be used only for the slowest of test suites that make extensive use of suspendable functions.


#### ConcurrencyMode.Isolated

This is the default setting, which runs all specs and tests in a single coroutine.

Consider the following specs:

```kotlin
class DefaultTest1 : FunSpec() {
   init {
      test("a") {
      }
      test("b") {
      }
   }
}

class DefaultTest2 : FunSpec() {
   init {
      test("c") {
      }
      test("d") {
      }
   }
}
```

In `Isolated` mode, this would perform like the following pseudo-code:

```kotlin
val spec1 = DefaultTest1()
spec1.testa()
spec1.testb()

val spec2 = DefaultTest1()
spec2.testc()
spec2.testd()
```

Each test is executed sequentially, and any suspension or blocking will suspend or block the entire test run.



#### ConcurrencyMode.SpecConcurrent

This setting runs each spec in a separate coroutine, but the tests inside each coroutine will execute sequentially.

It is common for tests inside the same class to re-use variables or use instance fields which are not thread safe.
Even when executing specs concurrently, we may not want tests inside the same spec to run concurrently.

Using the same example as before, in `SpecConcurrent` mode, this would perform like the following pseudo-code:

```kotlin
launch {
  val spec1 = DefaultTest1()
  spec1.testa()
  spec1.testb()
}

launch {
  val spec2 = DefaultTest1()
  spec2.testc()
  spec2.testd()
}
```

As you can see, the specs are launched in separate coroutines, and will execute concurrently
but the tests inside any particular spec will still execute sequentially.

If a test case suspends, the containing spec will suspend until that test case resumes, but other specs are free to run.

Specs must be thread-safe with respect to other specs, but test inside each spec are free to use shared state and
thread unsafe code that is contained within that spec.


#### ConcurrencyMode.SpecAndTestConcurrent

This setting runs each spec and each root test in their own separate coroutines.

With the same example as before, in `SpecAndTestConcurrent` mode, this would look like the following pseudo-code:

```kotlin
launch {
  val spec1 = DefaultTest1()
  launch {
    spec1.testa()
  }
  launch {
    spec1.testb()
  }
}

launch {
  val spec2 = DefaultTest1()
  launch {
    spec2.testc()
  }
  launch {
    spec2.testd()
  }
}
```

Now you can see that every spec and test is wrapped in their own coroutine.
This offers the highest level of concurrency, but requires all tests to be thread-safe.

!!! warning "Isolation mode"
    The default isolation mode for specs is _single instance per class_ - which means the same instance of
    the spec class is used for all tests of that class. If you have top level variables in your class, they must
    be thread safe, or you can change the isolation mode.


### @Isolate Safety Hatch

It is common to have many tests that are safe to run concurrently, while having some tests that must run in isolation.
Kotest supports this through the `@Isolate` annotation at the spec level.

Any specs annotated with this will always run sequentially, after all thread safe specs (those not annotated) have finished.




### Multiple Threads

The second feature Kotest offers for concurrency is the ability to take advantage of multiple cores.
When running in a multi-core environment, more than one spec could be executing in parallel.

Kotest supports this through the `parallelism` configuration option or the `kotest.framework.parallelism` system property.

By default, the value is set to 1 so all specs would execute in the same thread as mentioned earlier.
When we set this flag to a value greater than 1, multiple threads will be created for executing tests.

For example, setting this to K will (subject to caveats around blocking tests) allow up to K tests to be executing in parallel.

!!! note "Thread stickiness"
    When using multiple threads, all the tests and lifecycle callbacks of a given spec are guaranteed to be executed using the same thread.

!!! tip "Blocking calls"
    Setting this value higher than the number of cores offers a benefit if you are testing code that is using
    blocking calls and you are unable to move the calls onto another dispatcher.

!!! note
    Setting parallelism > 1 automatically enables `SpecConcurrent` concurrency mode unless another value is set explicitly.



