package com.sksamuel.kotlintest.specs.funspec

import io.kotlintest.AssertionMode
import io.kotlintest.IsolationMode
import io.kotlintest.Tag
import io.kotlintest.TestCaseOrder
import io.kotlintest.extensions.locale.LocaleTestListener
import io.kotlintest.extensions.locale.TimeZoneTestListener
import io.kotlintest.specs.FunSpec
import java.util.*

class FunSpecExampleNewDsl : FunSpec({

   val linuxTag = Tag("linux")
   val jvmTag = Tag("JVM")

   tags(linuxTag, jvmTag)

   set(IsolationMode.InstancePerLeaf)
   set(AssertionMode.Warn)
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
      // test here
   }

   test("this test has config").config(invocations = 1, enabled = true) {
      // test here
   }
})
