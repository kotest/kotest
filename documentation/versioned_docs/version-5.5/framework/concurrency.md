---
id: concurrency
title: Concurrency
slug: concurrency.html
---



Concurrency is at the heart of Kotlin, with compiler support for continuations (suspend functions), enabling
the powerful coroutines library, in addition to the standard Java concurrency tools.

So it is expected that a Kotlin test framework should offer full support for executing tests concurrently, whether that is through
traditional blocking calls or suspendable functions.

Kotest offers the following features:

* The ability to launch specs and tests concurrently in separate coroutines to support context switching when using suspending functions.
* The ability to configure multiple threads to take advantage of multi-core environments and to allow for calls that use blocking APIs.

These two features are orthogonal but complimentary.


By default, Kotest will execute each test case sequentially using a single thread.
This means if a test inside a spec suspends or blocks, the whole test run will suspend or block until that test case resumes.

This is the safest default to use, since it places no burden or expectation on the user to write thread-safe tests. For example,
tests can share state or use instance fields which are not thread safe. It won't subject your tests to race conditions or require you to know Java's memory model. Specs can use before and after methods confidently knowing they won't interfere with each other.

However, it is understandable that many users will want to run tests concurrently to reduce the total execution time of their test suite.
This is especially true when testing code that suspends or blocks - the performance gains from allowing tests to run concurrently can be significant.



## Concurrency Mode

Kotest offers the ability to launch each spec and/or test in separate coroutines that can run concurrently.
Then, whenever a test suspends, another coroutine can be swapped onto that thread.

For example, if we were testing some kind of retry function, which sleeps for a period of time between invocations, the
test engine would simply be idling while the retry was suspended unless there were other coroutines that could be swapped onto the thread.

We can configure this feature by setting the configuration field `concurrencyMode` or the system property `kotest.framework.concurrency.mode`.

There are four values:

* `None` - Launch specs and tests **sequentially**.
* `Spec` - Launch specs **concurrently**, but tests within each spec **sequentially**.
* `Test` - Launch specs **sequentially**, but tests within each spec **concurrently**.
* `All` - Launch **both** specs and tests **concurrently**.

:::note "Caveats"
This setting does not change the thread count, but allows for co-operative concurrency when using suspendable functions.
If you are testing code that does not use coroutines or uses frequent blocking calls then this setting alone is not sufficient.
See the section on multiple threads.
:::

:::caution "Blocking Methods"
Usage of blocking calls - such as those from Java's IO Api or Thread.sleep - will block the thread and not allow that
thread for other coroutines regardless of this setting.

One can either try to avoid the use of blocking calls in favour of suspendable equivalents or wrap blocking calls
inside a `withContext` block to move the offending code onto another dispatcher, such as `Dispatchers.IO`.
:::


### ConcurrencyMode.None

This is the default setting, which launches all specs and tests sequentially. That is, the next spec and test are launched
only when the previous one completes.

Consider the following specs:

```kotlin
class MyTest1 : FunSpec() {
   init {
      test("a") {
      }
      test("b") {
      }
   }
}

class MyTest2 : FunSpec() {
   init {
      test("c") {
      }
      test("d") {
      }
   }
}
```

When using `ConcurrencyMode.None`, this would perform like the following pseudo-code:

```kotlin
val spec1 = MyTest1()
spec1.testa()
spec1.testb()

val spec2 = MyTest2()
spec2.testc()
spec2.testd()
```

Suspension or blocking will suspend or block the entire test run. All callback methods such as `beforeTest` and `afterSpec` are
guaranteed to run in isolation.


### ConcurrencyMode.Spec

This setting launches each spec at once, but tests inside each coroutine sequentially.

It is common for tests inside the same class to re-use variables or use instance fields which are not thread safe.
Even when executing specs concurrently, we may not want tests inside the same spec to run concurrently.

Using the same example as before, using `ConcurrencyMode.Spec`, this would perform like the following pseudo-code:

```kotlin
launch { // coroutine 1
  val spec1 = MyTest1()
  spec1.testa() // a suspend here would suspend spec1 only
  spec1.testb() // this test will only run once testa has completed
}

launch { // coroutine 2
  val spec2 = MyTest2()
  spec2.testc() // a suspend here would suspend spec2 only
  spec2.testd() // this test will only run once testc has completed
}
```

