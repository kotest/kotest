package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Returns the [TestLogger] that is embedded with this [TestScope], or returns null
 * if no such logger has been added to the context.
 */
@ExperimentalKotest
internal val TestScope.logger: TestLogger?
   get() = coroutineContext[TestScopeLoggingCoroutineContextElement]?.logger

@ExperimentalKotest
internal class TestScopeLoggingCoroutineContextElement(
   val logger: TestLogger
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestScopeLoggingCoroutineContextElement>
}
