package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestType
import io.kotlintest.core.specs.KotlinTestDsl
import kotlinx.coroutines.CoroutineScope

// used by intellij to detect junit 5 tests
expect annotation class Junit5EnabledIfSystemProperty constructor(val named: String, val matches: String)
expect annotation class Junit5TestFactory()

// used by the kotlin compiler to generate test methods, we use this for js impl
expect annotation class JsTest()

// these functions call out to the jasmine runner
// on the jvm these functions will be empty
expect fun container(name: String, fn: suspend TestContext.() -> Unit)

abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : Spec {

  init {
    body()
  }

  // this is a dummy method, so that the IDEs and compilers can "detect" this class as a test class
  @Junit5EnabledIfSystemProperty("foo", "woo")
  @Junit5TestFactory
  fun intellijMarkerStub() {
  }

  // this is a dummy method, intercepted by the kotlin.js adapter to generate tests
  @JsTest
  fun kotlintestGenerateTests() {
    rootTestCases.forEach { container(it.name, it.test) }
  }

  private val rootTestCases = mutableListOf<TestCase>()

  override fun testCases(): List<TestCase> = rootTestCases

  override fun closeResources() {}

  fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
    rootTestCases.add(
      TestCase(
        Description.spec(this::class).append(name),
        this,
        { SuiteScope(this).test() },
        sourceRef(),
        TestType.Container,
        TestCaseConfig()
      )
    )
  }

  @KotlinTestDsl
  inner class SuiteScope(val context: TestContext) {
    suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
      context.registerTestCase(name, this@SuiteSpec, test, TestCaseConfig(), TestType.Test)
    }
  }
}

class MyTests : SuiteSpec() {
  init {
    suite("validate email") {
      test("with @ sign") {

      }
    }
  }
}

expect fun runTest(block: suspend (scope : CoroutineScope) -> Unit)
