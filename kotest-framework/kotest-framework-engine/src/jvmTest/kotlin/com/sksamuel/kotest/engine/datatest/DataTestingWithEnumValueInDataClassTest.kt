package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

class DataTestingWithEnumValueInDataClassTest : FunSpec({
   context("Pythag triples tests") {
      forAll(
         PythagTriple(PythagNumber.Three, PythagNumber.Four, PythagNumber.Five),
         PythagTriple(PythagNumber.Four, PythagNumber.Three, PythagNumber.Five),
      ) {}
   }
   context("Data class having enum where enum has non data class value") {
      forAll(
         FooClass(a = Bar.Bar1, b =Bar.Bar2)
      ) {}
   }
}) {
   override fun afterAny(testCase: TestCase, result: TestResult) {
      NamesStore.names.add(testCase.description.displayName())
   }

   override fun afterSpec(spec: Spec) {
      NamesStore.names shouldBe listOf(
         "PythagTriple(a=Three, b=Four, c=Five)",
         "PythagTriple(a=Four, b=Three, c=Five)",
         "Pythag triples tests",
         "FooClass(a=Bar1, b=Bar2)",
         "Data class having enum where enum has non data class value"
      )
   }

   override fun beforeSpec(spec: Spec) {
      NamesStore.names.clear()
   }
}

private object NamesStore {
   val names = mutableListOf<String>()
}

enum class PythagNumber(val num: Int) {
   Three(3), Four(4), Five(5);
}

data class PythagTriple(val a: PythagNumber, val b: PythagNumber, val c: PythagNumber)
data class FooClass(val a: Bar, val b: Bar)
class Baz(val message: String)

enum class Bar(val baz: Baz) {
   Bar1(Baz("Baz1")),
   Bar2(Baz("Baz2"))
}
