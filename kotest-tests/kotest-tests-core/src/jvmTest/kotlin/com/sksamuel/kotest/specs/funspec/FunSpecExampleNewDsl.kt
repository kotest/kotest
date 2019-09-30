package com.sksamuel.kotest.specs.funspec

import io.kotest.AssertionMode
import io.kotest.IsolationMode
import io.kotest.Tag
import io.kotest.TestCaseOrder
import io.kotest.extensions.locale.LocaleTestListener
import io.kotest.extensions.locale.TimeZoneTestListener
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import java.util.Locale
import java.util.TimeZone

class FunSpecExampleNewDsl : FunSpec({

   val linuxTag = Tag("linux")
   val jvmTag = Tag("JVM")

   tags(linuxTag, jvmTag)

   set(IsolationMode.InstancePerLeaf)
   set(AssertionMode.Error)
   set(TestCaseOrder.Random)

   listeners(LocaleTestListener(Locale.CANADA_FRENCH), TimeZoneTestListener(TimeZone.getTimeZone("GMT")))

   beforeTest { testCase ->
      println("Starting test ${testCase.description}")
   }

   afterTest { testCase, result ->
      println("Test ${testCase.description} completed with result $result")
   }

   beforeSpec { spec ->
      println("Starting spec ${spec.description()}")
   }

   afterSpec { spec ->
      println("Completed spec ${spec.description()}")
   }

   test("this is a test") {
      1 + 1 shouldBe 2
   }

   test("this test has config").config(invocations = 1, enabled = true) {
      2 * 2 shouldBe 4
   }
})
