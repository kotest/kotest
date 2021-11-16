package io.kotest.core.test

import io.kotest.common.SoftDeprecated
import io.kotest.core.spec.KotestDsl
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@SoftDeprecated("Renamed in 5.0 to TestScope")
typealias TestContext = TestScope

/**
 * A test in Kotest is simply a function `suspend TestScope.() -> Unit`
 *
 * The [TestScope] receiver provides the resolved runtime configuration for the test
 * currently being executed. For instance, the timeouts, tags, spec instance and so on.
 *
 * This context extends [CoroutineScope] giving the ability for any test function to launch
 * coroutines directly, without requiring them to supply a coroutine scope, and to retrieve
 * elements from the current [CoroutineContext] via [CoroutineContext.get]
 */
@KotestDsl
interface TestScope : CoroutineScope {

   /**
    * The currently executing [TestCase].
    */
   val testCase: TestCase
}
