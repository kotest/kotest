@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations.statistics

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.permutations.permutations
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.ints

@OptIn(ExperimentalKotest::class)
class CoverageCheckTest : FunSpec() {
   init {

      test("count: passes when the actual count meets the required minimum (default label)") {
         permutations {
            iterations = 100
            coverage {
               count("even", 50)
               count("odd", 50)
            }
            val a by gen { Exhaustive.ints(0..99) }
            check {
               classify(a % 2 == 0, "even", "odd")
            }
         }
      }

      test("count: fails when the actual count is below the required minimum (default label)") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  count("even", 80)
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify(a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage count for [even] under label [statistics] was 80 but actual was 50"
      }

      test("count: passes when the actual count meets the required minimum (custom label)") {
         permutations {
            iterations = 100
            coverage {
               count("parity", "even", 50)
               count("parity", "odd", 50)
            }
            val a by gen { Exhaustive.ints(0..99) }
            check {
               classify("parity", a % 2 == 0, "even", "odd")
            }
         }
      }

      test("count: fails when the actual count is below the required minimum (custom label)") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  count("parity", "even", 75)
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify("parity", a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage count for [even] under label [parity] was 75 but actual was 50"
      }

      test("count: fails when the value was never classified") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  count("missing", 1)
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify(a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage count for [missing] under label [statistics] was 1 but actual was 0"
      }

      test("percentage: passes when the actual percentage meets the required minimum (default label)") {
         permutations {
            iterations = 100
            coverage {
               percentage("even", 50.0)
               percentage("odd", 50.0)
            }
            val a by gen { Exhaustive.ints(0..99) }
            check {
               classify(a % 2 == 0, "even", "odd")
            }
         }
      }

      test("percentage: fails when the actual percentage is below the required minimum (default label)") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  percentage("even", 75.0)
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify(a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage percentage for [even] under label [statistics] was 75.0% but actual was 50.0%"
      }

      test("percentage: passes when the actual percentage meets the required minimum (custom label)") {
         permutations {
            iterations = 100
            coverage {
               percentage("parity", "even", 50.0)
            }
            val a by gen { Exhaustive.ints(0..99) }
            check {
               classify("parity", a % 2 == 0, "even", "odd")
            }
         }
      }

      test("percentage: fails when the actual percentage is below the required minimum (custom label)") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  percentage("parity", "even", 90.0)
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify("parity", a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage percentage for [even] under label [parity] was 90.0% but actual was 50.0%"
      }

      test("no coverage configuration should always pass") {
         permutations {
            iterations = 10
            val a by gen { Exhaustive.ints(0..9) }
            check {
               classify(a % 2 == 0, "even", "odd")
            }
         }
      }

      test("multiple coverage requirements should all be enforced - failing one fails the test") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  count("even", 30)
                  count("odd", 80) // unreachable: only 50 odds in 100 exhaustive ints
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify(a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage count for [odd] under label [statistics] was 80 but actual was 50"
      }

      test("a satisfied count requirement combined with a failing percentage requirement still fails") {
         val message = shouldThrowAny {
            permutations {
               iterations = 100
               coverage {
                  count("even", 30)
                  percentage("even", 75.0)
               }
               val a by gen { Exhaustive.ints(0..99) }
               check {
                  classify(a % 2 == 0, "even", "odd")
               }
            }
         }.message
         message shouldContain "Required coverage percentage for [even] under label [statistics] was 75.0% but actual was 50.0%"
      }

      test("a count of zero is satisfied by an unclassified value") {
         permutations {
            iterations = 10
            coverage {
               count("never-classified", 0)
            }
            val a by gen { Exhaustive.ints(0..9) }
            check {
               classify(a % 2 == 0, "even", "odd")
            }
         }
      }
   }
}
