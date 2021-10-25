package com.sksamuel.kotest.engine.spec.incomplete

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class FeatureSpecIncompleteContainerTest : FunSpec() {
   init {
      test("feature spec should error if not complete") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(IncompleteFeatureSpec::class)
            .launch()
         val desc = collector.tests.mapKeys { it.key.descriptor.id }
         desc[DescriptorId("a")]?.isError shouldBe true
         desc[DescriptorId("a")]?.errorOrNull?.message shouldBe "Test 'a' requires at least one nested test"
         desc[DescriptorId("b")]?.isSuccess shouldBe true
         desc[DescriptorId("c")]?.isError shouldBe true
         desc[DescriptorId("c")]?.errorOrNull?.message shouldBe "Test 'c' requires at least one nested test"
      }
   }
}

private class IncompleteFeatureSpec : FeatureSpec() {
   init {
      feature("a") { }
      feature("b") {
         feature("c") { }
      }
   }
}
