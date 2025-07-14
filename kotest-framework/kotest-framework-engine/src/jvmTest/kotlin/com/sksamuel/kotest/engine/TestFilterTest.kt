package com.sksamuel.kotest.engine

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.descriptors.Descriptor
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestFilterTest : FunSpec() {
   init {

      test("a filtered test should be ignored with reason") {

         val filter = object : DescriptorFilter {
            override fun filter(descriptor: Descriptor): DescriptorFilterResult {
               return when (descriptor.id.value) {
                  "foo" -> DescriptorFilterResult.Exclude("get outta here!")
                  else -> DescriptorFilterResult.Include
               }
            }
         }

         val collector = CollectingTestEngineListener()
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(filter)
         }

         TestEngineLauncher(collector)
            .withClasses(SillySpec::class)
            .withProjectConfig(c)
            .launch()

         collector.result("foo") shouldBe TestResult.Ignored("get outta here!")
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
