package com.sksamuel.kotest.engine.datatest

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.names.TestName
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap

@Isolate // sets global values via configuration so must be isolated
class DataTest : FunSpec() {

   init {

      val results = ConcurrentHashMap<Descriptor, TestStatus>()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            results[testCase.descriptor] = result.status
         }
      }

      beforeTest {
         results.clear()
      }

      test("free spec should support data tests") {
         KotestEngineLauncher().withListener(listener).withSpec(FreeSpecDataTest::class).launch()
         assertResults(results)
      }

      test("describe spec should support data tests") {
         KotestEngineLauncher().withListener(listener).withSpec(DescribeSpecDataTest::class).launch()
         assertResults(results)
      }

      test("fun spec should support data tests") {
         KotestEngineLauncher().withListener(listener).withSpec(FunSpecDataTest::class).launch()
         assertResults(results)
      }

      test("expect spec should support data tests") {
         KotestEngineLauncher().withListener(listener).withSpec(ExpectSpecDataTest::class).launch()
         assertResults(results)
      }

//      test("should spec should support data tests") {
//         KotestEngineLauncher().withListener(listener).withSpec(ShouldSpecDataTest::class).launch()
//         assertResults(results)
//      }

      test("feature spec should support data tests") {
         KotestEngineLauncher().withListener(listener).withSpec(FeatureSpecDataTest::class).launch()
         assertResults(results)
      }

//      test("word spec should support data tests") {
//         KotestEngineLauncher().withListener(listener).withSpec(WordSpecDataTest::class).launch()
//         results.entries.toSet() shouldBe setOf(
//            TestName("datatest forAll should") to TestStatus.Success,
//            TestName("PythagTriple(a=3, b=4, c=5)") to TestStatus.Success,
//            TestName("PythagTriple(a=6, b=8, c=10)") to TestStatus.Success,
//            TestName("datatest forAll failure should") to TestStatus.Success,
//            TestName("PythagTriple(a=3, b=2, c=1)") to TestStatus.Failure,
//            TestName("PythagTriple(a=4, b=3, c=2)") to TestStatus.Failure,
//            TestName("datatest forNone should") to TestStatus.Success,
//            TestName("PythagTriple(a=1, b=2, c=3)") to TestStatus.Success,
//            TestName("PythagTriple(a=2, b=3, c=4)") to TestStatus.Success,
//            TestName("datatest forNone failure should") to TestStatus.Success,
//            TestName("PythagTriple(a=13, b=84, c=85)") to TestStatus.Failure,
//            TestName("PythagTriple(a=16, b=63, c=65)") to TestStatus.Failure,
//         )
//      }
 }

   private fun assertResults(results: MutableMap<Descriptor, TestStatus>) {
      results shouldBe mapOf(
         TestName("datatest forAll") to TestStatus.Success,
         TestName("PythagTriple(a=3, b=4, c=5)") to TestStatus.Success,
         TestName("PythagTriple(a=6, b=8, c=10)") to TestStatus.Success,
         TestName("datatest forAll failure") to TestStatus.Success,
         TestName("PythagTriple(a=3, b=2, c=1)") to TestStatus.Failure,
         TestName("PythagTriple(a=4, b=3, c=2)") to TestStatus.Failure,
         TestName("datatest forNone") to TestStatus.Success,
         TestName("PythagTriple(a=1, b=2, c=3)") to TestStatus.Success,
         TestName("PythagTriple(a=2, b=3, c=4)") to TestStatus.Success,
         TestName("datatest forNone failure") to TestStatus.Success,
         TestName("PythagTriple(a=13, b=84, c=85)") to TestStatus.Failure,
         TestName("PythagTriple(a=16, b=63, c=65)") to TestStatus.Failure,
         TestName("1") to TestStatus.Success,
         TestName("2") to TestStatus.Success,
         TestName("3") to TestStatus.Success,
         TestName("4") to TestStatus.Success,
         TestName("5") to TestStatus.Success,
         TestName("6") to TestStatus.Success,
      )
   }
}
