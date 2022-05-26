package io.kotest.common

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class TestPath(val value: String)

@ExperimentalKotest
class TestPathContextElement(
   val testPath: TestPath,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestPathContextElement>
}
