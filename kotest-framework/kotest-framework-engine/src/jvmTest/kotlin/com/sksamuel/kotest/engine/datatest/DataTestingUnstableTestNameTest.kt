package com.sksamuel.kotest.engine.datatest

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DataTestingUnstableTestNameTest : FunSpec() {
   init {

      val results = mutableListOf<TestStatus>()

      val listener = object : TestEngineListener {
         override fun testFinished(testCase: TestCase, result: TestResult) {
            results.add(result.status)
         }
      }

      beforeTest {
         results.clear()
      }

      test("isolation mode leaf + regular classes should show all tests in data testing") {

         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(RegularClassAndLeafIsolation::class)
            .launch()

         results shouldBe listOf(
            TestStatus.Success,
            TestStatus.Failure,
            TestStatus.Success,
            TestStatus.Success,
         )
      }

      test("isolation mode leaf + data classes with regular class param should show all tests in data testing") {

         KotestEngineLauncher()
            .withListener(listener)
            .withSpec(DataClassWithNonDataParameterAndLeafIsolation::class)
            .launch()

         results shouldBe listOf(
            TestStatus.Success,
            TestStatus.Failure,
            TestStatus.Success,
            TestStatus.Success,
         )
      }
   }
}

private class RegularClassAndLeafIsolation : DescribeSpec() {
   init {
      isolationMode = IsolationMode.InstancePerLeaf

      describe("foo") {
         forAll(
            NotADataClass(1),
            NotADataClass(2),
            NotADataClass(3),
         ) { d ->
            d.a shouldNotBe 2
         }
      }
   }
}

private class DataClassWithNonDataParameterAndLeafIsolation : DescribeSpec() {
   init {
      isolationMode = IsolationMode.InstancePerLeaf

      describe("foo") {
         forAll(
            DataClassWithNonDataParameter(1, NotADataClass(1)),
            DataClassWithNonDataParameter(1, NotADataClass(2)),
            DataClassWithNonDataParameter(1, NotADataClass(3)),
         ) { d ->
            d.b.a shouldNotBe 2
         }
      }
   }
}

data class DataClassWithNonDataParameter(val a: Int, val b: NotADataClass)

class NotADataClass(val a: Int)
