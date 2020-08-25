package io.kotest.engine.multiconfig

import io.kotest.core.config.configuration
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.shouldBe

class Test : WordSpec() {
   init {
      "detecting two configs" should {
         "merge listeners" {
            listeners shouldBe 2
         }
         "merge beforeAll" {
            beforeAll shouldBe 2
         }
         "merge separate settings" {
            configuration.testCaseOrder shouldBe TestCaseOrder.Random
            configuration.specExecutionOrder shouldBe SpecExecutionOrder.Random
         }
      }
   }
}
