package com.sksamuel.kotest.engine.listener

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.shouldFail
import io.kotest.assertions.show.Printed
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
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

class TeamCityTestEngineListenerTest : FunSpec() {

   private val kclass: KClass<out Spec> = TeamCityTestEngineListenerTest::class

   private val testCaseContainer = TestCase(
      kclass.toDescriptor().append("my container"),
      TestName("my container"),
      this@TeamCityTestEngineListenerTest,
      { },
      sourceRef(),
      TestType.Container,
      ResolvedTestConfig.default,
      null,
      null
   )

   private val testCaseTest = testCaseContainer.copy(
      descriptor = testCaseContainer.descriptor.append("my test case"),
      TestName("my test case"),
      type = TestType.Test
   )

   override fun afterSpec(spec: Spec) {
      error("garaeasd")
   }

   init {

      afterSpec {
         error("tertert")
      }

      context("fuck me up big style") {
         test("monkey") { }
         error("foo")
      }

      test("specStarted should write testSuiteStarted") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").specStarted(kclass)
         } shouldBe "testcity[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='spec']$nl"
      }

      test("specExit errors should add placeholder test") {
         val listener = TeamCityTestEngineListener("testcity")
         listener.specEnter(kclass)
         val out = captureStandardOut {
            listener.specExit(kclass, Exception("whip!"))
         }
         out.shouldContain("""testcity[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='spec']""")
         out.shouldContain("""testcity[testStarted name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' test_type='test']""")
         out.shouldContain("""testcity[testFailed name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' message='whip!' details='java.lang.Exception:""")
         out.shouldContain("""testcity[testFinished name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' test_type='test'""")
         out.shouldContain("""testcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' result_status='Error' test_type='spec']""")
      }

      test("specExit should write testSuiteFinished in error is null") {
         val listener = TeamCityTestEngineListener("testcity")
         listener.specStarted(kclass)
         captureStandardOut {
            listener.specExit(kclass, null)
         } shouldBe "testcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' result_status='Success' test_type='spec']$nl"
      }

      test("inactive spec should write each root test as ignored") {
         val listener = TeamCityTestEngineListener("testcity")
         listener.specEnter(kclass)
         listener.specInactive(kclass, mapOf(testCaseTest to TestResult.Ignored(null)))
         val out = captureStandardOut {
            listener.specExit(kclass, null)
         }
         out.shouldContain("testcity[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest'")
         out.shouldContain("testcity[testIgnored name='my test case'")
         out.shouldContain("testcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest'")
      }

      test("inactive spec should write dummy ignored test if there are no tests") {
         val listener = TeamCityTestEngineListener("testcity")
         val out = captureStandardOut {
            listener.specEnter(kclass)
            listener.specInactive(kclass, emptyMap())
            listener.specExit(kclass, null)
         }
         out.shouldContain("testcity[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest'")
         out.shouldContain("testcity[testIgnored name='<no tests>'")
         out.shouldContain("testcity[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest'")
      }

      test("testStarted should write testSuiteStarted for parent test") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testStarted(testCaseContainer)
         } shouldBe "testcity[testSuiteStarted name='my container' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='container']$nl"
      }

      test("testStarted should write testStarted for TestType.Test") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testStarted(testCaseTest)
         } shouldBe "testcity[testStarted name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='test']$nl"
      }

      test("testFinished should write testFinished for TestType.Test success") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseTest, TestResult.success(234))
         } shouldBe "testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='234' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='test' result_status='Success']$nl"
      }

      test("testFinished should write testSuiteFinished for TestType.Container success") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseContainer, TestResult.success(15))
         } shouldBe "testcity[testSuiteFinished name='my container' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='15' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='container' result_status='Success']$nl"
      }

      test("testFinished should write testFailed and testFinished for TestType.Test failure") {
         val msg = captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(
               testCaseTest,
               TestResult.failure(AssertionError("wibble"), 51)
            )
         }
         msg.shouldStartWith("testcity[testFailed name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='51' message='wibble' details='java.lang.AssertionError: wibble|n\tat com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest")
         msg.shouldEndWith("testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='51' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='test' result_status='Failure']$nl")
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
            TeamCityTestEngineListener("testcity").testFinished(testCaseTest, TestResult.Ignored("ignore me?"))
         } shouldBe ""
      }

      test("testFinished should write nothing for container ignored") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(testCaseContainer, TestResult.Ignored("ignore me?"))
         } shouldBe ""
      }

      test("testIgnored should write testIgnored") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").testIgnored(testCaseTest, "ignore me?")
         } shouldBe "testcity[testIgnored name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='test' message='ignore me?' result_status='Ignored']$nl"
      }

      test("specFinish should write nothing") {
         captureStandardOut {
            TeamCityTestEngineListener("testcity").specFinished(kclass, emptyMap())
         } shouldBe ""
      }

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
         out.shouldStartWith("testcity[testFailed name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='8123' message='Test failed' details='java.lang.AssertionError: |nThe following 2 assertions failed:|n1) Test failed for (a, 2), (b, 3), (max, 1) with error expected:<1> but was:<3>|n	at com.sksa")
         out.shouldEndWith("testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='8123' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='test' result_status='Error']$nl")
      }

      test("engine errors should be output as a placeholder engine test") {
         val out = captureStandardOut {
            TeamCityTestEngineListener("testcity").engineFinished(listOf(Exception("foo")))
         }
         out shouldBe "testcity[testStarted name='Engine failure']${nl}testcity[testFailed name='Engine failure' message='foo']${nl}"
      }

      test("multiple engine errors should be output as a placeholder engine test") {
         val out = captureStandardOut {
            TeamCityTestEngineListener("testcity").engineFinished(listOf(Exception("foo"), Exception("bar")))
         }
         out shouldBe "testcity[testStarted name='Engine failure']${nl}testcity[testFailed name='Engine failure' message='foo|nbar']${nl}"
      }

      test("should use comparison values with a supported exception type") {
         val out = captureStandardOut {
            TeamCityTestEngineListener("testcity").testFinished(
               testCaseTest,
               TestResult.error(failure(Expected(Printed("expected")), Actual(Printed("actual"))), 14)
            )
         }
         out.shouldHaveLineCount(3)
         out.shouldStartWith("testcity[testFailed name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='14' message='expected:<expected> but was:<actual>' details='io.kotest.assertions.AssertionFailedError: expected:<expected> but was:<actual>|n\tat com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest")
         out.shouldContain("type='comparisonFailure'")
         out.shouldContain("actual='actual'")
         out.shouldContain("expected='expected'")
         out.shouldEndWith("testcity[testFinished name='my test case' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container -- my test case' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/my container' duration='14' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1' test_type='test' result_status='Error']$nl")
      }
   }
}
