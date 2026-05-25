@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

@Suppress("OPT_IN_USAGE")
class FullExampleTest : FunSpec() {
   init {
      test("addition is commutative") {
         permutations {

            val a by gen { Arb.int() }
            val b by gen { Arb.int() }

            iterations = 1000

            check {

               classify("sign", a > 0, "positive", "negative")
               classify("sign", b > 0, "positive", "negative")
               classify("parity", a % 2 == 0, "even", "odd")
               classify("parity", b % 2 == 0, "even", "odd")

               (a + b) shouldBe (b + a)
            }

            coverage {
               // confirms that at least 200 of the parity classifications were classified as 'even'
               count("parity", "even", 200)
               count("parity", "even", 200)

               // confirms that at least 25% of the sign classifications were classified as 'positive'
               percentage("sign", "positive", 25.0)
               percentage("sign", "negative", 25.0)
            }
         }
      }
   }
}
