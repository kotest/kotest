package com.sksamuel.kotest.engine.active

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener

private var counter = 0

/**
 * A [TestFilter] added via the launcher should filter tests out.
 */
@Isolate
class LauncherTestFilterTest : FunSpec() {
   init {
      test("filter added via launcher should filter test cases") {

         val filter = object : TestFilter {
            override fun filter(descriptor: Descriptor): TestFilterResult {
               return if (descriptor.id.value == "a") TestFilterResult.Include else TestFilterResult.Exclude
            }
         }

         val listener = object : TestEngineListener {
            override suspend fun testStarted(testCase: TestCase) {
               if (testCase.descriptor.id.value == "b")
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
            override fun filter(descriptor: Descriptor): TestFilterResult {
               return if (descriptor.id.value == "a") TestFilterResult.Include else TestFilterResult.Exclude
            }
         }

         val listener = object : TestEngineListener {
            override suspend fun testStarted(testCase: TestCase) {
               if (testCase.descriptor.id.value == "b")
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
