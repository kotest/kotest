package com.sksamuel.kotest.engine.spec.xmethod

import io.kotest.core.spec.style.WordSpec

class WordSpecXDisabledTest : WordSpec() {
   init {
      "using xshould" xshould {
         "disable test" {
            error("boom")
         }
      }
      "a disabled when" xwhen {
         error("boom")
      }
   }
}
