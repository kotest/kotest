package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.substringFoundAtIndexes

class SubstringFoundAtIndexesTest : StringSpec() {
   init {
      "should find substring at correct indexes" {
         substringFoundAtIndexes("Mayday", "ay") shouldContainExactly listOf(1, 4)
         substringFoundAtIndexes("121212","1212") shouldContainExactly listOf(0, 2)
      }
       "should return empty list if substring is not found" {
          substringFoundAtIndexes("Mayday","xyz").shouldBeEmpty()
       }
   }
}
