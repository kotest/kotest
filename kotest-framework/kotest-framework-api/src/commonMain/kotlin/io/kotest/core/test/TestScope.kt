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
 * The [TestScope] provides a [TestCase], which provides the details of the test at runtime,
 * such as tags, timeouts, assertion modes and so on.
 *
 * A test scope extends [CoroutineScope] giving the ability for any test to launch coroutines
 * directly from the test, without requiring a coroutine scope, and also to retrieve
 * elements from the current [CoroutineContext] via [CoroutineContext.get]
 */
@KotestDsl
interface TestScope : CoroutineScope {
   val testCase: TestCase
}
