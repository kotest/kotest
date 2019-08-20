package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.TestCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

actual annotation class Junit5TestFactory

actual annotation class Junit5EnabledIfSystemProperty constructor(actual val named: String, actual val matches: String)

actual typealias JsTest = kotlin.test.Test

actual fun runTest(block: suspend (scope : CoroutineScope) -> Unit): dynamic = GlobalScope.promise { block(this) }

external fun describe(name: String, fn: () -> Unit)
external fun it(name: String, fn: () -> Any?)

fun testContext(d: Description,
                coroutineContext: CoroutineContext): TestContext = object : TestContext(coroutineContext) {

  override suspend fun registerTestCase(testCase: TestCase) {
    it(testCase.name) {
      launch {
        val t = testCase.test
        testContext(d.append(testCase.name), coroutineContext).t()
      }
    }
  }

  override fun description(): Description = d
}

actual fun container(name: String, fn: suspend TestContext.() -> Unit) {
  describe(name) {
    GlobalScope.launch {
      testContext(Description(emptyList(), name), coroutineContext)
    }
  }
}
