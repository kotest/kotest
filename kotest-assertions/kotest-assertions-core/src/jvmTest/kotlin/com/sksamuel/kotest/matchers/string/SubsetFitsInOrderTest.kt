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
      "returns true when multiple matches per substring and an increasing sequence is found" {
         val indexesOfMatches = listOf(
            listOf(10, 15, 20,),
            listOf(8, 16, 24,),
            listOf(6, 12, 18, ),
         )
         subsetFitsInOrder(indexesOfMatches, listOf(0, 1, 2)) shouldBe true
      }
      "returns false when multiple matches per substring and an increasing sequence cannot be found" {
         val indexesOfMatches = listOf(
            listOf(10, 15, 20,),
            listOf(8, 16, 24,),
            listOf(6, 12, 16, ),
         )
         subsetFitsInOrder(indexesOfMatches, listOf(0, 1, 2)) shouldBe false
      }
      "returns false when first substring has no matches" {
         val indexesOfMatches = listOf(
            emptyList(),
            listOf(8, 16, 24,),
            listOf(6, 12, 16, ),
         )
         subsetFitsInOrder(indexesOfMatches, listOf(0, 1, 2)) shouldBe false
      }
       "returns false when substring in the middle has no matches" {
          val indexesOfMatches = listOf(
             listOf(10, 15, 20,),
             emptyList(),
             listOf(6, 12, 16, ),
          )
          subsetFitsInOrder(indexesOfMatches, listOf(0, 1, 2)) shouldBe false
       }
       "returns false when last substring has no matches" {
          val indexesOfMatches = listOf(
             listOf(10, 15, 20,),
             listOf(8, 16, 24,),
             emptyList(),
          )
          subsetFitsInOrder(indexesOfMatches, listOf(0, 1, 2)) shouldBe false
       }

   }
}
