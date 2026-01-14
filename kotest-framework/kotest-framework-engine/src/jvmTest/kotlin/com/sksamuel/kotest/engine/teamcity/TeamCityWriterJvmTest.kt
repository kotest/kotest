package com.sksamuel.kotest.engine.teamcity

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.teamcity.TeamCityWriter
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe

class TeamCityWriterJvmTest : FunSpec() {
   init {

      test("outputTestIgnored should use embedded locations when enabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), true)
         val tc = TestCase(
            TeamCityWriterJvmTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterJvmTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val stdout = captureStandardOut {
            writer.outputTestIgnored(tc, TestResult.Ignored(null))
         }
         stdout shouldBe "tc[testIgnored name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' result_status='Ignored']\n"
      }

      test("outputTestIgnored should use the display name when embedded locations are disabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), false)
         val tc = TestCase(
            TeamCityWriterJvmTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterJvmTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val stdout = captureStandardOut {
            writer.outputTestIgnored(tc, TestResult.Ignored(null))
         }
         stdout shouldBe "tc[testIgnored name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest' result_status='Ignored']\n"
      }

      test("outputTestStarted should use embedded locations when enabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), true)
         val tc = TestCase(
            TeamCityWriterJvmTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterJvmTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val stdout = captureStandardOut {
            writer.outputTestStarted(tc)
         }
         stdout shouldBe "tc[testStarted name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
      }

      test("outputTestStarted should use the display name when embedded locations are disabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), false)
         val tc = TestCase(
            TeamCityWriterJvmTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterJvmTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val stdout = captureStandardOut {
            writer.outputTestStarted(tc)
         }
         stdout shouldBe "tc[testStarted name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
      }

      test("outputTestSuiteStarted should use embedded locations when enabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), true)
         val tc = TestCase(
            TeamCityWriterJvmTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterJvmTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val stdout = captureStandardOut {
            writer.outputTestSuiteStarted(tc)
         }
         stdout shouldBe "tc[testSuiteStarted name='<kotest>com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a</kotest>a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
      }

      test("outputTestSuiteStarted should use the display name when embedded locations are disabled") {
         val writer = TeamCityWriter("tc", DisplayNameFormatting(null), false)
         val tc = TestCase(
            TeamCityWriterJvmTest::class.toDescriptor().append("a"),
            TestNameBuilder.builder("a").build(),
            TeamCityWriterJvmTest(),
            {},
            SourceRef.None,
            TestType.Test,
         )
         val stdout = captureStandardOut {
            writer.outputTestSuiteStarted(tc)
         }
         stdout shouldBe "tc[testSuiteStarted name='a' id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest/a' parent_id='com.sksamuel.kotest.engine.teamcity.TeamCityWriterJvmTest']\n"
      }
   }
}
