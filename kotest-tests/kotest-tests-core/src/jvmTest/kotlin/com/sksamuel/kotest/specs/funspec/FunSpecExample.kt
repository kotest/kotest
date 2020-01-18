package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.Tag
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.core.extensions.TestListener
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.description
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.locale.LocaleTestListener
import io.kotest.extensions.locale.TimeZoneTestListener
import java.util.Locale
import java.util.TimeZone
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class FunSpecExample : FunSpec() {

   private val linuxTag = Tag("linux")
   private val jvmTag = Tag("JVM")

   override fun tags(): Set<Tag> = setOf(jvmTag, linuxTag)

   override fun beforeTest(testCase: TestCase) {
      println("Starting test ${testCase.description}")
   }

   override fun beforeSpec(spec: SpecConfiguration) {
      println("Starting spec ${spec::class.description()}")
   }

   override fun afterSpec(spec: SpecConfiguration) {
      println("Completed spec ${spec::class.description()}")
   }

   override fun afterTest(testCase: TestCase, result: TestResult) {
      println("Test ${testCase.description} completed with result $result")
   }

   override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerLeaf

   override fun listeners(): List<TestListener> =
      listOf(LocaleTestListener(Locale.CANADA_FRENCH), TimeZoneTestListener(TimeZone.getTimeZone("GMT")))

   override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Random

   init {
      test("this is a test") {
         // test here
      }
      test("this test has config").config(timeout = 412.milliseconds, enabled = true) {
         // test here
      }
   }
}
