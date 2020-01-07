package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.IsolationMode
import io.kotest.core.TestCaseConfig
import io.kotest.specs.StringSpec

class StringSpecSingleInstanceDuplicateNameTest : StringSpec() {

   override fun isolationMode() = IsolationMode.SingleInstance

   init {
      defaultTestCaseConfig = TestCaseConfig(invocations = 2)

      "foo" {}
      try {
         "foo" {}
         throw RuntimeException("Must fail when adding duplicate root test name")
      } catch (e: IllegalArgumentException) {
      }
      "should not count multiple invocations as the same test".config(invocations = 3) {}
   }
}
