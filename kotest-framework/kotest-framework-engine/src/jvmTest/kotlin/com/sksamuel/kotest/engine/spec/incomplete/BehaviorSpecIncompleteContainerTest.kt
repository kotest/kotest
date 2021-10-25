package com.sksamuel.kotest.engine.spec.incomplete

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class BehaviorSpecIncompleteContainerTest : FunSpec() {
   init {
      test("behavior spec should error if not complete") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(IncompleteBehaviorSpec::class)
            .launch()
         val desc = collector.tests.mapKeys { it.key.descriptor.id }
         desc[DescriptorId("g")]?.isError shouldBe true
         desc[DescriptorId("g")]?.errorOrNull?.message shouldBe "Test 'g' requires at least one nested test"
         desc[DescriptorId("w")]?.isError shouldBe true
         desc[DescriptorId("w")]?.errorOrNull?.message shouldBe "Test 'w' requires at least one nested test"
      }
   }
}

private class IncompleteBehaviorSpec : BehaviorSpec() {
   init {
      given("g") {}
      given("h") {
         When("w") {}
      }
   }
}
