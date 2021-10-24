package com.sksamuel.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.config.Configuration
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestContext
import io.kotest.engine.TestEngineLauncher
import io.kotest.extensions.system.withSystemProperties
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

private val executed = mutableListOf<String>()

internal fun TestContext.testAndIncrementCounter() {
   1 shouldBe 1 // fake assertion so tests don't fail from fail on no assertion setting if it's set
   executed.add(this.testCase.name.testName)
}

private fun numberOfTestsRunShouldBe(expected: Int) {
   executed.size shouldBe expected
}

private fun testsRunShouldBe(vararg name: String) {
   executed shouldContainExactly name.toList()
}

private val testSuite = listOf<KClass<out Spec>>(
   DistantFutureSciFiTests::class,
   NearFutureSciFiTests::class,
   io.kotest.engine.extensions.bar.TestEngineConfigFiltersInterceptorInnerTests::class,
   io.kotest.engine.extensions.foo.TestEngineConfigFiltersInterceptorInnerTests::class,
)

/**
 * Test that the filter expressions in [KotestEngineProperties.filterTests] and
 * [KotestEngineProperties.filterSpecs] work similarly to how gradle filters in --tests described in
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
@KotestInternal
@Isolate
class SystemPropertyTestFiltersTests : FunSpec({

   beforeTest {
      executed.clear()
   }

   test("include all classes when filter specs is blank") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }
      numberOfTestsRunShouldBe(13)
   }

   test("filters a specific class") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "*DistantFutureSciFiTests",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }
      numberOfTestsRunShouldBe(7)
   }

   test("filters a class prefix") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "*FutureSciFiTests",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }
      numberOfTestsRunShouldBe(9)
   }

   test("filters a specific class and test") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "*NearFutureSciFiTests",
            KotestEngineProperties.filterTests to "Daedalus*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }

      numberOfTestsRunShouldBe(1)
   }

   test("filters a test name with spaces") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "trek tests*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }

      numberOfTestsRunShouldBe(3)
   }

   test("filters all classes in a package") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "io.kotest.engine.extensions.bar*",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }

      numberOfTestsRunShouldBe(2)
   }

   test("filters nested tests in a context") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "expanse tests*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }

      numberOfTestsRunShouldBe(4)
   }

   test("filter tests using prefix and suffix wildcard") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "*anse tes*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }

      numberOfTestsRunShouldBe(4)
   }

   test("filter tests with prefix wildcard") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "*BC-304"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(Configuration()).launch() }

      numberOfTestsRunShouldBe(2)
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

