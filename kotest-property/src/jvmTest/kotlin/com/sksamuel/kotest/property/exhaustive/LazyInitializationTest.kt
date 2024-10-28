package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.lazy

@EnabledIf(LinuxCondition::class)
class LazyInitializationTest : FunSpec({

   test("Exhaustive.lzy should not evaluate given exhaustive provider when return arb is not used") {
      var callCount = 0

      Exhaustive.lazy {
         callCount++
         MyDummyExhaustive(2)
      }
      callCount shouldBe 0
   }

   test("Exhaustive.lzy should evaluate given exhaustive provider only once when return arb is used") {
      var callCount = 0

      val lazyExhaustive = Exhaustive.lazy {
         callCount++
         MyDummyExhaustive(2)
      }

      lazyExhaustive.values shouldBe listOf(2)
      lazyExhaustive.values shouldBe listOf(2)

      callCount shouldBe 1
   }
})

private class MyDummyExhaustive(seed: Int) : Exhaustive<Int>() {
   override val values: List<Int> = listOf(seed)
}
