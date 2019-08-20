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

// these functions call out to the jasmine runner
// on the jvm these functions will be empty
expect fun generateTests(rootTests: List<TestCase>)

abstract class SpecParent : Spec {
  protected val rootTestCases = mutableListOf<TestCase>()
}

abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : SpecParent() {

  init {
    body()
  }

  // this is a dummy method, so that the IDEs and compilers can "detect" this class as a test class
  @Junit5EnabledIfSystemProperty("foo", "woo")
  @Junit5TestFactory
  fun intellijMarkerStub() {
  }

  // this is a dummy method, intercepted by the kotlin.js framework adapter to generate tests
  @JsTest
  fun kotlintestGenerateTests() {
    generateTests(rootTestCases.toList())
  }

  override fun testCases(): List<TestCase> = rootTestCases

  override fun closeResources() {}

  fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
    rootTestCases.add(
      TestCase(
        Description.fromSpecClass(this::class).append(name),
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
