package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class FreeSpecTest : FreeSpec() {

   private var count = 0

   override fun afterSpec(spec: Spec) {
      count shouldBe 3
   }

   init {

      "context a" - {
         "b1" - {
            "c" {
               count += 1
            }
         }
         "b2" - {
            "d" {
               count += 2
            }
         }
      }

      "context with coroutine in free scope" - {
         launch { delay(1) }
         "another context with coroutine in free scope" - {
            launch { delay(1) }
            "a dummy test" {

            }
         }
      }


      "params" - {
         "support config".config(enabled = true) {
         }
      }

      "for all" - {
         data class TestCase(val string: String, val length: Int)
         forAll(
            mapOf(
               "zero" to TestCase(string = "", length = 0),
               "one" to TestCase(string = "a", length = 1)
            )
         ) {
            it.string.length shouldBe it.length

            "sub" - {
               it.string.length shouldBe it.length
            }
         }
      }
   }
}
