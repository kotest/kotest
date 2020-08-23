package com.sksamuel.kotest.engine.active

import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener

private var counter = 0

/**
 * A [TestFilter] added via the launcher should filter tests out.
 */
class LauncherTestFilterTest : FunSpec() {
   init {
      test("filter added via launcher should filter test cases") {

         val filter = object : TestFilter {
            override fun filter(description: Description): TestFilterResult {
               return if (description.displayName() == "a") TestFilterResult.Include else TestFilterResult.Exclude
            }
         }

         val listener = object : TestEngineListener {
            override fun testStarted(testCase: TestCase) {
               if (testCase.description.displayName() == "b")
                  error("should not run")
            }
         }

         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(MyTestClass::class)
            .withFilter(filter)
            .launch()
      }

      test("filter with test path added via launcher should filter test cases") {

         val filter = object : TestFilter {
            override fun filter(description: Description): TestFilterResult {
               return if (description.displayName() == "a") TestFilterResult.Include else TestFilterResult.Exclude
            }
         }

         val listener = object : TestEngineListener {
            override fun testStarted(testCase: TestCase) {
               if (testCase.description.displayName() == "b")
                  error("should not run")
            }
         }

         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(MyTestClass::class)
            .withFilter(filter)
            .launch()
      }
   }
}

private class MyTestClass : FunSpec() {
   init {
      test("a") {
         counter++
      }
      test("b") {
         error("ignore")
      }
   }
}
