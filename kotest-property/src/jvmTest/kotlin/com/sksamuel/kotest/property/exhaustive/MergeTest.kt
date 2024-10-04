package com.sksamuel.kotest.property.exhaustive

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.merge
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.merge

private sealed class Common {
   internal data class Foo(val value: Int) : Common()
   internal data class Bar(val value: Int) : Common()
}

@EnabledIf(LinuxCondition::class)
class MergeTest : FunSpec({
   test("merge two exhaustive gens where one is a subtype of the other") {
      listOf(1, 2, 3).exhaustive().merge(listOf(4, 5, 6).exhaustive()).values shouldBe listOf(1, 4, 2, 5, 3, 6)
   }

   test("merge two exhaustive gens where neither is a subtype of the other") {
      val firstGen = listOf(Common.Foo(1), Common.Foo(2), Common.Foo(3)).exhaustive()
      val secondGen = listOf(Common.Bar(4), Common.Bar(5), Common.Bar(6)).exhaustive()
      firstGen.merge(secondGen).values shouldBe listOf(
         Common.Foo(1),
         Common.Bar(4),
         Common.Foo(2),
         Common.Bar(5),
         Common.Foo(3),
         Common.Bar(6)
      )
   }

   test("Arb.merge composition should not exhaust call stack") {
      var arb: Arb<Int> = Arb.of(0)
      repeat(100) {
         arb = arb.merge(Arb.of(1))
      }
      val result = shouldNotThrowAny { arb.single(RandomSource.seeded(1234L)) }
      result shouldBe 1
   }
})
