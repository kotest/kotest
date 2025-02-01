package io.kotest.permutations.seeds

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.permutations.permutations
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.random
import kotlin.random.Random

@EnabledIf(LinuxCondition::class)
class SeedTest : FunSpec({

   test("seeds should result in consistent randoms") {
      val seed = Random.nextLong()
      Arb.int().single(seed.random()) shouldBe Arb.int().single(seed.random())
      Arb.long().single(seed.random()) shouldBe Arb.long().single(seed.random())
      Arb.string().single(seed.random()) shouldBe Arb.string().single(seed.random())
      Arb.boolean().single(seed.random()) shouldBe Arb.boolean().single(seed.random())
   }

   test("should use random seed by default") {
      var seed1 = 0L
      var seed2 = 0L
      permutations {
         forEach {
            seed1 = rs.seed
         }
      }
      permutations {
         forEach {
            seed2 = rs.seed
         }
      }
      seed1 shouldNotBe seed2
   }

   test("failed test should print seed") {
      shouldThrowAny {
         permutations {
            seed = 12345
            forEach {
               1 shouldBe 0
            }
         }
      }.message shouldContain "Repeat this test by using seed 12345"
   }

   test("errored test should print seed") {
      shouldThrowAny {
         permutations {
            seed = 12345
            forEach {
               error("boom")
            }
         }
      }.message shouldContain "Repeat this test by using seed 12345"
   }
})
