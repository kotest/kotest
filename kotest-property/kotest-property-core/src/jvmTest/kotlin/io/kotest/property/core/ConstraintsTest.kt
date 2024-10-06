package io.kotest.property.core

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class ConstraintsTest : FunSpec() {
   init {
      test("iterations should be used by default") {
         var counter = 0
         permutations {
            iterations = 3
            forEach {
               counter++
            }
         }
         counter shouldBe 3
      }

      test("duration should override iterations") {
         var counter = 0
         permutations {
            iterations = 3
            duration = 100.milliseconds
            forEach {
               counter++
            }
         }
         counter shouldBeGreaterThan 3
      }

      test("custom contraints should override durations and iterations") {
         var counter = 0
         permutations {
            iterations = 3
            duration = 100.milliseconds
            constraints = Constraints.iterations(5)
            forEach {
               counter++
            }
         }
         counter shouldBe 5
      }
   }
}
