package io.kotest.core.spec

import io.kotest.core.test.*
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

fun testContext(
   testCase: TestCase,
   coroutineContext: CoroutineContext
): TestContext = object : TestContext() {
   override val testCase: TestCase = testCase
   override suspend fun registerTestCase(test: NestedTest) {
      it(test.name) {
         GlobalScope.promise {
            val t = test.test
            testContext(TODO(), coroutineContext).t()
         }
      }
   }

   override val coroutineContext: CoroutineContext = coroutineContext
}

// we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
actual fun generateTests(rootTests: List<TestCase>) {

   fun runner(testCase: TestCase) = GlobalScope.promise {
      val t = testCase.test
      testContext(testCase, coroutineContext).t()
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

