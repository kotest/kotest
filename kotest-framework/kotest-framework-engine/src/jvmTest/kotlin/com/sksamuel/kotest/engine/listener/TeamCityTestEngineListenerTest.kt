package com.sksamuel.kotest.engine.listener

import io.kotest.assertions.KotestAssertionFailedError
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import org.opentest4j.AssertionFailedError
import java.io.FileNotFoundException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class TeamCityTestEngineListenerTest : FunSpec() {

   init {

      val a = TestCase(
         TeamCityTestEngineListenerTest::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         this@TeamCityTestEngineListenerTest,
         { },
         SourceRef.ClassLineSource("foo.bar.Test", 12),
         TestType.Container,
         null,
         null,
         null
      )

      val b = a.copy(
         parent = a,
         name = TestNameBuilder.builder("b").build(),
         descriptor = a.descriptor.append("b"),
         source = SourceRef.ClassLineSource("foo.bar.Test", 17),
      )

      val c = b.copy(
         parent = b,
         name = TestNameBuilder.builder("c").build(),
         descriptor = b.descriptor.append("c"),
         type = TestType.Test,
         source = SourceRef.ClassLineSource("foo.bar.Test", 33),
      )

      test("should support nested tests") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(c, TestResult.Success(123.milliseconds))
            listener.testFinished(b, TestResult.Success(324.milliseconds))
            listener.testFinished(a, TestResult.Success(653.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='324' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='653' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support errors in tests") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(c, TestResult.Error(653.milliseconds, Exception("boom")))
            listener.testFinished(b, TestResult.Success(123.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='653' message='boom' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='653' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='123' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support ignored tests with reason") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testIgnored(c, "don't like it")
            listener.testFinished(b, TestResult.Success(123.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testIgnored name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33' message='don|'t like it' result_status='Ignored']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='123' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support errors in test suites by adding placeholder test") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(c, TestResult.Success(123.milliseconds))
            listener.testFinished(b, TestResult.Error(653.milliseconds, Exception("boom")))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' result_status='Success']
a[testStarted name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b']
a[testFailed name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' message='boom']
a[testFinished name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='653' result_status='Error']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support errors in specs by adding placeholder test") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(c, TestResult.Success(123.milliseconds))
            listener.testFinished(b, TestResult.Success(555.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Error(0.seconds, Exception("wobble"))
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testStarted name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testFailed name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' message='wobble']
a[testFinished name='Exception' id='Exception' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support multiline error messages") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(
               c,
               TestResult.Error(
                  123.milliseconds, FileNotFoundException(
                     """
               well this is a
               big
               message"""
                  )
               )
            )
            listener.testFinished(b, TestResult.Success(555.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' message='well this is a' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='123' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should write engine errors") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(c, TestResult.Success(555.milliseconds))
            listener.testFinished(b, TestResult.Success(555.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(listOf(Exception("big whoop")))
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testStarted name='Engine exception']
a[testFailed name='Engine exception' message='big whoop']
a[testFinished name='Engine exception']
"""
      }

      test("should write multiple engine errors") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(c, TestResult.Success(555.milliseconds))
            listener.testFinished(b, TestResult.Success(555.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(listOf(Exception("big whoop"), Exception("big whoop 2")))
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' result_status='Success']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testStarted name='Engine exception 1']
a[testFailed name='Engine exception 1' message='big whoop']
a[testFinished name='Engine exception 1']
a[testStarted name='Engine exception 2']
a[testFailed name='Engine exception 2' message='big whoop 2']
a[testFinished name='Engine exception 2']
"""
      }

      test("should use comparison values with kotest assertion failed error type") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(
               c,
               TestResult.Error(
                  555.milliseconds,
                  KotestAssertionFailedError("boom", null, "expected", "actual")
               )
            )
            listener.testFinished(b, TestResult.Success(555.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' message='boom' type='comparisonFailure' expected='expected' actual='actual' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should use comparison values with opentest4j assertion failed error type") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testStarted(b)
            listener.testStarted(c)
            listener.testFinished(
               c,
               TestResult.Error(
                  555.milliseconds,
                  AssertionFailedError("boom", "expected", "actual"),
               )
            )
            listener.testFinished(b, TestResult.Success(555.milliseconds))
            listener.testFinished(a, TestResult.Success(324.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteStarted name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' locationHint='kotest://foo.bar.Test:17']
a[testStarted name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' locationHint='kotest://foo.bar.Test:33']
a[testFailed name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' message='boom' type='comparisonFailure' expected='expected' actual='actual' result_status='Error']
a[testFinished name='c' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' duration='555' result_status='Error']
a[testSuiteFinished name='b' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' duration='555' result_status='Success']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='324' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should clear state between specs") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a", details = false)
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testFinished(a, TestResult.Success(124.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(a)
            listener.testFinished(a, TestResult.Success(523.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         output shouldBe """a[enteredTheMatrix durationStrategy='MANUAL']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='124' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://foo.bar.Test:12']
a[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a' parent_id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' duration='523' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' id='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }
   }
}
