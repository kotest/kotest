package com.sksamuel.kotest.engine.listener

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe

class TeamCityTestEngineListenerEmbeddedLocationsTest : FunSpec() {
   init {

      context("!when embedded locations are enabled") {
         val listener = TeamCityTestEngineListener(prefix = "tc", details = true)
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
      }

      context("!when embedded locations are disabled") {
         val listener = TeamCityTestEngineListener(prefix = "tc", details = true)
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
         test("outputTestFinished should NOT include the embedded location") {
            val stdout = captureStandardOut {
              listener.testFinished(tc, TestResult.Ignored(null))
            }
            stdout shouldBe "tc[testFinished name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' duration='0' result_status='Ignored']\n"
         }
         test("outputTestSuiteStarted should NOT include the embedded location") {
            val stdout = captureStandardOut {
              listener.testStarted(tc)
            }
            stdout shouldBe "tc[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
         }
         test("outputTestSuiteFinished should NOT include the embedded location") {
            val stdout = captureStandardOut {
              listener.testFinished(tc, TestResult.Ignored(null))
            }
            stdout shouldBe "tc[testSuiteFinished name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' duration='0' result_status='Ignored']\n"
         }
      }
   }
}
