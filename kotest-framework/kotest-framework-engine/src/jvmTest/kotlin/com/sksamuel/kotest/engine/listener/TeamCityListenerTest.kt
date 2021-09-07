package com.sksamuel.kotest.engine.listener

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.shouldFail
import io.kotest.assertions.show.Printed
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldStartWith
import kotlin.math.max
import kotlin.reflect.KClass

private val nl = System.lineSeparator()

class TeamCityListenerTest : FunSpec() {

   private val kclass: KClass<out Spec> = TeamCityListenerTest::class

   private val testCaseContainer = TestCase(
      kclass.toDescription().appendContainer("my container"),
      this@TeamCityListenerTest,
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

      test("specStarted should write testSuiteStarted") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").specStarted(kclass)
         } shouldBe "${nl}testcity[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='spec']$nl"
      }

      test("testStarted should write testSuiteStarted for TestType.Container") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testStarted(testCaseContainer)
         } shouldBe "${nl}testcity[testSuiteStarted name='my container' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='container']$nl"
      }

      test("testStarted should write testStarted for TestType.Test") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testStarted(testCaseTest)
         } shouldBe "${nl}testcity[testStarted name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='test']$nl"
      }

      test("testFinished should write testFinished for TestType.Test success") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseTest, TestResult.success(234))
         } shouldBe "${nl}testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='234' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='test' result_status='Success']$nl"
      }

      test("testFinished should write testSuiteFinished for TestType.Container success") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseContainer, TestResult.success(15))
         } shouldBe "${nl}testcity[testSuiteFinished name='my container' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' duration='15' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='container' result_status='Success']$nl"
      }

      test("testFinished should write testFailed and testFinished for TestType.Test failure") {
         val msg = captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(
               testCaseTest,
               TestResult.failure(AssertionError("wibble"), 51)
            )
         }
         msg.shouldStartWith("${nl}testcity[testFailed name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='51' message='wibble' details='java.lang.AssertionErrorː wibble|n\tat com.sksamuel.kotest.engine.listener.TeamCityListenerTest")
         msg.shouldEndWith("testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='51' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='test' result_status='Failure']$nl")
      }

      test("testFinished should insert dummy test and write testSuiteFinished for TestType.Container failure") {
         val out = captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(
               testCaseContainer,
               TestResult.failure(AssertionError("wibble"), 51)
            )
         }
         out.shouldContain("testcity[testStarted name='my container <error>'")
         out.shouldContain("testcity[testFailed name='my container <error>'")
         out.shouldContain("testcity[testFinished name='my container <error>'")
      }

      test("testFinished should write nothing for TestType.Test ignored") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseTest, TestResult.ignored("ignore me?"))
         } shouldBe ""
      }

      test("testFinished should write nothing for container ignored") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseContainer, TestResult.ignored("ignore me?"))
         } shouldBe ""
      }

      test("testIgnored should write testIgnored") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testIgnored(testCaseTest, "ignore me?")
         } shouldBe "${nl}testcity[testIgnored name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='test' message='ignore me?' result_status='Ignored']$nl"
      }

      test("specFinish should write nothing") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").specFinished(kclass, emptyMap())
         } shouldBe ""
      }

      test("specExit should write testSuiteFinished in error is null") {
         val listener = TeamCityTestEngineListener("testcity")
         listener.specStarted(kclass)
         captureStandardOut {
            listener.specExit(kclass, null)
         } shouldBe "${nl}testcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' result_status='Success' test_type='spec']$nl"
      }

//      test("specFinished should insert dummy test and write testSuiteFinished for spec error") {
//         val err = captureStandardErr {
//            captureStandardOut {
//               TeamCityTestEngineListener("testcity").specFinished(kclass, AssertionError("boom"), emptyMap())
//            } shouldBe nl +
//               "testcity[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityListenerTest <init>']$nl" +
//               "testcity[testFailed name='com.sksamuel.kotest.engine.listener.TeamCityListenerTest <init>' message='boom']$nl" +
//               "testcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest' test_type='spec' result_status='Failure']$nl"
//         }
//         err shouldStartWith "${nl}java.lang.AssertionError: boom$nl" +
//            "\tat com.sksamuel.kotest.engine.listener.TeamCityListenerTest"
//      }

      test("testFinished with error should handle multiline messages") {

         val error = shouldFail {
            forAll(
               row(2, 3, 1),
               row(0, 2, 0)
            ) { a, b, max ->
               max(a, b) shouldBe max
            }
         }

         val out = captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseTest, TestResult.error(error, 8123))
         }
         out.shouldHaveLineCount(5) // the test failed, blank line, the test finished
         out.shouldStartWith("${nl}testcity[testFailed name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='8123' message='Test failed' details='java.lang.AssertionErrorː |n")
         out.shouldContain("duration='8123'")
         out.shouldEndWith("testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='8123' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='test' result_status='Error']$nl")
      }

      test("should use comparison values with a supported exception type") {
         val out = captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(
               testCaseTest,
               TestResult.error(failure(Expected(Printed("expected")), Actual(Printed("actual"))), 14)
            )
         }
         out.shouldHaveLineCount(5)
         out.shouldStartWith("${nl}testcity[testFailed name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='14' message='expectedː<expected> but wasː<actual>' details='io.kotest.assertions.AssertionFailedError")
         out.shouldContain("type='comparisonFailure'")
         out.shouldContain("actual='actual'")
         out.shouldContain("expected='expected'")
         out.shouldEndWith("testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container/my_test_case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityListenerTest/my_container' duration='14' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityListenerTest:1' test_type='test' result_status='Error']$nl")
      }
   }
}
