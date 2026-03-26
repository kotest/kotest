package com.sksamuel.kotest.matchers.collections

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.sliceStart
import io.kotest.matchers.shouldBe

class SliceStartTest : StringSpec() {
   init {
       "find slice at start" {
           sliceStart(listOf(1, 2, 3, 4, 5), listOf(1, 2, 3)) shouldBe 0
       }
       "find slice in the middle" {
            sliceStart(listOf(1, 2, 3, 4, 5), listOf(3, 4)) shouldBe 2
       }
         "find slice at the end" {
            sliceStart(listOf(1, 2, 3, 4, 5), listOf(4, 5)) shouldBe 3
         }
      "not find absent slice" {
            sliceStart(listOf(1, 2, 3, 4, 5), listOf(4, 5, 6)) shouldBe null
         }
   }
}
