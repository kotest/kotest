//package io.kotest.datatest
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.annotation.Isolate
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.DescribeSpec
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.core.test.TestStatus
//import io.kotest.engine.KotestEngineLauncher
//import io.kotest.engine.listener.AbstractTestEngineListener
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.shouldNotBe
//
//@ExperimentalKotest
//@Isolate // sets global values via configuration so must be isolated
//class UnstableTestNameWithLeafIsolationTest : FunSpec() {
//   init {
//
//      val results = mutableListOf<TestStatus>()
//
//      val listener = object : AbstractTestEngineListener() {
//         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
//            results.add(result.status)
//         }
//      }
//
//      beforeTest {
//         results.clear()
//      }
//
//      test("isolation mode leaf + regular classes should show all tests in data testing") {
//
//         KotestEngineLauncher()
//            .withListener(listener)
//            .withSpec(RegularClassAndLeafIsolation::class)
//            .launch()
//
//         results shouldBe listOf(
//            TestStatus.Success,
//            TestStatus.Failure,
//            TestStatus.Success,
//            TestStatus.Success,
//            TestStatus.Failure,
//            TestStatus.Success,
//            TestStatus.Success, // final success is the foo context
//         )
//      }
//
//      test("isolation mode leaf + data classes with regular class param should show all tests in data testing") {
//
//         KotestEngineLauncher()
//            .withListener(listener)
//            .withSpec(DataClassWithNonDataParameterAndLeafIsolation::class)
//            .launch()
//
//         results shouldBe listOf(
//            TestStatus.Success,
//            TestStatus.Failure,
//            TestStatus.Success,
//            TestStatus.Success,
//            TestStatus.Failure,
//            TestStatus.Success,
//            TestStatus.Success,  // final success is the foo context
//         )
//      }
//   }
//}
//
//@ExperimentalKotest
//private class RegularClassAndLeafIsolation : DescribeSpec() {
//   init {
//      isolationMode = IsolationMode.InstancePerLeaf
//
//      withData(
//         NotADataClass(1),
//         NotADataClass(2),
//         NotADataClass(3),
//      ) { d ->
//         d.a shouldNotBe 2
//      }
//
//      describe("foo") {
//         withData(
//            NotADataClass(1),
//            NotADataClass(2),
//            NotADataClass(3),
//         ) { d ->
//            d.a shouldNotBe 2
//         }
//      }
//   }
//}
//
//@ExperimentalKotest
//private class DataClassWithNonDataParameterAndLeafIsolation : DescribeSpec() {
//   init {
//      isolationMode = IsolationMode.InstancePerLeaf
//
//      withData(
//         DataClassWithNonDataParameter(1, NotADataClass(1)),
//         DataClassWithNonDataParameter(1, NotADataClass(2)),
//         DataClassWithNonDataParameter(1, NotADataClass(3)),
//      ) { d ->
//         d.b.a shouldNotBe 2
//      }
//
//      describe("foo") {
//         withData(
//            DataClassWithNonDataParameter(1, NotADataClass(1)),
//            DataClassWithNonDataParameter(1, NotADataClass(2)),
//            DataClassWithNonDataParameter(1, NotADataClass(3)),
//         ) { d ->
//            d.b.a shouldNotBe 2
//         }
//      }
//   }
//}
//
//data class DataClassWithNonDataParameter(val a: Int, val b: NotADataClass)
//
//class NotADataClass(val a: Int)
