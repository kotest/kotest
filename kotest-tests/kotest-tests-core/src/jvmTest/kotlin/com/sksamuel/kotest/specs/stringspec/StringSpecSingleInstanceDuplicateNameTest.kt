package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseConfig
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
   }
}
