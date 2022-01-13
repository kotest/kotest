package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.take
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period

class ReflectiveBindTest : StringSpec(
   {

      data class Wobble(val a: String, val b: Boolean, val c: Int, val d: Pair<Double, Float>)
      data class WobbleWobble(val a: Wobble)
      data class BubbleBobble(val a: String?, val b: Boolean?)

      "provided arb for type is used" {
         val wobble = Wobble("test", false, 0, 1.0 to 3.14f)
         val wobbleArb: Arb<Wobble> = arbitrary { wobble }

         val arb = Arb.bind<WobbleWobble>(mapOf(Wobble::class to wobbleArb))

         arb.take(10).forAll {
            it.a shouldBe wobble
         }
      }

      "provided arb is used on all levels" {
         val intArb: Arb<Int> = arbitrary { 1 }

         data class C(val x: Int)
         data class B(val x: Int, val c: C)
         data class A(val x: Int, val b: B)

         val arb = Arb.bind<A>(
            mapOf(
               Int::class to intArb,
               C::class to arbitrary { C(4) }
            )
         )

         arb.take(10).forAll { a ->
            a.x shouldBe 1
            a.b.x shouldBe 1
            a.b.c.x shouldBe 4
         }
      }

      "nested data classes" {
         val arb = Arb.bind<WobbleWobble>()
         arb.take(10).toList().size shouldBe 10
      }

      "collections" {
         data class CollectionsContainer(
            val a: Map<String, Wobble>,
            val b: List<Wobble>,
            val c: Set<Wobble>,
         )

         val arb = Arb.bind<CollectionsContainer>()
         arb.take(10).toList().size shouldBe 10
      }

      "java.time types" {
         data class DateContainer(
            val a: LocalDate,
            val b: LocalDateTime,
            val c: LocalTime,
            val d: Period
         )

         val arb = Arb.bind<DateContainer>()
         arb.take(10).toList().size shouldBe 10
      }

      "Arb.reflectiveBind" {
         val arb = Arb.bind<Wobble>()
         arb.take(10).toList().size shouldBe 10
      }

      "Arb.reflectiveBind should inject nulls when applicable" {
         val bubbles = Arb.bind<BubbleBobble>().take(1000).toList()
         bubbles.map { it.a }.toSet().shouldContain(null)
         bubbles.map { it.b }.toSet().shouldContain(null)
      }

      "Arb.reflectiveBind should generate probabilistic edge cases" {
         val arb = Arb.bind<Wobble>()
         val edgeCases = arb
            .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
            .take(5)
            .map { it.value }
            .toList()

         edgeCases shouldContainExactly listOf(
            Wobble(a = "a", b = false, c = 1, d = Pair(-0.0, Float.POSITIVE_INFINITY)),
            Wobble(a = "", b = true, c = 0, d = Pair(3.5669621934936836E307, -3.4028235E38F)),
            Wobble(a = "a", b = true, c = Int.MIN_VALUE, d = Pair(1.3317496548681731E308, -1.0F)),
            Wobble(a = "", b = false, c = 1, d = Pair(-1.402243144992822E308, 1.0F)),
            Wobble(a = "", b = false, c = 0, d = Pair(Double.POSITIVE_INFINITY, 3.4028235E38F))
         )
      }

   }
)
