package com.sksamuel.kotest.engine.spec.incomplete

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class WordSpecIncompleteContainerTest : FunSpec() {
   init {
      test("word spec should error if not complete") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(IncompleteWordSpec::class)
            .launch()
         val desc = collector.tests.mapKeys { it.key.descriptor.id }
         desc[DescriptorId("s should")]?.isError shouldBe true
         desc[DescriptorId("s should")]?.errorOrNull?.message shouldBe "Test 's should' requires at least one nested test"

         desc[DescriptorId("w when")]?.isError shouldBe true
         desc[DescriptorId("w when")]?.errorOrNull?.message shouldBe "Test 'w when' requires at least one nested test"

         desc[DescriptorId("y should")]?.isError shouldBe true
         desc[DescriptorId("y should")]?.errorOrNull?.message shouldBe "Test 'y should' requires at least one nested test"
      }
   }
}

private class IncompleteWordSpec : WordSpec() {
   init {
      "s" should {
      }
      "w" `when` {

      }
      "x" `when` {
         "y" should {
         }
      }
   }
}
