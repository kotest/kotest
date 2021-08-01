package com.sksamuel.kotest.engine.launcher

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.shouldFail
import io.kotest.assertions.show.Printed
import io.kotest.core.sourceRef
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.engine.reporter.TeamCityConsoleReporter
import io.kotest.extensions.system.captureStandardErr
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldStartWith
import kotlin.reflect.KClass

private val nl = System.lineSeparator()

@Isolate
class TeamCityConsoleReporterTest : FunSpec() {

   private val kclass: KClass<out Spec> = TeamCityConsoleReporterTest::class

   private val testCaseContainer = TestCase(
      kclass.toDescription().appendContainer("my context").appendContainer("my test container"),
      this@TeamCityConsoleReporterTest,
      { },
      sourceRef(),
      TestType.Container,
      TestCaseConfig(),
      null,
      null
   )

   private val testCaseTest = testCaseContainer.copy(
      description = testCaseContainer.description.appendTest("my test case"),
      type = TestType.Test
   )

   init {

      test("before spec class should write testSuiteStarted started") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").specStarted(kclass)
         } shouldBe "${nl}testcity[testSuiteStarted name='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest' locationHint='kotest://com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest:1' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest' test_type='spec']${nl}"
      }

      test("before test should write testSuiteStarted for TestType.Container") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").testStarted(testCaseContainer)
         } shouldBe "${nl}testcity[testSuiteStarted name='my test container' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context' locationHint='kotest://com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest:39' test_type='container']${nl}"
      }

      test("before test should write testStarted for TestType.Test") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").testStarted(testCaseTest)
         } shouldBe "${nl}testcity[testStarted name='my test case' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container/my_test_case' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' locationHint='kotest://com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest:39' test_type='Test']${nl}"
      }

      test("after spec class should write testSuiteFinished") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").specFinished(kclass, null, emptyMap())
         } shouldBe "${nl}testcity[testSuiteFinished name='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest' test_type='spec' result_status='Success']${nl}"
      }

      test("afterSpecClass should insert dummy test and write testSuiteFinished for spec error") {
         val err = captureStandardErr {
            captureStandardOut {
               TeamCityConsoleReporter("testcity").specFinished(kclass, AssertionError("boom"), emptyMap())
            } shouldBe nl +
               "testcity[testStarted name='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest <init>']${nl}" +
               "testcity[testFailed name='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest <init>' message='boom']${nl}" +
               "testcity[testSuiteFinished name='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest' test_type='Spec' result_status='Failure']${nl}"
         }
         err shouldStartWith "${nl}java.lang.AssertionError: boom${nl}" +
            "\tat com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest"
      }

      test("after test should write testSuiteFinished for container success") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").testFinished(testCaseContainer, TestResult.success(15))
         } shouldBe "${nl}testcity[testSuiteFinished name='my test container' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context' duration='15' test_type='container' result_status='Success']${nl}"
      }

      test("after test should insert dummy test and write testSuiteFinished for container error") {
         captureStandardErr {
            captureStandardOut {
               TeamCityConsoleReporter("testcity").testFinished(
                  testCaseContainer,
                  TestResult.failure(AssertionError("wibble"), 51)
               )
            } shouldBe nl +
               "testcity[testStarted name='my test container <init>']${nl}" +
               "testcity[testFailed name='my test container <init>' message='wibble']${nl}" +
               "testcity[testSuiteFinished name='my test container' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context' duration='51' test_type='container' result_status='Failure']${nl}"
         } shouldStartWith "${nl}java.lang.AssertionError: wibble${nl}" +
            "\tat com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest"
      }

      test("after test should write testSuiteFinished for container ignored") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").testFinished(testCaseContainer, TestResult.ignored("ignore me?"))
         } shouldBe "${nl}testcity[testSuiteFinished name='my test container' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context' test_type='container' result_status='Ignored']${nl}"
      }

      test("after test should write testFinished for test success") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").testFinished(testCaseTest, TestResult.success(234))
         } shouldBe "${nl}testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container/my_test_case' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' duration='234' test_type='Test' result_status='Success']${nl}"
      }

      test("after test should write testIgnored for test with ignored") {
         captureStandardOut {
            TeamCityConsoleReporter("testcity").testFinished(testCaseTest, TestResult.ignored("ignore me?"))
         } shouldBe "${nl}testcity[testIgnored name='my test case' id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container/my_test_case' parent_id='com.sksamuel.kotest.engine.launcher.TeamCityConsoleReporterTest/my_context/my_test_container' message='ignore me?' test_type='Test' result_status='Ignored']${nl}"
      }

      test("after test with error should handle multiline messages") {

         val error = shouldFail {
            forAll(
               row(2, 3, 1),
               row(0, 2, 0)
            ) { a, b, max ->
               kotlin.math.max(a, b) shouldBe max
            }
         }

         val out = captureStandardOut {
            TeamCityConsoleReporter("testcity").testFinished(testCaseTest, TestResult.error(error, 8123))
         }
         out.trim().shouldHaveLineCount(1)
         out.shouldStartWith("${nl}testcity[testFailed name='my test case' message='Test failed' details='")
         out.shouldContain("duration='8123'")
      }

      test("should use comparison values with a supported exception type") {
         val out = captureStandardOut {
            TeamCityConsoleReporter("testcity").testFinished(
               testCaseTest,
               TestResult.error(failure(Expected(Printed("expected")), Actual(Printed("actual"))), 14)
            )
         }
         out.shouldStartWith("${nl}testcity[testFailed name='my test case' message='expected:<expected> but was:<actual>' details='")
         out.shouldContain("type='comparisonFailure'")
         out.shouldContain("actual='actual'")
         out.shouldContain("expected='expected'")
         out.trim().shouldHaveLineCount(1)
      }
   }
}
