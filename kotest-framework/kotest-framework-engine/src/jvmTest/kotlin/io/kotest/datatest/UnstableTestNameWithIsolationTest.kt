package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@ExperimentalKotest
@Isolate // sets global values via configuration so must be isolated
@EnabledIf(NotMacOnGithubCondition::class)
class UnstableTestNameWithIsolationTest : FunSpec() {
   init {

      val results = mutableListOf<String>()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            results.add(result.name)
         }
      }

      beforeTest {
         results.clear()
      }

      test("isolation mode leaf + regular classes should show all tests in data testing") {

         TestEngineLauncher()
            .withListener(listener)
            .withClasses(RegularClassAndIsolation::class)
            .launch()

         results shouldBe listOf(
            TestStatus.Success.name,
            TestStatus.Failure.name,
            TestStatus.Success.name,
            TestStatus.Success.name,
            TestStatus.Failure.name,
            TestStatus.Success.name,
            TestStatus.Success.name, // final success is the foo context
         )
      }

      test("isolation mode leaf + data classes with regular class param should show all tests in data testing") {

         TestEngineLauncher()
            .withListener(listener)
            .withClasses(DataClassWithNonDataParameterAndIsolation::class)
            .launch()

         results shouldBe listOf(
            TestStatus.Success.name,
            TestStatus.Failure.name,
            TestStatus.Success.name,
            TestStatus.Success.name,
            TestStatus.Failure.name,
            TestStatus.Success.name,
            TestStatus.Success.name,  // final success is the foo context
         )
      }
   }
}

@ExperimentalKotest
private class RegularClassAndIsolation : DescribeSpec() {
   init {
      isolationMode = IsolationMode.InstancePerRoot

      withData(
         NotADataClass(1),
         NotADataClass(2),
         NotADataClass(3),
      ) { d ->
         d.a shouldNotBe 2
      }

      describe("foo") {
         withData(
            NotADataClass(1),
            NotADataClass(2),
            NotADataClass(3),
         ) { d ->
            d.a shouldNotBe 2
         }
      }
   }
}

@ExperimentalKotest
private class DataClassWithNonDataParameterAndIsolation : DescribeSpec() {
   init {
      isolationMode = IsolationMode.InstancePerRoot

      withData(
         DataClassWithNonDataParameter(1, NotADataClass(1)),
         DataClassWithNonDataParameter(1, NotADataClass(2)),
         DataClassWithNonDataParameter(1, NotADataClass(3)),
      ) { d ->
         d.b.a shouldNotBe 2
      }

      describe("foo") {
         withData(
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
