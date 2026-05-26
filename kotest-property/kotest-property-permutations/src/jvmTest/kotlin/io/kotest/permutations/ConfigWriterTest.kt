@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.of

@OptIn(ExperimentalKotest::class)
class ConfigWriterTest : FunSpec() {
   init {

      test("ConfigWriter should print the header when shouldPrintConfig is true") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldContain "Permutation test config:"
      }

      test("ConfigWriter should not print anything when shouldPrintConfig is false") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = false
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldNotContain "Permutation test config"
      }

      test("ConfigWriter should always print the unconditional fields") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldContain "Output statistics:"
         stdout shouldContain "Print generated values:"
         stdout shouldContain "Print shrink steps:"
         stdout shouldContain "Fail on seed:"
         stdout shouldContain "Write failed seed:"
      }

      test("ConfigWriter should print Max failures when maxFailures is greater than zero") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               maxFailures = 7
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldContain "Max failures: 7"
      }

      test("ConfigWriter should omit Max failures when maxFailures is zero") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               maxFailures = 0
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldNotContain "Max failures"
      }

      test("ConfigWriter should print Max discard percentage when maxDiscardPercentage is greater than zero") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               maxDiscardPercentage = 42
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldContain "Max discard percentage: 42"
      }

      test("ConfigWriter should omit Max discard percentage when maxDiscardPercentage is zero") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               maxDiscardPercentage = 0
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldNotContain "Max discard percentage"
      }

      test("ConfigWriter should print Min successes when minSuccess is greater than zero") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               minSuccess = 3
               iterations = 5
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldContain "Min successes: 3"
      }

      test("ConfigWriter should omit Min successes when minSuccess is zero") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               minSuccess = 0
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldNotContain "Min successes"
      }

      test("ConfigWriter should print Custom seed when a seed has been explicitly set") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               seed = 12345L
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldContain "Custom seed: true"
      }

      test("ConfigWriter should omit Custom seed when no seed was explicitly set") {
         val stdout = captureStandardOut {
            permutations {
               shouldPrintConfig = true
               iterations = 1
               val x by gen { Exhaustive.of(1) }
               check { x shouldNotBe null }
            }
         }
         stdout shouldNotContain "Custom seed"
      }
   }
}
