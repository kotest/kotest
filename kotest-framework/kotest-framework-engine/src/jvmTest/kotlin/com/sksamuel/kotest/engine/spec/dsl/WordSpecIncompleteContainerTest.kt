package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestStatus
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
         desc[DescriptorId("s should")]?.status shouldBe TestStatus.Error
         desc[DescriptorId("s should")]?.error?.message shouldBe "Test 's should' requires at least one nested test"

         desc[DescriptorId("w when")]?.status shouldBe TestStatus.Error
         desc[DescriptorId("w when")]?.error?.message shouldBe "Test 'w when' requires at least one nested test"

         desc[DescriptorId("y should")]?.status shouldBe TestStatus.Error
         desc[DescriptorId("y should")]?.error?.message shouldBe "Test 'y' requires at least one nested test"
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
