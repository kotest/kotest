package com.sksamuel.kotest.engine.interceptors

import com.sksamuel.kotest.engine.interceptors.filters1.BarTests
import com.sksamuel.kotest.engine.interceptors.filters2.FooTests
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import io.kotest.engine.KotestEngineProperties
import io.kotest.engine.TestEngineLauncher
import io.kotest.extensions.system.withSystemProperties
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

private val executed = mutableListOf<String>()

internal fun TestScope.testAndIncrementCounter() {
   1 shouldBe 1 // fake assertion so tests don't fail from fail on no assertion setting if it's set
   executed.add(this.testCase.name.testName)
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
 * Test that the filter expressions in [KotestEngineProperties.filterTests] and
 * [KotestEngineProperties.filterSpecs] work similarly to how gradle filters in --tests described in
 * https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
 */
@EnabledIf(LinuxCondition::class)
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
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }
      numberOfTestsRunShouldBe(13)
   }

   test("filters a specific class") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "*DistantFutureSciFiTests",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }
      numberOfTestsRunShouldBe(7)
   }

   test("filters a class prefix") {
      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "*FutureSciFiTests",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }
      numberOfTestsRunShouldBe(9)
   }

   test("filters a specific class and test") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "*NearFutureSciFiTests",
            KotestEngineProperties.filterTests to "Daedalus*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }

      numberOfTestsRunShouldBe(1)
   }

   test("filters a test name with spaces") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "trek tests*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }

      numberOfTestsRunShouldBe(3)
   }

   test("filters all classes in a package") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "com.sksamuel.kotest.engine.interceptors.filters1.*",
            KotestEngineProperties.filterTests to ""
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }

      numberOfTestsRunShouldBe(2)
   }

   test("filters nested tests in a context") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "expanse tests*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }

      numberOfTestsRunShouldBe(4)
   }

   test("filter tests using prefix and suffix wildcard") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "*anse tes*"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }

      numberOfTestsRunShouldBe(4)
   }

   test("filter tests with prefix wildcard") {

      withSystemProperties(
         mapOf(
            KotestEngineProperties.filterSpecs to "",
            KotestEngineProperties.filterTests to "*BC-304"
         )
      ) { TestEngineLauncher().withClasses(testSuite).withConfiguration(ProjectConfiguration()).launch() }

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

