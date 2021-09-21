package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.take
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period

class ReflectiveBindTest : StringSpec(
   {

      data class Wobble(val a: String, val b: Boolean, val c: Int, val d: Pair<Double, Float>)

      "nested data classes" {
         data class WobbleWobble(val a: Wobble)

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

      "Arb.reflectiveBind should generate probabilistic edge cases" {
         val arb = Arb.bind<Wobble>()
         val edgeCases = arb
            .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
            .take(5)
            .map { it.value }
            .toList()

         edgeCases shouldContainExactly listOf(
            Wobble(a = "a", b = false, c = 1, d = Double.POSITIVE_INFINITY to -0.0f),
            Wobble(a = "", b = true, c = 1, d = 1.0 to -2.064131E37f),
            Wobble(a = "a", b = true, c = 0, d = Double.POSITIVE_INFINITY to 1.4E-45f),
            Wobble(a = "", b = false, c = Int.MIN_VALUE, d = -Double.MAX_VALUE to 3.4028235E38f),
            Wobble(a = "", b = false, c = -1, d = Double.NEGATIVE_INFINITY to 2.4178792E38f)
         )
      }

   }
)
