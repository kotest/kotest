package com.sksamuel.kotest.engine.active

import io.kotest.core.annotation.Isolate
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.Node

private var counter = 0

/**
 * A [TestFilter] added via the launcher should filter tests out.
 */
@Isolate
class LauncherTestFilterTest : FunSpec() {
   init {
      // disabled until filters can be added to one launcher independently
      test("filter added via launcher should filter test cases") {

         val filter = object : TestFilter {
            override fun filter(descriptor: Descriptor): TestFilterResult {
               return if (descriptor.id.value == "a") TestFilterResult.Include else TestFilterResult.Exclude(null)
            }
         }

         val listener = object : AbstractTestEngineListener() {
            override suspend fun executionStarted(node: Node) {
               if (node is Node.Test && node.testCase.descriptor.id.value == "b")
                  error("should not run")
            }
         }

         TestEngineLauncher(listener)
            .withClasses(MyTestClass::class)
            .withExtensions(filter)
            .launch()
      }

      test("filter with test path added via launcher should filter test cases") {

         val filter = object : TestFilter {
            override fun filter(descriptor: Descriptor): TestFilterResult {
               return if (descriptor.id.value == "a") TestFilterResult.Include else TestFilterResult.Exclude(null)
            }
         }

         val listener = object : AbstractTestEngineListener() {
            override suspend fun executionStarted(node: Node) {
               if (node is Node.Test && node.testCase.descriptor.id.value == "b")
                  error("should not run")
            }
         }

         TestEngineLauncher(listener)
            .withClasses(MyTestClass::class)
            .withExtensions(filter)
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
