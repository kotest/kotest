package io.kotest.framework.xml

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.test.TestResultBuilder
import io.kotest.framework.multiplatform.JUnitXmlWriter
import io.kotest.matchers.shouldBe
import java.io.IOException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class JUnitXmlWriterTest : FunSpec() {
   init {
      test("happy path") {
         val writer = JUnitXmlWriter(
            object : Clock {
               override fun now(): Instant = Instant.fromEpochSeconds(1709898983, 123456789)
            },
            includeStackTraces = false
         )

         val test = TestCase(
            name = TestNameBuilder.builder("foo").build(),
            descriptor = JUnitXmlWriterTest::class.toDescriptor().append("foo"),
            spec = this@JUnitXmlWriterTest,
            test = {},
            type = TestType.Test,
         )

         val tests = mapOf(
            test to TestResultBuilder.builder().withDuration(42.milliseconds).build(),
            test.copy(
               name = TestNameBuilder.builder("bar").build(),
               descriptor = JUnitXmlWriterTest::class.toDescriptor().append("bar")
            ) to TestResultBuilder.builder().withDuration(124.milliseconds).withError(IOException("ioe")).build(),
            test.copy(
               name = TestNameBuilder.builder("baz").build(),
               descriptor = JUnitXmlWriterTest::class.toDescriptor().append("baz")
            ) to TestResultBuilder.builder().withDuration(52.milliseconds).withError(AssertionError("ae")).build(),
            test.copy(
               name = TestNameBuilder.builder("wiz").build(),
               descriptor = JUnitXmlWriterTest::class.toDescriptor().append("wiz")
            ) to TestResultBuilder.builder().withDuration(9.milliseconds).withIgnoreReason("ignore me").build(),
         )
         writer.writeXml(
            this@JUnitXmlWriterTest,
            tests
         ) shouldBe """<testsuite name="io.kotest.framework.xml.JUnitXmlWriterTest" tests="4" failures="1" errors="1" skipped="1" timestamp="2024-03-08T11:56:23" time="0.218">
  <testcase name="[jvm] io.kotest.framework.xml.JUnitXmlWriterTest/foo" classname="io.kotest.framework.xml.JUnitXmlWriterTest" time="0.042" />
  <testcase name="[jvm] io.kotest.framework.xml.JUnitXmlWriterTest/bar" classname="io.kotest.framework.xml.JUnitXmlWriterTest" time="0.124">
    <error message="ioe" type="IOException" />
  </testcase>
  <testcase name="[jvm] io.kotest.framework.xml.JUnitXmlWriterTest/baz" classname="io.kotest.framework.xml.JUnitXmlWriterTest" time="0.052">
    <failure message="ae" type="AssertionError" />
  </testcase>
  <testcase name="[jvm] io.kotest.framework.xml.JUnitXmlWriterTest/wiz" classname="io.kotest.framework.xml.JUnitXmlWriterTest" time="0.0">
    <skipped message="ignore me" />
  </testcase>
</testsuite>"""
      }
   }
}
