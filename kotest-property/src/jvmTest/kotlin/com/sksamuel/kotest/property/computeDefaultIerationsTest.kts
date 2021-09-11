import io.kotest.core.script.test
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.exhaustive

val exhaustive1 = exhaustive(List(10_000) { it })
val exhaustive2 = exhaustive(List(5_000) { it })

test("computeDefaultIteration should use default if larger than an arbs edge cases") {
   run {
      arrayOf<Gen<*>>(Arb.string())
      PropertyTesting.defaultIterationCount
   } shouldBe PropertyTesting.defaultIterationCount
}

test("computeDefaultIteration should yield default iteration for arbitraries") {
   val edgeCases = List(234234) { it }
   val arb = arbitrary(edgeCases) { 1 }
   run {
      arrayOf<Gen<*>>(arb)
      PropertyTesting.defaultIterationCount
   } shouldBe PropertyTesting.defaultIterationCount
}

test("computeDefaultIteration should use exhaustive values") {
   run {
      arrayOf<Gen<*>>(exhaustive1)
      PropertyTesting.defaultIterationCount
   } shouldBe 10_000
   run {
      arrayOf<Gen<*>>(exhaustive2)
      PropertyTesting.defaultIterationCount
   } shouldBe 5_000
   run {
      arrayOf<Gen<*>>(exhaustive1, exhaustive2)
      PropertyTesting.defaultIterationCount
   } shouldBe 50000000
}

