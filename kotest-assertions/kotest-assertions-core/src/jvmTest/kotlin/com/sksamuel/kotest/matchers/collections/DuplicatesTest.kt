package com.sksamuel.kotest.matchers.collections

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.duplicates
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class DuplicatesTest : StringSpec(){
   init {
       "return empty list" {
          listOf(1, 2, 3, 4).duplicates().shouldBeEmpty()
       }
      "return duplicates" {
         listOf(1, 2, 3, 4, 3, 2).duplicates() shouldContainExactlyInAnyOrder listOf(2, 3)
      }
   }
}
