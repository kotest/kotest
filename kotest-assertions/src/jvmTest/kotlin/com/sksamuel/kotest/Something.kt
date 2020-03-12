package com.sksamuel.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Something : StringSpec() {
   init {
       "should compare int and double" {
          "1" shouldBe 1.0
       }
   }
}
