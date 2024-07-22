package io.kotest.datatest

import io.kotest.engine.datatest.IsStableType
import io.kotest.engine.datatest.WithDataTestName
import io.kotest.engine.datatest.withData
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe

class ContainerDataTestNameFunctionTest : FunSpec({

   context("Data test with pair") {
      withData(
         Pair(2, 1),
         Pair(1, 2),
      ) {}
   }

   context("Data test with triple") {
      withData(
         Triple(1, 2, 3),
         Triple(3, 2, 1),
      ) {}
   }

   context("data test for type extending WithDataTestName") {
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

   context("data test with name function and varargs") {
      withData<SimpleClass>(
         { "simple${it.a}${it.b}" },
         SimpleClass("a1", "b1"),
         SimpleClass("a2", "b2"),
      ) {}
   }

   context("data test with name function and sequence") {
      withData<SimpleClass>(
         { "simple${it.a}${it.b}" },
         sequenceOf(
            SimpleClass("a1", "b1"),
            SimpleClass("a2", "b2"),
         )
      ) {}
   }

   context("data test with name function and collection") {
      withData<SimpleClass>(
         { "simple${it.a}${it.b}" },
         listOf(
            SimpleClass("a1", "b1"),
            SimpleClass("a2", "b2"),
         )
      ) {}
   }

   context("data test with name function and range") {
      withData<Int>(
         { i: Int -> "Test $i" },
         1..3
      ) {}
   }

   context("data test with progression") {
      withData(4 downTo 0 step 2) {}
   }

}) {
   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      DataTestNamesStore.names.add(testCase.descriptor.id.value)
   }

   override suspend fun afterSpec(spec: Spec) {
      DataTestNamesStore.names shouldBe listOf(
         "(2, 1)",
         "(1, 2)",
         "Data test with pair",
         "(1, 2, 3)",
         "(3, 2, 1)",
         "Data test with triple",
         "ADummyClass(a : a1, b : b1)",
         "ADummyClass(a : a2, b : b2)",
         "data test for type extending WithDataTestName",
         "AnotherDummyClass(a : a1, b : b1)",
         "AnotherDummyClass(a : a2, b : b2)",
         "Data test for type annotated with IsStableType",
         "simplea1b1",
         "simplea2b2",
         "data test with name function and varargs",
         "simplea1b1",
         "simplea2b2",
         "data test with name function and sequence",
         "simplea1b1",
         "simplea2b2",
         "data test with name function and collection",
         "Test 1",
         "Test 2",
         "Test 3",
         "data test with name function and range",
         "4",
         "2",
         "0",
         "data test with progression",
      )
   }

   override suspend fun beforeSpec(spec: Spec) {
      DataTestNamesStore.names.clear()
   }
}

object DataTestNamesStore {
   val names = mutableListOf<String>()
}

internal class ADummyClass(private val a: String, private val b: String) : WithDataTestName {
   override fun dataTestName() = "ADummyClass(a : $a, b : $b)"
}

internal class SimpleClass(val a: String, val b: String)

@IsStableType
internal data class AnotherDummyClass(private val a: String, private val b: String) {
   override fun toString() = "AnotherDummyClass(a : $a, b : $b)"
}
