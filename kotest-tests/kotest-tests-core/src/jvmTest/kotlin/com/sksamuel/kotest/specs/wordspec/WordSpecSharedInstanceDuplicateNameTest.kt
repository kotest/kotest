package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec

class WordSpecSharedInstanceDuplicateNameTest : WordSpec() {

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
