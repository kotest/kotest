package io.kotest.common

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@ExperimentalKotest
class TestNameContextElement(
   val testName: String,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestNameContextElement>
}
