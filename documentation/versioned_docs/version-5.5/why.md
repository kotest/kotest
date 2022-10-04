---
title: Why Kotest
sidebar_label: Why Kotest
slug: why-kotest.html
---

If you are moving from Java to Kotlin then this page outlines some of the advantages of using Kotest over the common Java test libraries.

## vs Junit

* Tight coroutine integration: Every test is a coroutine, therefore, you can invoke suspension methods without requiring `runBlocking` or other boilerplate
  * One line configuration to enable coroutine debugging for a test or for all tests
  * Callbacks allow modifying the coroutine context for child coroutines
  * Customize the coroutine dispatcher used
  * Launch multiple tests in parallel that do not block each other if they suspend

* Multiplatform support
  * JVM, Native and Javascript support
  * Same test structure across all targets

* Flexible test layout styles
  * Simple DSL avoids needing to wrap test names in backticks
  * Layout tests in unrestricted hierarchies.
  * Use styles like Javascript frameworks - `describe`/`it`
  * Or like Scalatest with `"my test" should "do foo"`
  * Or in a BDD style with `given` / `when` / `then`.

* Data Driven Testing
  * Create repeated tests simply and cleanly using regular Kotlin functions.
  * Use data classes as your data driven testing "row" object.

* Powerful concurrency utilities
  * Test that code completes within a given period using `eventually` without blocking threads.
  * Test that code passes continually for a given period of time using `continually`, also without blocking a thread.

* Callbacks
  * Functional callbacks that allow lifecycle events to be treated as functions.
  * Test interceptors allow around-advice style callbacks.

## vs AssertJ

* Assertions available as extension functions for IDE discoverability.
* Provides assertions for Kotlin specific types, such as `Sequence`, `Pair` and `Regex`.
* Most assertions are multiplatform enabled.
* Wrap multiple assertions to collect all errors before exiting the test.

