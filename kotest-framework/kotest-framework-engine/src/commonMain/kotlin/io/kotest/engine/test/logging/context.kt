package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Returns the [TestLogger] that is embedded with this [TestContext], or returns null
 * if no such logger has been added to the context.
 */
@ExperimentalKotest
internal val TestContext.logger: TestLogger?
   get() = coroutineContext[TestContextLoggingCoroutineContextElement]?.logger

@ExperimentalKotest
internal class TestContextLoggingCoroutineContextElement(
   val logger: TestLogger
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestContextLoggingCoroutineContextElement>
}
