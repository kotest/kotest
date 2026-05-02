package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.multiple

@EnabledIf(LinuxOnlyGithubCondition::class)
class MultipleArbitraryTest : FunSpec() {
   init {
      test("multiple generation") {
         Arb.multiple(3, 100)
            .generate(RandomSource.default())
            .take(100)
            .forAll { it.value % 3 shouldBe 0 }
      }

      // regression: nextInt(0, max / k) was half-open, so the largest multiple of k that is <= max
      // was unreachable. For Arb.multiple(5, 100), the value 100 was never produced even though
      // 100 is a valid multiple of 5 within the range.
      test("multiple should be able to produce the largest multiple of k <= max") {
         val seen = Arb.multiple(5, 100)
            .generate(RandomSource.seeded(1234L))
            .take(2000)
            .map { it.value }
            .toSet()
         seen.shouldContain(100)
      }

      test("multiple(k, k) should be able to produce k") {
         val seen = Arb.multiple(7, 7)
            .generate(RandomSource.seeded(1234L))
            .take(200)
            .map { it.value }
            .toSet()
         seen.shouldContain(7)
      }
   }
}
