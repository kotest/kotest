package com.sksamuel.kotest.engine.active

import io.kotest.core.annotation.Isolate
import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener

private var counter = 0

/**
 * A [DescriptorFilter] added via the launcher should filter tests out.
 */
@Isolate
class LauncherTestFilterTest : FunSpec() {
   init {
      // disabled until filters can be added to one launcher independently
      test("filter added via launcher should filter test cases") {

         val filter = object : DescriptorFilter {
            override fun filter(descriptor: Descriptor): DescriptorFilterResult {
               return if (descriptor.id.value == "a") DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
            }
         }

         val listener = object : AbstractTestEngineListener() {
            override suspend fun testStarted(testCase: TestCase) {
               if (testCase.descriptor.id.value == "b")
                  error("should not run")
            }
         }

         TestEngineLauncher().withListener(listener)
            .withClasses(MyTestClass::class)
            .addExtensions(filter)
            .launch()
      }

      test("filter with test path added via launcher should filter test cases") {

         val filter = object : DescriptorFilter {
            override fun filter(descriptor: Descriptor): DescriptorFilterResult {
               return if (descriptor.id.value == "a") DescriptorFilterResult.Include else DescriptorFilterResult.Exclude(null)
            }
         }

         val listener = object : AbstractTestEngineListener() {
            override suspend fun testStarted(testCase: TestCase) {
               if (testCase.descriptor.id.value == "b")
                  error("should not run")
            }
         }

         TestEngineLauncher().withListener(listener)
            .withClasses(MyTestClass::class)
            .addExtensions(filter)
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
