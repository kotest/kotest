package com.sksamuel.kotest.matchers.char

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.char.shouldBeInRange
import io.kotest.matchers.char.shouldNotBeInRange

class CharMatchersTest : StringSpec() {
   init {
      "should be in range" {
         'd' shouldBeInRange ('a'..'z')
         'E' shouldBeInRange ('A'..'z')
         ']' shouldBeInRange ('A'..'z')
      }

      "should not be in range" {
         'd' shouldNotBeInRange ('e' .. 'z')
         'd' shouldNotBeInRange ('a' .. 'c')
      }
   }
}
