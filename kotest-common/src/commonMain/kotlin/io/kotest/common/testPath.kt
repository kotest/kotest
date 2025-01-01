package io.kotest.common

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * A [TestPath] is a unique flattened string identifier for a test.
 *
 * They begin with the spec fully qualified name and then include the hierarchy of test names
 * separated by a delimiter.
 *
 * For example, a test path might be "io.kotest.MyTest/test case parent -- test case"
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
