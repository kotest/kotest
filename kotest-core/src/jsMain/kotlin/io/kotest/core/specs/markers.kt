package io.kotest.core.specs

import io.kotest.Description
import io.kotest.TestCase
import io.kotest.TestType
import io.kotest.core.TestContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

actual annotation class Junit5TestFactory
actual annotation class Junit5EnabledIfSystemProperty constructor(actual val named: String, actual val matches: String)

actual typealias JsTest = kotlin.test.Test

external fun describe(name: String, fn: () -> Unit)
external fun xdescribe(name: String, fn: () -> Unit)
external fun it(name: String, fn: () -> Any?)
external fun xit(name: String, fn: () -> Any?)

fun testContext(desc: Description,
                coroutineContext: CoroutineContext): TestContext = object : TestContext(coroutineContext) {

  override suspend fun registerTestCase(testCase: TestCase) {
    it(testCase.name) {
      GlobalScope.promise {
        val t = testCase.test
        testContext(desc.append(testCase.name), coroutineContext).t()
      }
    }
  }

  override fun description(): Description = desc
}

// we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
actual fun generateTests(rootTests: List<TestCase>) {

  fun runner(testCase: TestCase) = GlobalScope.promise {
    val t = testCase.test
    testContext(testCase.description, coroutineContext).t()
  }

  rootTests.forEach {
    when (it.type) {
      TestType.Container -> describe(it.name) { runner(it) }
      TestType.Test -> it(it.name) { runner(it) }
    }
  }
}

actual interface AutoCloseable {
   actual fun close()
}

