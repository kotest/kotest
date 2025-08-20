---
id: concurrency
title: Concurrency
slug: concurrency.html
---

!!! note "Kotest 5.0"
  This document describes the concurrency features introduced in Kotest 5.0 that were marked experimental and have been changed for Kotest 6.0.
  If you are using Kotest 6.0, please see [new concurrency documentation](https://kotest.io/docs/framework/concurrency6.0.html).


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

Kotest offers the ability to take advantage of multiple cores.
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



