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

// these functions call out to the js test methods
// on the jvm these functions will be empty
expect fun generateTests(rootTests: List<TestCase>)

abstract class SpecParent : Spec {

  protected val rootTestCases = mutableListOf<TestCase>()

  // this var can be set to false by a subclass so no more root classes can be added
  protected var acceptingTopLevelRegistration = true

  override fun testCases(): List<TestCase> = rootTestCases

  protected fun createTestCase(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType) =
    TestCase(Description.fromSpecClass(this::class).append(name), this, test, sourceRef(), type, config)

  protected fun addTestCase(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType) {
    if (rootTestCases.any { it.name == name })
      throw IllegalArgumentException("Cannot add test with duplicate name $name")
    if (!acceptingTopLevelRegistration)
      throw IllegalArgumentException("Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly")
    rootTestCases.add(createTestCase(name, test, config, type))
  }

  override fun closeResources() {}

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
}

abstract class QuickSpec(body: QuickSpec.() -> Unit = {}) : SpecParent() {

  init {
    body()
  }

  fun t(name: String, test: suspend TestContext.() -> Unit) {
    rootTestCases.add(
      TestCase(
        Description.fromSpecClass(this::class).append(name),
        this,
        test,
        sourceRef(),
        TestType.Test,
        TestCaseConfig()
      )
    )
  }
}

abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : SpecParent() {

  init {
    body()
  }

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
