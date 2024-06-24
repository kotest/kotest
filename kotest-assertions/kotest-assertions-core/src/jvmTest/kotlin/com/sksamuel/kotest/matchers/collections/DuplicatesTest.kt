package com.sksamuel.kotest.matchers.collections

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.duplicates
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.shouldBe

class DuplicatesTest : StringSpec() {
   init {
      "return empty list" {
         listOf(1, 2, 3, 4, null).duplicates().shouldBeEmpty()
      }

      "return not null duplicates" {
         listOf(1, 2, 3, 4, 3, 2).duplicates() shouldContainExactlyInAnyOrder listOf(2, 3)
      }

      "return null duplicates" {
         listOf(1, 2, 3, null, 4, 3, null, 2).duplicates() shouldContainExactlyInAnyOrder listOf(2, 3, null)
      }
      "array should contain duplicates" {
         arrayOf(0, 1, 1, 2, 3).shouldContainDuplicates()
      }
      "arbitrary iterable should not contain duplicates" {
         class Game(val name: String, players: Iterable<String>) : Iterable<String> by players

         val game = Game("Risk", listOf("p1", "p2", "p3", "p4")).shouldNotContainDuplicates()
         game.name shouldBe "Risk"
      }
   }
}
