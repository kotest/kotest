package com.sksamuel.kotest.engine.interceptors

import com.sksamuel.kotest.engine.interceptors.filters1.BarTests
import com.sksamuel.kotest.engine.interceptors.filters2.FooTests
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.TestEngineLauncher
import io.kotest.extensions.system.withSystemProperties
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

private val executed = mutableListOf<String>()

internal fun TestScope.testAndIncrementCounter() {
   1 shouldBe 1 // fake assertion so tests don't fail from fail on no assertion setting if it's set
   executed.add(this.testCase.name.name)
}

private fun numberOfTestsRunShouldBe(expected: Int) {
   executed.size shouldBe expected
}

private val testSuite = listOf<KClass<out Spec>>(
   DistantFutureSciFiTests::class,
   NearFutureSciFiTests::class,
   BarTests::class,
   FooTests::class,
)

/**
 * Test that the filter expressions in [KotestEngineProperties.FILTER_TESTS] and
 * [KotestEngineProperties.FILTER_SPECS] work similarly to how gradle filters in --tests described in
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class SystemPropertyTestFiltersTests : FunSpec({

   beforeTest {
      executed.clear()
   }

   xtest("include all classes when filter specs is blank") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "",
            KotestEngineProperties.FILTER_TESTS to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }
      numberOfTestsRunShouldBe(13)
   }

   xtest("filters a specific class") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "*DistantFutureSciFiTests",
            KotestEngineProperties.FILTER_TESTS to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }
      numberOfTestsRunShouldBe(7)
   }

   xtest("filters a class prefix") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "*FutureSciFiTests",
            KotestEngineProperties.FILTER_TESTS to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }
      numberOfTestsRunShouldBe(9)
   }

   xtest("filters a specific class and test") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "*NearFutureSciFiTests",
            KotestEngineProperties.FILTER_TESTS to "Daedalus*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }

      numberOfTestsRunShouldBe(1)
   }

   xtest("filters a test name with spaces") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "",
            KotestEngineProperties.FILTER_TESTS to "trek tests*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }

      numberOfTestsRunShouldBe(3)
   }

   xtest("filters all classes in a package") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "com.sksamuel.kotest.engine.interceptors.filters1.*",
            KotestEngineProperties.FILTER_TESTS to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }

      numberOfTestsRunShouldBe(2)
   }

   xtest("filters nested tests in a context") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "",
            KotestEngineProperties.FILTER_TESTS to "expanse tests*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }

      numberOfTestsRunShouldBe(4)
   }

   xtest("filter tests using prefix and suffix wildcard") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "",
            KotestEngineProperties.FILTER_TESTS to "*anse tes*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }

      numberOfTestsRunShouldBe(4)
   }

   xtest("filter tests with prefix wildcard") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.FILTER_SPECS to "",
            KotestEngineProperties.FILTER_TESTS to "*BC-304"
         )
      ) { TestEngineLauncher().withClasses(testSuite).launch() }

      numberOfTestsRunShouldBe(2)
      executed.shouldContainExactly("Daedalus BC-304", "Odyssey BC-304")
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

