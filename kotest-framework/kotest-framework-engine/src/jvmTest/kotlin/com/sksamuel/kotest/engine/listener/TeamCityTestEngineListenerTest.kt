package com.sksamuel.kotest.engine.listener

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.Node
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import java.io.FileNotFoundException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TeamCityTestEngineListenerTest : FunSpec() {

   init {

      val a = TestCase(
         TeamCityTestEngineListenerTest::class.toDescriptor().append("a"),
         TestName("a"),
         this@TeamCityTestEngineListenerTest,
         { },
         SourceRef.ClassSource("foo.bar.Test", 12),
         TestType.Container,
         ResolvedTestConfig.default,
         null,
         null
      )

      val b = a.copy(
         parent = a,
         name = TestName("b"),
         descriptor = a.descriptor.append("b"),
         source = SourceRef.ClassSource("foo.bar.Test", 17),
      )

      val c = b.copy(
         parent = b,
         name = TestName("c"),
         descriptor = b.descriptor.append("c"),
         type = TestType.Test,
         source = SourceRef.ClassSource("foo.bar.Test", 33),
      )

      test("should support nested tests") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Success(123.milliseconds))
            listener.executionFinished(Node.Test(b), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(653.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' locationHint='kotest:class://foo.bar.Test:33' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='324' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='653' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should support errors in tests") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Error(653.milliseconds, Exception("boom")))
            listener.executionFinished(Node.Test(b), TestResult.Success(123.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='653' locationHint='kotest:class://foo.bar.Test:33' message='boom' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='653' locationHint='kotest:class://foo.bar.Test:33' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='123' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should support ignored tests with reason") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Ignored("don't like it"))
            listener.executionFinished(Node.Test(b), TestResult.Success(123.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testIgnored name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33' message='don|'t like it' result_status='Ignored']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='123' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should support errors in test suites by adding placeholder test") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Success(123.milliseconds))
            listener.executionFinished(Node.Test(b), TestResult.Error(653.milliseconds, Exception("boom")))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' locationHint='kotest:class://foo.bar.Test:33' result_status='Success']
a[testStarted name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b']
a[testFailed name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' message='boom']
a[testFinished name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='653' locationHint='kotest:class://foo.bar.Test:17' result_status='Error']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should support errors in specs by adding placeholder test") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Success(123.milliseconds))
            listener.executionFinished(Node.Test(b), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.Error(Duration.ZERO, Exception("wobble")))
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' locationHint='kotest:class://foo.bar.Test:33' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testStarted name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testFailed name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' message='wobble']
a[testFinished name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should support multiline error messages") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(
               Node.Test(c),
               TestResult.Error(
                  123.milliseconds, FileNotFoundException(
                     """
               well this is a
               big
               message"""
                  )
               )
            )
            listener.executionFinished(Node.Test(b), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' locationHint='kotest:class://foo.bar.Test:33' message='well this is a' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' locationHint='kotest:class://foo.bar.Test:33' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should write engine errors") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(b), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.Error(Duration.ZERO, Exception("big whoop")))
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' locationHint='kotest:class://foo.bar.Test:33' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='Engine exception']
a[testFailed name='Engine exception' message='big whoop']
a[testFinished name='Engine exception']
"""
      }

      test("should write multiple engine errors") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(Node.Test(c), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(b), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.Error(Duration.ZERO, MultipleExceptions(listOf(Exception("big whoop"), Exception("big whoop 2")))))
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' locationHint='kotest:class://foo.bar.Test:33' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='Engine exception 1']
a[testFailed name='Engine exception 1' message='big whoop']
a[testFinished name='Engine exception 1']
a[testStarted name='Engine exception 2']
a[testFailed name='Engine exception 2' message='big whoop 2']
a[testFinished name='Engine exception 2']
"""
      }

      test("should use comparison values with a supported exception type") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionStarted(Node.Test(b))
            listener.executionStarted(Node.Test(c))
            listener.executionFinished(
               Node.Test(c),
               TestResult.Error(
                  555.milliseconds,
                  failure(Expected(Printed("expected")), Actual(Printed("actual")))
               )
            )
            listener.executionFinished(Node.Test(b), TestResult.Success(555.milliseconds))
            listener.executionFinished(Node.Test(a), TestResult.Success(324.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest:class://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest:class://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' locationHint='kotest:class://foo.bar.Test:33' message='expected:<expected> but was:<actual>' type='comparisonFailure' actual='actual' expected='expected' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' locationHint='kotest:class://foo.bar.Test:33' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' locationHint='kotest:class://foo.bar.Test:17' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }

      test("should clear state between specs") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.executionStarted(Node.Engine(EngineContext.empty))
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionFinished(Node.Test(a), TestResult.Success(124.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionStarted(Node.Spec(TeamCityTestEngineListenerTest::class))
            listener.executionStarted(Node.Test(a))
            listener.executionFinished(Node.Test(a), TestResult.Success(523.milliseconds))
            listener.executionFinished(Node.Spec(TeamCityTestEngineListenerTest::class), TestResult.success)
            listener.executionFinished(Node.Engine(EngineContext.empty), TestResult.success)
         }
         output shouldBe """a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='124' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://foo.bar.Test:12']
a[testFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='523' locationHint='kotest:class://foo.bar.Test:12' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest:class://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
"""
      }
   }
}
