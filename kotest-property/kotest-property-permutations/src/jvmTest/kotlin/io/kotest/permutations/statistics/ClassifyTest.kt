@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations.statistics

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.permutations.Classifications
import io.kotest.permutations.Label
import io.kotest.permutations.Permutation
import io.kotest.property.RandomSource

@OptIn(ExperimentalKotest::class)
class ClassifyTest : FunSpec() {
   init {

      test("classify(value) should label the output under the default 'statistics' label") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify("yes")

         classifications.counts.keys shouldBe setOf(Label.Default)
         classifications.counts[Label.Default]!!.shouldContainExactly(mapOf("yes" to 1))
      }

      test("classify(label, value) should label the output under the given label") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify("parity", "even")

         classifications.counts.keys shouldBe setOf(Label("parity"))
         classifications.counts[Label("parity")]!!.shouldContainExactly(mapOf("even" to 1))
      }

      test("classify should increment the count for repeat values under the same label") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify("parity", "even")
         permutation.classify("parity", "even")
         permutation.classify("parity", "odd")

         classifications.counts[Label("parity")]!!.shouldContainExactly(
            mapOf("even" to 2, "odd" to 1)
         )
      }

      test("classify should keep counts separate across labels") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify("parity", "even")
         permutation.classify("sign", "positive")
         permutation.classify("parity", "odd")
         permutation.classify("sign", "positive")

         classifications.counts[Label("parity")]!!.shouldContainExactly(
            mapOf("even" to 1, "odd" to 1)
         )
         classifications.counts[Label("sign")]!!.shouldContainExactly(
            mapOf("positive" to 2)
         )
      }

      test("classify(predicate, ifTrue, ifFalse) should label the output by branch") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify(true, "yes", "no")
         permutation.classify(false, "yes", "no")
         permutation.classify(true, "yes", "no")

         classifications.counts[Label.Default]!!.shouldContainExactly(
            mapOf("yes" to 2, "no" to 1)
         )
      }

      test("classify(label, predicate, ifTrue, ifFalse) should label the output by branch under the given label") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify("parity", true, "even", "odd")
         permutation.classify("parity", false, "even", "odd")

         classifications.counts[Label("parity")]!!.shouldContainExactly(
            mapOf("even" to 1, "odd" to 1)
         )
      }

      test("classify should ignore null values") {
         val classifications = Classifications()
         val permutation = Permutation(0, RandomSource.seeded(1L), classifications)

         permutation.classify(null)
         permutation.classify("parity", null)

         classifications.counts.shouldContainExactly(emptyMap())
      }
   }
}
