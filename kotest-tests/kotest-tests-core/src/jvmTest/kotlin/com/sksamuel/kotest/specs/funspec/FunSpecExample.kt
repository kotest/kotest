package com.sksamuel.kotest.specs.funspec

import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.Tag
import io.kotest.TestCase
import io.kotest.TestCaseOrder
import io.kotest.TestResult
import io.kotest.extensions.TestListener
import io.kotest.extensions.locale.LocaleTestListener
import io.kotest.extensions.locale.TimeZoneTestListener
import io.kotest.specs.FunSpec
import java.util.Locale
import java.util.TimeZone

class FunSpecExample : FunSpec() {

   private val linuxTag = Tag("linux")
   private val jvmTag = Tag("JVM")

   override fun tags(): Set<Tag> = setOf(jvmTag, linuxTag)

   override fun beforeTest(testCase: TestCase) {
      println("Starting test ${testCase.description}")
   }

   override fun beforeSpec(spec: Spec) {
      println("Starting spec ${spec.description()}")
   }

   override fun afterSpec(spec: Spec) {
      println("Completed spec ${spec.description()}")
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
      test("this test has config").config(invocations = 1, enabled = true) {
         // test here
      }
   }
}
