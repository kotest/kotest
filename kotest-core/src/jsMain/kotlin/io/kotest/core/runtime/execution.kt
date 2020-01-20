package io.kotest.core.runtime

import io.kotest.core.test.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

/**
 * Executes a [TestCase] and returns a Javascript [Promise] which
 * can be used by the underlying test framework for async support.
 */
fun TestCase.executeAsPromise(): Promise<Unit> = GlobalScope.promise {

   fun testContext(
      testCase: TestCase,
      coroutineContext: CoroutineContext
   ): TestContext = object : TestContext() {
      override val testCase: TestCase = testCase
      override val coroutineContext: CoroutineContext = coroutineContext
      override suspend fun registerTestCase(nested: NestedTest) {
         val t = nested.toTestCase(testCase.spec, testCase.description)
         when (t.type) {
            TestType.Container -> describe(t.name) { t.executeAsPromise() }
            TestType.Test -> it(t.name) { t.executeAsPromise() }
         }
      }
   }

   with(this) {
      testContext(this@executeAsPromise, coroutineContext).test()
   }
}
