package io.kotest.core.test

import io.kotest.common.SoftDeprecated
import io.kotest.core.spec.KotestDsl
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

@SoftDeprecated("Renamed in 5.0 to TestScope")
typealias TestContext = TestScope

/**
 * A test in Kotest is simply a function `suspend TestScope.() -> Unit`
 *
 * The [TestScope] provides the [TestCase] at runtime, which contains resolved details of the test,
 * such as tags, timeouts, and so on on.
 *
 * This context extends [CoroutineScope] giving the ability for any test function to launch
 * coroutines directly, without requiring them to supply a coroutine scope, and to retrieve
 * elements from the current [CoroutineContext] via [CoroutineContext.get]
 */
@KotestDsl
interface TestScope : CoroutineScope {
   val testCase: TestCase
}
