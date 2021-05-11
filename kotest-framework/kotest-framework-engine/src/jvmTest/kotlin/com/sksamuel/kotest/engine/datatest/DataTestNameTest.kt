package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

class DataTestNameTest: FunSpec({
   context("Data test with pair") {
      forAll(
         Pair(2,1),
         Pair(1,2),
      ) {}
   }
}) {
   override fun afterAny(testCase: TestCase, result: TestResult) {
      DataTestNamesStore.names.add(testCase.description.displayName())
   }

   override fun afterSpec(spec: Spec) {
      DataTestNamesStore.names shouldBe listOf(
         "(2, 1)",
         "(1, 2)",
         "Data test with pair"
      )
   }

   override fun beforeSpec(spec: Spec) {
      DataTestNamesStore.names.clear()
   }
}

private object DataTestNamesStore {
   val names = mutableListOf<String>()
}
