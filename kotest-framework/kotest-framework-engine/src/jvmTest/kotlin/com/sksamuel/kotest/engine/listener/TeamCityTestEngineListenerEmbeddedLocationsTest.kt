package com.sksamuel.kotest.engine.listener

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
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith

/**
 * The engine no longer wraps test names with `<kotest>...</kotest>` location tags - jump-to-source
 * navigation now flows via the JUnit Platform `MethodSource` (and `proxy.locationUrl`).  This test
 * pins the resulting [TeamCityTestEngineListener] output: every lifecycle message names the test
 * by its plain descriptor path, with no embedded `<kotest>` tag and no legacy ` -- ` separator
 * between nested segments.
 */
class TeamCityTestEngineListenerEmbeddedLocationsTest : FunSpec() {
   init {

      context("!when embedded locations are enabled") {
         val listener = TeamCityTestEngineListener(prefix = "tc")
         val tc = TestCase(
           TeamCityTestEngineListenerEmbeddedLocationsTest::class.toDescriptor().append("a"),
           TestNameBuilder.builder("a").build(),
           TeamCityTestEngineListenerEmbeddedLocationsTest(),
           {},
           SourceRef.None,
           TestType.Test,
         )
         test("testIgnored should include the embedded location") {
            val stdout = captureStandardOut {
              listener.testIgnored(tc, null)
            }
            stdout shouldBe "tc[testIgnored name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' result_status='Ignored']\n"
         }
         test("testStarted should include the embedded location") {
            val stdout = captureStandardOut {
              listener.testStarted(tc)
            }
            stdout shouldBe "tc[testStarted name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
         }
         test("testFinished should include the embedded location") {
            val stdout = captureStandardOut {
              listener.testFinished(tc, TestResult.Ignored(null))
            }
            stdout shouldBe "tc[testFinished name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' duration='0' result_status='Ignored']\n"
         }
         test("container started should include the embedded location") {
            val stdout = captureStandardOut {
              listener.testStarted(tc)
            }
            stdout shouldBe "tc[testSuiteStarted name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
         }
         test("container finished should include the embedded location") {
            val stdout = captureStandardOut {
              listener.testFinished(tc, TestResult.Ignored(null))
            }
            stdout shouldBe "tc[testSuiteFinished name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' duration='0' result_status='Ignored']\n"
         }
         return l
      }

      context("!when embedded locations are disabled") {
         val listener = TeamCityTestEngineListener(prefix = "tc")
         val tc = TestCase(
           TeamCityTestEngineListenerEmbeddedLocationsTest::class.toDescriptor().append("a"),
           TestNameBuilder.builder("a").build(),
           TeamCityTestEngineListenerEmbeddedLocationsTest(),
           {},
           SourceRef.None,
           TestType.Test,
         )
         test("outputTestIgnored should NOT include the embedded location") {
            val stdout = captureStandardOut {
              listener.testIgnored(tc, null)
            }
            stdout shouldBe "tc[testIgnored name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' result_status='Ignored']\n"
         }
         test("outputTestStarted should NOT include the embedded location") {
            val stdout = captureStandardOut {
              listener.testStarted(tc)
            }
            stdout shouldBe "tc[testStarted name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
         }
         stdout shouldNotContain "<kotest>"
         stdout shouldNotContain " -- "
         stdout shouldStartWith "tc[testIgnored "
         stdout shouldContain "name='$specFqn/a'"
         stdout shouldContain "result_status='Ignored'"
      }

      test("testStarted should emit a plain test name with no <kotest> tag") {
         val listener = freshListenerWithSpec()
         val stdout = captureStandardOut {
            listener.testStarted(tc)
         }
         stdout shouldNotContain "<kotest>"
         stdout shouldNotContain " -- "
         stdout shouldStartWith "tc[testStarted "
      }

      test("testFinished should emit a plain test name with no <kotest> tag") {
         val listener = freshListenerWithSpec()
         val stdout = captureStandardOut {
            listener.testFinished(tc, TestResult.Ignored(null))
         }
         stdout shouldNotContain "<kotest>"
         stdout shouldNotContain " -- "
      }
   }
}

private class DummyTcSpec : FunSpec({})
