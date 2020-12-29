import io.kotest.core.script.test
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.string
import io.kotest.property.computeDefaultIteration
import io.kotest.property.exhaustive.exhaustive

val exhaustive1 = exhaustive(List(10_000) { it })
val exhaustive2 = exhaustive(List(5_000) { it })

test("computeDefaultIteration should use default if larger than an arbs edge cases") {
   computeDefaultIteration(Arb.string()) shouldBe PropertyTesting.defaultIterationCount
}

test("computeDefaultIteration should use arbs edge cases if larger than default " +
   "iff kotest.proptest.arb.require-at-least-one-sample is disabled") {
   val edgecases = List(234234) { it }
   val arb = arbitrary(edgecases) { 1 }
   computeDefaultIteration(arb) shouldBe 234234
}

test("computeDefaultIteration should use arbs edge cases if larger than default " +
   "iff kotest.proptest.arb.require-at-least-one-sample is enabled") {
   val defaultRequireAtLeastOneSampleForArbs = PropertyTesting.requireAtLeastOneSampleForArbs
   PropertyTesting.requireAtLeastOneSampleForArbs = true
   val edgecases = List(234234) { it }
   val arb = arbitrary(edgecases) { 1 }
   try {
      computeDefaultIteration(arb) shouldBe 234234 + 1
   } finally {
      PropertyTesting.requireAtLeastOneSampleForArbs = defaultRequireAtLeastOneSampleForArbs
   }
}

test("computeDefaultIteration should use exhaustive values") {
   computeDefaultIteration(exhaustive1) shouldBe 10_000
   computeDefaultIteration(exhaustive2) shouldBe 5_000
   computeDefaultIteration(exhaustive1, exhaustive2) shouldBe 50000000
}

