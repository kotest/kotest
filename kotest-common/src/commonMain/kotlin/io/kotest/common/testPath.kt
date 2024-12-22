package io.kotest.common

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * A [TestPath] is a unique flattened string identifier for a test.
 * Note: A test path can include an optional spec name.
 */
data class TestPath(val value: String) {
   init {
      require(value.trim() == value) { "TestPath cannot have leading or trailing whitespace" }
   }
}

@ExperimentalKotest
class TestPathContextElement(
   val testPath: TestPath,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestPathContextElement>
}

@ExperimentalKotest
class TestNameContextElement(
   val testName: String,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<TestNameContextElement>
}
