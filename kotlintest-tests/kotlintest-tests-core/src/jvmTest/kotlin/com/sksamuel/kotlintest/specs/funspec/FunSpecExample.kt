package com.sksamuel.kotlintest.specs.funspec

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.Tag
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.locale.LocaleTestListener
import io.kotlintest.extensions.locale.TimeZoneTestListener
import io.kotlintest.specs.FunSpec
import java.util.*

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
