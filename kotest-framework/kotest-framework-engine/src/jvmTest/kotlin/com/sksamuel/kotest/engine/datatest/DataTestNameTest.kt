package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.IsStableType
import io.kotest.core.datatest.WithDataTestName
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
   context("Data test with triple") {
      forAll(
         Triple(1,2,3),
         Triple(3,2,1),
      ) {}
   }
   context("Data test for type extending WithDataTestName") {
      forAll(
         ADummyClass("a1", "b1"),
         ADummyClass("a2", "b2")
      ) {}
   }
   context("Data test for type annotated with IsStableType") {
      forAll(
         AnotherDummyClass("a1", "b1"),
         AnotherDummyClass("a2", "b2")
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
         "Data test with pair",
         "(1, 2, 3)",
         "(3, 2, 1)",
         "Data test with triple",
         "ADummyClass(a : a1, b : b1)",
         "ADummyClass(a : a2, b : b2)",
         "Data test for type extending WithDataTestName",
         "AnotherDummyClass(a : a1, b : b1)",
         "AnotherDummyClass(a : a2, b : b2)",
         "Data test for type annotated with IsStableType",
      )
   }

   override fun beforeSpec(spec: Spec) {
      DataTestNamesStore.names.clear()
   }
}

private object DataTestNamesStore {
   val names = mutableListOf<String>()
}

private class ADummyClass(private val a: String, private val b: String) : WithDataTestName {
   override fun dataTestName() = "ADummyClass(a : $a, b : $b)"
}

@IsStableType
private data class AnotherDummyClass(private val a: String, private val b: String){
   override fun toString() = "AnotherDummyClass(a : $a, b : $b)"
}
