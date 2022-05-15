package io.kotest.framework.shared.test

import io.kotest.common.ExperimentalKotest
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class TestPath(val value: String)

@ExperimentalKotest
class TestPathContextElement(
   val testPath: TestPath,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestPathContextElement>
}
