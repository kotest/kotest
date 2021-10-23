package com.sksamuel.kotest.engine.interceptors

import io.kotest.common.ExperimentalKotest
import io.kotest.common.KotestInternal
import io.kotest.common.runBlocking
import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestContext
import io.kotest.engine.TestEngine
import io.kotest.engine.TestEngineConfig
import io.kotest.engine.TestSuite
import io.kotest.engine.extensions.createTestEngineConfigFiltersProcessor
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

private val mut = Mutex()
private val executed = mutableListOf<String>()

internal suspend fun TestContext.testAndIncrementCounter() {
   1 shouldBe 1 // fake assertion so tests don't fail from fail on no assertion setting if it's set
   mut.withLock { executed.add(this.testCase.name.testName) }
}

private suspend fun numberOfTestsRunShouldBe(expected: Int) = mut.withLock {
   executed.size shouldBe expected
}

private suspend fun testsRunShouldBe(vararg name: String) = mut.withLock {
   executed shouldContainExactly name.toList()
}

@OptIn(KotestInternal::class)
private val baseConfig = TestEngineConfig(
   NoopTestEngineListener, listOf(), configuration, listOf(), listOf(), null
)

@ExperimentalKotest
private val testSuite = listOf<KClass<out Spec>>(
   DistantFutureSciFiTests::class,
   NearFutureSciFiTests::class,
   io.kotest.engine.extensions.bar.TestEngineConfigFiltersInterceptorInnerTests::class,
   io.kotest.engine.extensions.foo.TestEngineConfigFiltersInterceptorInnerTests::class,
).map { ReflectiveSpecRef(it) }.let { TestSuite(it) }

@KotestInternal
private fun runTestsWithFilters(specFilterRegex: String = "", testFilterRegex: String = "") = runBlocking {
   val interceptor = createTestEngineConfigFiltersProcessor(
      specFiltersSetting = specFilterRegex,
      testFiltersSetting = testFilterRegex,
   )

   val config = interceptor.process(baseConfig)
   val engine = TestEngine(config).execute(testSuite)
   engine.errors.shouldBeEmpty()
}

/**
 * Test that the filter expressions in [testFiltersProperty] and [specFiltersProperty]
 * work similarly to how gradle filters in --tests described in
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
@KotestInternal
@Isolate
class TestEngineConfigFiltersInterceptorTests : FunSpec({
   beforeTest {
      mut.withLock {
         executed.clear()
      }
   }

   test("filters a specific class") {
      runTestsWithFilters(specFilterRegex = "*DistantFutureSciFiTests", testFilterRegex = "")
      numberOfTestsRunShouldBe(7)
   }

   test("filters a specific class and test") {
      runTestsWithFilters(specFilterRegex = "*NearFutureSciFiTests", testFilterRegex = "Daedalus")
      numberOfTestsRunShouldBe(1)
   }

   test("filters a test name with spaces") {
      runTestsWithFilters(testFilterRegex = "UNN Arboghast")
      numberOfTestsRunShouldBe(1)
   }

   test("filters all classes in a package") {
      runTestsWithFilters(specFilterRegex = "io.kotest.engine.extensions.bar*")
      numberOfTestsRunShouldBe(2)
   }

   test("filters nested tests in a context") {
      runTestsWithFilters(testFilterRegex = "expanse tests*")
      numberOfTestsRunShouldBe(4)
   }

   test("filters all tests with names that start with") {
      runTestsWithFilters(testFilterRegex = "*UNN*")
      numberOfTestsRunShouldBe(2)
      testsRunShouldBe("UNN Arboghast", "UNN Agatha King")
   }

   test("filters all tests with names that end with") {
      runTestsWithFilters(testFilterRegex = "*BC-304")
      testsRunShouldBe("Daedalus BC-304", "Odyssey BC-304")
   }
})

private class DistantFutureSciFiTests : FunSpec({
   context("trek tests") {
      test("Enterprise NCC-1701") { testAndIncrementCounter() }
      test("Excelsior NCC-2000") { testAndIncrementCounter() }
      test("Defiant NX-74205") { testAndIncrementCounter() }
   }

   context("expanse tests") {
      test("MCRN Donnager") { testAndIncrementCounter() }
      test("Rocinante") { testAndIncrementCounter() }
      test("UNN Arboghast") { testAndIncrementCounter() }
      test("UNN Agatha King") { testAndIncrementCounter() }
   }
})

private class NearFutureSciFiTests : FunSpec({
   test("Daedalus BC-304") { testAndIncrementCounter() }
   test("Odyssey BC-304") { testAndIncrementCounter() }
})

