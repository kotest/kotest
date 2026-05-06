package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.removeOverlapsInIndexes

class RemoveOverlapsInIndexesTest : StringSpec() {
   init {
       "works for empty list" {
          removeOverlapsInIndexes(
             listOf(), overlapLength = 2
          ).shouldBeEmpty()
       }
      "works for list of one element" {
         removeOverlapsInIndexes(
            listOf(1), overlapLength = 2
         ) shouldContainExactly listOf(1)
      }
      "keeps all elements when they are far enough apart" {
         removeOverlapsInIndexes(
            listOf(1, 3, 5), overlapLength = 2
         ) shouldContainExactly listOf(1, 3, 5)
      }
      "removes overlap in the middle" {
         removeOverlapsInIndexes(
            listOf(1, 2, 5), overlapLength = 2
         ) shouldContainExactly listOf(1, 5)
      }
      "removes overlap in the end" {
         removeOverlapsInIndexes(
            listOf(1, 4, 5), overlapLength = 2
         ) shouldContainExactly listOf(1, 4)
      }
   }
}
