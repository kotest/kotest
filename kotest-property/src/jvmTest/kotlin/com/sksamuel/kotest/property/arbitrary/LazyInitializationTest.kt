package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.int

import io.kotest.property.arbitrary.lazy
import io.kotest.property.arbitrary.take
import io.kotest.property.asSample

@EnabledIf(LinuxOnlyGithubCondition::class)
class LazyInitializationTest : FunSpec({
   test("Arb.lzy should not evaluate given arb provider when return arb is not used") {
      var callCount = 0

      Arb.lazy {
         callCount++
         Arb.int(0, 10)
      }

      callCount shouldBe 0
   }

   test("Arb.lzy should evaluate given arb provider when return arb is used") {
      var callCount = 0

      val lazyArb = Arb.lazy {
         callCount++
         MyDummyArb(2)
      }
      listOf(2, 2, 2, 2) shouldBe lazyArb.take(4).toList()
      listOf(2, 2) shouldBe lazyArb.take(2).toList()
      callCount shouldBe 1
   }
})

private class MyDummyArb(private val seed: Int) : Arb<Int>() {
   override fun edgecase(rs: RandomSource): Sample<Int> = listOf(1, 2, 3).random().asSample()
   override fun sample(rs: RandomSource): Sample<Int> = Sample(seed)
}

