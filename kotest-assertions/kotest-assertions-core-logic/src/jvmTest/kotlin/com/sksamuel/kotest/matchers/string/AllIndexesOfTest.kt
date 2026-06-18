package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.allIndexesOf

class AllIndexesOfTest : StringSpec() {
   init {
       "find two occurrences, first at the start" {
          allIndexesOf("no pain no gain", "no") shouldBe listOf(0, 8)
       }
      "find two occurrences, both in the middle" {
         allIndexesOf("no pain no gain!", "ai") shouldBe listOf(4, 12)
      }
      "find two occurrences, second at the end" {
         allIndexesOf("no pain no gain", "ain") shouldBe listOf(4, 12)
      }
      "find none" {
         allIndexesOf("no pain no gain", "bad") shouldBe emptyList()
      }
   }
}
