package com.sksamuel.kotest.engine.listener

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
import java.io.FileNotFoundException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class TeamCityTestEngineListenerTest : FunSpec() {

   // Stack traces are non-deterministic; strip the details attribute so the rest of the message can be asserted exactly.
   private fun stripDetails(output: String): String = output.replace(Regex(" details='[^']*'"), "")

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

      test("nested tests are not output") {
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='123' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support errors in tests") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFailed name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' message='boom' result_status='Error']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='653' result_status='Error']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support ignored tests with reason") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testIgnored name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest/a -- b -- c' locationHint='kotest://foo.bar.Test:33' message='don|'t like it' result_status='Ignored']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support errors in test suites by adding a placeholder test") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='123' result_status='Success']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ Exception']
a[testFailed name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ Exception' message='boom']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ Exception']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support errors in specs by adding a placeholder test") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='123' result_status='Success']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest ⇢ Exception']
a[testFailed name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest ⇢ Exception' message='wobble']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest ⇢ Exception']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should support multiline error messages") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFailed name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' message='well this is a' result_status='Error']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='123' result_status='Error']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should write engine errors as top level failed tests") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='555' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testStarted name='Exception']
a[testFailed name='Exception' message='big whoop']
a[testFinished name='Exception']
"""
      }

      test("should write multiple engine errors") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' locationHint='kotest://foo.bar.Test:33']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a ⇢ b ⇢ c' duration='555' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testStarted name='Exception']
a[testFailed name='Exception' message='big whoop']
a[testFinished name='Exception']
a[testStarted name='Exception']
a[testFailed name='Exception' message='big whoop 2']
a[testFinished name='Exception']
"""
      }

      test("should clear state between specs") {
         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
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
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }

      test("should remove periods from output") {
         val cperiods = c.copy(
            parent = null,
            name = TestNameBuilder.builder("a.b.c").build(),
            descriptor = a.descriptor.append("a.b.c"),
            source = SourceRef.ClassLineSource("foo.bar.Test", 17),
         )

         val output = captureStandardOut {
            val listener = TeamCityTestEngineListener("a")
            listener.engineStarted()
            listener.specStarted(SpecRef.Reference(TeamCityTestEngineListenerTest::class))
            listener.testStarted(cperiods)
            listener.testFinished(cperiods, TestResult.Success(124.milliseconds))
            listener.specFinished(
               SpecRef.Reference(TeamCityTestEngineListenerTest::class),
               TestResult.Success(0.seconds)
            )
            listener.engineFinished(emptyList())
         }
         stripDetails(output) shouldBe """a[enteredTheMatrix]
a[testSuiteStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest' locationHint='kotest://com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest:1']
a[testStarted name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a b c' locationHint='kotest://foo.bar.Test:17']
a[testFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest.a b c' duration='124' result_status='Success']
a[testSuiteFinished name='com.sksamuel.kotest.engine.listener.TeamCityTestEngineListenerTest']
"""
      }
   }
}
