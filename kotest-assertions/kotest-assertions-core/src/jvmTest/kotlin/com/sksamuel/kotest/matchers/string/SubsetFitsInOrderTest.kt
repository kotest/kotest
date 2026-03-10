package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.subsetFitsInOrder

class SubsetFitsInOrderTest : StringSpec() {
   init {
       "returns true in the simplest case of one match per substring" {
            val indexesOfMatches = listOf(
               listOf(0),
               listOf(1),
               listOf(2),
            )
            subsetFitsInOrder(indexesOfMatches, listOf(0, 1, 2)) shouldBe true
       }

   }
}
