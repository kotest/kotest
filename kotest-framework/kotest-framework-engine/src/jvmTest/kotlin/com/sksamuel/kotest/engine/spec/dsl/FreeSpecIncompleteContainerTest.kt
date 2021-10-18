package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class FreeSpecIncompleteContainerTest : FunSpec() {
   init {
      test("free spec should error if not complete") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(IncompleteFreeSpec::class)
            .launch()
         val desc = collector.tests.mapKeys { it.key.descriptor.id }
         desc[DescriptorId("a")]?.status shouldBe TestStatus.Error
         desc[DescriptorId("a")]?.errorOrNull?.message shouldBe "Test 'a' requires at least one nested test"
         desc[DescriptorId("b")]?.status shouldBe TestStatus.Success
         desc[DescriptorId("c")]?.status shouldBe TestStatus.Error
         desc[DescriptorId("c")]?.errorOrNull?.message shouldBe "Test 'c' requires at least one nested test"
      }
   }
}

private class IncompleteFreeSpec : FreeSpec() {
   init {
      "a" - {

      }
      "b" - {
         "c" - {

         }
      }
   }
}