As you can see, the two specs are launched at the same time, and will execute concurrently
but the tests inside any particular spec will still execute sequentially.

If a test case suspends, only the containing spec will suspend until that test case resumes, but other spec coroutines are free to run.

Specs must be thread-safe with respect to other specs, but tests inside each spec are free to use shared state and
thread unsafe code that is contained within that spec. Test level callback methods such as `beforeTest` and `afterTest` are
guaranteed to run in isolation, but spec level callback methods such as `beforeSpec` and `afterSpec` can run concurrently.


### ConcurrencyMode.Test

This setting launches each spec sequentially, but each root test inside a spec concurrently.

With the same example as before, in `ConcurrencyMode.Test` mode, this would look like the following pseudo-code:

```kotlin
coroutineScope { // the scope ensures we wait for the whole spec to complete
  val spec1 = MyTest1()
  launch { // both tests launched at the same time
    spec1.testa()
  }
  launch {
    spec1.testb()
  }
}

coroutineScope { // the scope ensures we wait for the whole spec to complete
  val spec2 = MyTest2()
  launch { // both tests launched at the same time
    spec2.testc()
  }
  launch {
    spec2.testd()
  }
}
```

As you can see, the first spec will launch all of its tests at the same time, then suspend until they complete.
Only after all the contained tests have completed, will the next spec launch.
This mode is useful if you need thread safety between specs but tests inside each spec can run thread safe.

Test level callback methods such as `beforeTest` and `afterTest` can run concurrently,
but spec level callback methods such as `beforeSpec` and `afterSpec` are guaranteed to run in isolation.

:::caution "Isolation mode"
The default isolation mode for specs is _single instance per class_ - which means the same instance of
the spec class is used for all tests of that class. If you have top level variables in your class, they must
be thread safe.
:::


### ConcurrencyMode.All

This setting launches each spec concurrently and each test inside each spec concurrently. In effect, this is like
throwing all the tests into one giant pot and launching them all at the same time.

With the same example as before, in `ConcurrencyMode.All` mode, this would look like the following pseudo-code:

```kotlin
launch {
  val spec1 = MyTest1()
  launch {
    spec1.testa()
  }
  launch {
    spec1.testb()
  }
}

launch {
  val spec2 = MyTest2()
  launch {
    spec2.testc()
  }
  launch {
    spec2.testd()
  }
}
```

Now you can see that every spec and all tests in those specs are launched immediately.

All callback methods such as `beforeTest` and `afterSpec` can run concurrently. Users must ensure all callback is thread safe.

!!! warning
    This setting will give the maximum possible performance, but every single test must be thread safe (see @Isolate)

!!! warning "Isolation mode"
    The default isolation mode for specs is _single instance per class_ - which means the same instance of
    the spec class is used for all tests of that class. If you have top level variables in your class, they must
    be thread safe.




### @Isolate Safety Hatch

It is common to have many tests that are safe to run concurrently, while having some tests that must run in isolation.
Kotest supports this through the `@Isolate` annotation at the spec level.

Any specs annotated with this will always run sequentially, after all other specs (those not annotated) have finished.




### Multiple Threads

The second feature Kotest offers for concurrency is the ability to take advantage of multiple cores.
When running in a multi-core environment, more than one spec could be executing in parallel.

Kotest supports this through the `parallelism` configuration setting or the `kotest.framework.parallelism` system property.

By default, the value is set to 1 so that the test engine would use a single thread for the entire test run.
When we set this flag to a value greater than 1, multiple threads will be created for executing tests.

For example, setting this to K will (subject to caveats around blocking tests) allow up to K tests to be executing in parallel.

This setting has no effect on Javascript tests.

!!! note "Thread stickiness"
    When using multiple threads, all the tests of a particular spec (and the associated lifecycle callbacks) are guaranteed to be executed in the same thread.
    In other words, different threads are only used across different specs.

!!! tip "Blocking calls"
    Setting this value higher than the number of cores offers a benefit if you are testing code that is using
    blocking calls and you are unable to move the calls onto another dispatcher.

!!! note
    Setting parallelism > 1 automatically enables `Spec` concurrency mode unless another concurrency mode is set explicitly.



