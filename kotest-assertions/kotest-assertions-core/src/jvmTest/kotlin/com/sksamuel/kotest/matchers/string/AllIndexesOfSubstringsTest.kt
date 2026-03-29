package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.allIndexesOfSubstrings

class AllIndexesOfSubstringsTest : StringSpec() {
   init {
       "works for one substring" {
          allIndexesOfSubstrings("no pain no gain", listOf("no"), { 1 }) shouldBe listOf(listOf(0, 8))
       }
      "works for multiple substrings, all found" {
         allIndexesOfSubstrings("no pain no gain", listOf("no", "ain"), { 1 }) shouldBe listOf(
            listOf(0, 8),
            listOf(4, 12),
            )
       }
      "works for multiple substrings, some not found" {
         allIndexesOfSubstrings("no pain no gain", listOf("no", "main"), { 1 }) shouldBe listOf(
            listOf(0, 8),
            listOf(),
         )
      }
   }
}
