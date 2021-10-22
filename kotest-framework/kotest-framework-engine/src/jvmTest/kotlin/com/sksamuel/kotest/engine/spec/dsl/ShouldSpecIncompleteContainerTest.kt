package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class ShouldSpecIncompleteContainerTest : FunSpec() {
   init {
      test("should spec should error if not complete") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(IncompleteShouldSpec::class)
            .launch()
         val desc = collector.tests.mapKeys { it.key.descriptor.id }
         desc[DescriptorId("c")]?.isError shouldBe true
         desc[DescriptorId("c")]?.errorOrNull?.message shouldBe "Test 'c' requires at least one nested test"
      }
   }
}

private class IncompleteShouldSpec : ShouldSpec() {
   init {
      context("c") {}
   }
}
