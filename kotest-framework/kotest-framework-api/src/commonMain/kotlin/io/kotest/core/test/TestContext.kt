package io.kotest.core.test

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * A test in Kotest is simply a function `suspend TestContext.() -> Unit`
 *
 * The [TestContext] receiver allows the test function to interact with the test engine at runtime.
 * For instance fetching details of the executing test case (such as timeouts, tags),
 * registering a dynamic nested test, or adding a test lifecycle callback.
 *
 * This context extends [CoroutineScope] giving the ability for any test function to launch
 * coroutines directly, without requiring them to supply a coroutine scope, and to retrieve
 * elements from the current [CoroutineContext] via [CoroutineContext.get]
 */
interface TestContext : CoroutineScope {

   /**
    * The currently executing [TestCase].
    */
   val testCase: TestCase

   /**
    * Registers a [NestedTest] with the engine.
    *
    * Will throw if the current test is not a container test.
    */
   suspend fun registerTestCase(nested: NestedTest)
}
