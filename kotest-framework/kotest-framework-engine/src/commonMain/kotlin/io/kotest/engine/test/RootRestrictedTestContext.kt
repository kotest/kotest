package io.kotest.engine.test

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import kotlin.coroutines.CoroutineContext

/**
 * A [TestContext] for root level tests which disallows nested tests.
 */
class RootRestrictedTestContext(
   override val testCase: TestCase,
   override val coroutineContext: CoroutineContext
) : TestContext {

   init {
      require(testCase.parent == null) { "Only root level tests can be registered" }
   }

   override suspend fun registerTestCase(nested: NestedTest) {
      throw IllegalStateException("Spec styles that support nested tests are disallowed in kotest-js and kotest-native due to restrictions in the platforms. Please use FunSpec, StringSpec, or ShouldSpec and ensure that nested contexts are not used.")
   }
}
