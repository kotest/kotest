package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
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
@EnabledIf(LinuxOnlyGithubCondition::class)
class UnstableTestNameTest : FunSpec() {
   init {

      val results = mutableListOf<Pair<String, String>>()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            results.add(Pair(testCase.name.name, result.name))
         }
      }

      beforeTest {
         results.clear()
      }

      context("unstable classes should show all tests in data testing") {
         withData(
            IsolationMode.SingleInstance,
            IsolationMode.InstancePerRoot,
         ) { isolationMode ->

            val config = object : AbstractProjectConfig() {
               override val isolationMode: IsolationMode = isolationMode
            }

            TestEngineLauncher()
               .withProjectConfig(config)
               .withListener(listener)
               .withClasses(NonDataClassesTest::class)
               .launch()

            results shouldBe listOf(
               Pair("io kotest datatest NotADataClass", TestStatus.Success.name),
               Pair("(1) io kotest datatest NotADataClass", TestStatus.Failure.name),
               Pair("(2) io kotest datatest NotADataClass", TestStatus.Success.name),
               Pair("io kotest datatest DataClassWithNonDataParameter", TestStatus.Success.name),
               Pair("(1) io kotest datatest DataClassWithNonDataParameter", TestStatus.Failure.name),
               Pair("(2) io kotest datatest DataClassWithNonDataParameter", TestStatus.Success.name),
               Pair("io kotest datatest NotADataClass", TestStatus.Success.name),
               Pair("(1) io kotest datatest NotADataClass", TestStatus.Failure.name),
               Pair("(2) io kotest datatest NotADataClass", TestStatus.Success.name),
               Pair("foo", TestStatus.Success.name), // this success is the foo context
               Pair("io kotest datatest DataClassWithNonDataParameter", TestStatus.Success.name),
               Pair("(1) io kotest datatest DataClassWithNonDataParameter", TestStatus.Failure.name),
               Pair("(2) io kotest datatest DataClassWithNonDataParameter", TestStatus.Success.name),
               Pair("bar", TestStatus.Success.name), // this success is the bar context
            )
         }
      }
   }
}

class NonDataClassesTest : DescribeSpec() {
   init {

      withData(
         NotADataClass(1),
         NotADataClass(2),
         NotADataClass(3),
      ) { d ->
         d.a shouldNotBe 2
      }

      withData(
         DataClassWithNonDataParameter(1, NotADataClass(1)),
         DataClassWithNonDataParameter(1, NotADataClass(2)),
         DataClassWithNonDataParameter(1, NotADataClass(3)),
      ) { d ->
         d.b.a shouldNotBe 2
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

      describe("bar") {
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
