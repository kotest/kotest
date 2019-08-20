package io.kotlintest.core

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestType
import io.kotlintest.core.specs.KotlinTestDsl

// used by intellij to detect junit 5 tests
expect annotation class Junit5EnabledIfSystemProperty constructor(val named: String, val matches: String)
expect annotation class Junit5TestFactory()

// used by the kotlin compiler to generate test methods, we use this for js impl
expect annotation class JsTest()

abstract class SuiteSpec : Spec {

  // this is a dummy method, so that the IDEs and compilers can "detect" this class as a test class
  @Junit5EnabledIfSystemProperty("foo", "woo")
  @Junit5TestFactory
  @JsTest
  fun kotlintestMarkerStub() {
  }

  private val rootTestCases = mutableListOf<TestCase>()

  override fun testCases(): List<TestCase> = rootTestCases

  override fun closeResources() {}

  fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
    rootTestCases.add(
      TestCase(
        Description.spec(this::class),
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
    suspend fun test(name: String, test: () -> Unit) {
      context.registerTestCase(name, this@SuiteSpec, { test() }, TestCaseConfig(), TestType.Test)
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
