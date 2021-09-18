package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class DataTestNameTest: FunSpec({

   context("Data test with pair") {
      withData(
         Pair(2,1),
         Pair(1,2),
      ) {}
   }

   context("Data test with triple") {
      withData(
         Triple(1,2,3),
         Triple(3,2,1),
      ) {}
   }

   context("Data test for type extending WithDataTestName") {
      withData(
         ADummyClass("a1", "b1"),
         ADummyClass("a2", "b2")
      ) {}
   }

   context("Data test for type annotated with IsStableType") {
      withData(
         AnotherDummyClass("a1", "b1"),
         AnotherDummyClass("a2", "b2")
      ) {}
   }

   context("data test names with function") {
      withData<SimpleClass>(
         { "simple${it.a}${it.b}" },
         SimpleClass("a1", "b1"),
         SimpleClass("a2", "b2"),
      ) {}
   }

   withData<SimpleClass>(
      { "simple${it.a}${it.b}" },
      SimpleClass("a1", "b1"),
      SimpleClass("a2", "b2"),
   ) {}

}) {
   override fun afterAny(testCase: TestCase, result: TestResult) {
      DataTestNamesStore.names.add(testCase.descriptor.id.value)
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
         "simplea1b1",
         "simplea2b2",
         "data test names with function",
         "simplea1b1",
         "simplea2b2",
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


private class SimpleClass(val a: String, val b: String)

@IsStableType
private data class AnotherDummyClass(private val a: String, private val b: String) {
   override fun toString() = "AnotherDummyClass(a : $a, b : $b)"
}
