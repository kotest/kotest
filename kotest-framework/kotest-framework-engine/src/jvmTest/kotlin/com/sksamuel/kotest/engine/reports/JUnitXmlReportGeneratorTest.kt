package com.sksamuel.kotest.engine.reports

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.reports.JUnitXmlReportGenerator
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.time.Instant
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * A simple fixed clock implementation for testing
 */
class FixedClock(private val instant: Instant = Instant.parse("2023-01-01T12:00:00Z")) : Clock {
   override fun now(): kotlin.time.Instant = kotlin.time.Instant.fromEpochMilliseconds(instant.toEpochMilli())
}

class JUnitXmlReportGeneratorTest : FunSpec() {
   init {
      test("should generate XML for successful tests") {

         val clock = FixedClock()
         val generator = JUnitXmlReportGenerator(
            clock = clock,
            includeStackTraces = true,
            hostname = "localhost",
            target = null
         )

         val testCase = createTestCase("successful test")
         val testResult = TestResult.Success(1.seconds)
         val tests = mapOf(testCase to testResult)

         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
         xml shouldContain """<testsuite name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" tests="1" failures="0" errors="0" skipped="0" timestamp="2023-01-01T12:00:00Z" hostname="localhost" time="1.0">"""
         xml shouldContain """<testcase name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest/successful test" classname="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" time="1.0" />"""
      }

      test("should generate XML for failed tests") {

         val clock = FixedClock()
         val generator = JUnitXmlReportGenerator(
            clock = clock,
            includeStackTraces = true,
            hostname = "localhost",
            target = null
         )

         val testCase = createTestCase("failed test")
         val testResult = TestResult.Failure(500.milliseconds, AssertionError("Test failed"))
         val tests = mapOf(testCase to testResult)

         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
         xml shouldContain """<testsuite name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" tests="1" failures="1" errors="0" skipped="0" timestamp="2023-01-01T12:00:00Z" hostname="localhost" time="0.5">"""
         xml shouldContain """<testcase name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest/failed test" classname="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" time="0.5">"""
         xml shouldContain """<failure message="Test failed" type="AssertionError">java.lang.AssertionError: Test failed"""
      }

      test("should generate XML for error tests") {
         val clock = FixedClock()
         val generator = JUnitXmlReportGenerator(
            clock = clock,
            includeStackTraces = true,
            hostname = "localhost",
            target = null
         )

         val testCase = createTestCase("error test")
         val testResult = TestResult.Error(300.milliseconds, RuntimeException("Test error"))
         val tests = mapOf(testCase to testResult)

         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
         xml shouldContain """<testsuite name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" tests="1" failures="0" errors="1" skipped="0" timestamp="2023-01-01T12:00:00Z" hostname="localhost" time="0.3">
  <testcase name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest/error test" classname="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" time="0.3">
    <error message="Test error" type="RuntimeException">java.lang.RuntimeException"""
      }

      test("should generate XML for ignored tests") {
         val clock = FixedClock()
         val generator = JUnitXmlReportGenerator(
            clock = clock,
            includeStackTraces = true,
            hostname = "localhost",
            target = null
         )

         val testCase = createTestCase("ignored test")
         val testResult = TestResult.Ignored("Test ignored")
         val tests = mapOf(testCase to testResult)

         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
         xml shouldContain """<testcase name="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest/ignored test" classname="com.sksamuel.kotest.engine.reports.JUnitXmlReportGeneratorTest" time="0.0">"""
         xml shouldContain """<skipped message="Test ignored" />"""
      }
//
//      test("should generate XML for mixed test results") {
//         val clock = FixedClock()
//         val generator = JUnitXmlReportGenerator(
//            clock = clock,
//            includeStackTraces = true,
//            hostname = "localhost",
//            target = null
//         )
//
//         val successTestCase = createTestCase("success test")
//         val failureTestCase = createTestCase("failure test")
//         val errorTestCase = createTestCase("error test")
//         val ignoredTestCase = createTestCase("ignored test")
//
//         val tests = mapOf(
//            successTestCase to TestResult.Success(1.seconds),
//            failureTestCase to TestResult.Failure(500.milliseconds, AssertionError("Test failed")),
//            errorTestCase to TestResult.Error(300.milliseconds, RuntimeException("Test error")),
//            ignoredTestCase to TestResult.Ignored("Test ignored")
//         )
//
//         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
//         xml shouldBe """"""
//      }
//
//      test("should include target in test names when provided") {
//         val clock = FixedClock()
//         val generator = JUnitXmlReportGenerator(
//            clock = clock,
//            includeStackTraces = true,
//            hostname = "localhost",
//            target = "jvm"
//         )
//
//         val testCase = createTestCase("test with target")
//         val testResult = TestResult.Success(1.seconds)
//         val tests = mapOf(testCase to testResult)
//
//         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
//         xml.shouldContain("""name="[jvm] test with target"""")
//      }

      test("should not include stack traces when disabled") {
         val clock = FixedClock()
         val generator = JUnitXmlReportGenerator(
            clock = clock,
            includeStackTraces = false,
            hostname = "localhost",
            target = null
         )

         val testCase = createTestCase("test without stack trace")
         val testResult = TestResult.Error(300.milliseconds, RuntimeException("Test error"))
         val tests = mapOf(testCase to testResult)

         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
         xml.shouldNotContain("java.lang.RuntimeException")
      }

      test("should include stack traces when enabled") {
         val clock = FixedClock()
         val generator = JUnitXmlReportGenerator(
            clock = clock,
            includeStackTraces = true,
            hostname = "localhost",
            target = null
         )

         val testCase = createTestCase("test with stack trace")
         val testResult = TestResult.Error(300.milliseconds, RuntimeException("Test error"))
         val tests = mapOf(testCase to testResult)

         val xml = generator.xml(JUnitXmlReportGeneratorTest::class, tests)
         xml.shouldContain("java.lang.RuntimeException")
      }
   }

   private fun createTestCase(name: String): TestCase {
      return TestCase(
         JUnitXmlReportGeneratorTest::class.toDescriptor().append(name),
         TestNameBuilder.builder(name).build(),
         this,
         {},
         SourceRef.None,
         TestType.Test
      )
   }
}
