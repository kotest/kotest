package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.TestType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

actual annotation class Junit5TestFactory

actual annotation class Junit5EnabledIfSystemProperty constructor(actual val named: String, actual val matches: String)

actual typealias JsTest = kotlin.test.Test

external fun describe(name: String, test: () -> Unit)
external fun it(name: String, test: () -> Any?)

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

actual fun generateTests(rootTests: List<TestCase>) {
  fun runner(testCase: TestCase) {
    GlobalScope.launch {
      val context = testContext(testCase.description, coroutineContext)
      with(context) {
        val test = testCase.test
        test()
      }
    }
  }

  rootTests.forEach {
    when (it.type) {
      TestType.Container -> describe(it.name) { runner(it) }
      TestType.Test -> it(it.name) { runner(it) }
    }
  }
}
