import io.kotest.core.script.test
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.string
import io.kotest.property.computeDefaultConstraints
import io.kotest.property.exhaustive.exhaustive

val exhaustive1 = exhaustive(List(10_000) { it })
val exhaustive2 = exhaustive(List(5_000) { it })

test("computeDefaultIteration should use default if larger than an arbs edge cases") {
   computeDefaultConstraints(Arb.string()) shouldBe PropertyTesting.defaultIterationCount
}

test("computeDefaultIteration should yield default iteration for arbitraries") {
   val edgeCases = List(234234) { it }
   val arb = arbitrary(edgeCases) { 1 }
   computeDefaultConstraints(arb) shouldBe PropertyTesting.defaultIterationCount
}

test("computeDefaultIteration should use exhaustive values") {
   computeDefaultConstraints(exhaustive1) shouldBe 10_000
   computeDefaultConstraints(exhaustive2) shouldBe 5_000
   computeDefaultConstraints(exhaustive1, exhaustive2) shouldBe 50000000
}

