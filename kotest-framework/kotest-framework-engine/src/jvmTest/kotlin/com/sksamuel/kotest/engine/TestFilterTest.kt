package com.sksamuel.kotest.engine

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class TestFilterTest : FunSpec() {
   init {

      test("a filtered test should be ignored with reason") {

         val filter = object : TestFilter {
            override fun filter(descriptor: Descriptor): TestFilterResult {
               return when (descriptor.id.value) {
                  "foo" -> TestFilterResult.Exclude("get outta here!")
                  else -> TestFilterResult.Include
               }
            }
         }

         val collector = CollectingTestEngineListener()
         val c = ProjectConfiguration()
         c.registry.add(filter)

         TestEngineLauncher(collector)
            .withClasses(SillySpec::class)
            .withProjectConfig(c)
            .launch()

         collector.result("foo") shouldBe TestResult.Ignored("foo is excluded by test filter(s): get outta here!")
         collector.result("bar")!!.isSuccess.shouldBeTrue()
      }
   }
}

private class SillySpec : StringSpec() {
   init {
      // this test will be ignored through the TestFilter
      "foo" {
         error("foo")
      }
      "bar" {
      }
   }
}
