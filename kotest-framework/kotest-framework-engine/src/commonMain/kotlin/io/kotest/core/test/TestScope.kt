package io.kotest.core.test

import io.kotest.common.KotestInternal
import io.kotest.core.spec.KotestTestScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * A test in Kotest is simply a function `suspend TestScope.() -> Unit`
 *
 * The [TestScope] receiver allows the test function to interact with the test engine at runtime.
 * For instance fetching details of the executing [TestCase] (such as timeouts, tags) or by
 * registering a dynamic nested test
 *
 * This context extends [CoroutineScope] giving the ability for any test to launch
 * coroutines directly, without requiring the user to supply a coroutine scope, and to retrieve
 * elements from the current [CoroutineContext] via [CoroutineContext.get]
 */
@KotestTestScope
interface TestScope : CoroutineScope {

   /**
    * The currently executing [TestCase].
    */
   val testCase: TestCase

   /**
    * Registers a [NestedTest] with the engine as a child of the current [testCase].
    *
    * Will throw if the current [testCase] is not a container test.
    */
   @KotestInternal
   suspend fun registerTestCase(nested: NestedTest)
}
