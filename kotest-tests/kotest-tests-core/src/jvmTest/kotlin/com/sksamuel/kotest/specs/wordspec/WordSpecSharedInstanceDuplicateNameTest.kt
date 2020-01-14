package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseConfig

class WordSpecSharedInstanceDuplicateNameTest : WordSpec() {

   override fun defaultTestCaseConfig() =
      TestCaseConfig(invocations = 2)

   init {
      "context" should {
         "foo" {}
         try {
            "foo" {}
            throw RuntimeException("Must fail when adding duplicate root test name")
         } catch (e: IllegalStateException) {
         }
      }
   }
}
