package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.exhaustive.sampleIndexes

class SampleIndexesTest: StringSpec() {
   init {
      "should work for one element" {
         sampleIndexes(1).toList() shouldContainExactlyInAnyOrder listOf(
            listOf(),
            listOf(0),
         )
      }
       "should work for two elements" {
          sampleIndexes(2).toList() shouldContainExactlyInAnyOrder listOf(
             listOf(),
             listOf(0),
             listOf(1),
             listOf(0, 1),
          )
       }
      "should work for three elements" {
         sampleIndexes(3).toList() shouldContainExactlyInAnyOrder listOf(
            listOf(),
            listOf(0),
            listOf(1),
            listOf(2),
            listOf(0, 1),
            listOf(0, 2),
            listOf(1, 2),
            listOf(0, 1, 2),
         )
      }
      "should work for four elements" {
         sampleIndexes(4).toList() shouldContainExactlyInAnyOrder listOf(
            listOf(),
            listOf(0),
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(0, 1),
            listOf(0, 2),
            listOf(0, 3),
            listOf(1, 2),
            listOf(1, 3),
            listOf(2, 3),
            listOf(0, 1, 2),
            listOf(0, 1, 3),
            listOf(0, 2, 3),
            listOf(1, 2, 3),
            listOf(0, 1, 2, 3),
         )
      }
   }
}
