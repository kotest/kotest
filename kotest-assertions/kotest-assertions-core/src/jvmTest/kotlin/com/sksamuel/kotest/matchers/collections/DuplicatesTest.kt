package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containDuplicates
import io.kotest.matchers.collections.duplicates
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

class DuplicatesTest : WordSpec({
   "duplicates" should {
      "be empty for unique values" {
         listOf(1, 2, 3, 4, null).duplicates().shouldBeEmpty()
      }

      "return not null duplicates" {
         listOf(1, 2, 3, 4, 3, 2).duplicates() shouldContainExactlyInAnyOrder listOf(2, 3)
      }

      "return null duplicates" {
         listOf(1, 2, 3, null, 4, 3, null, 2).duplicates() shouldContainExactlyInAnyOrder listOf(2, 3, null)
      }

      "return null duplicates" {
         listOf<Unit?>(null, null).duplicates().shouldContainExactly(null)
      }
   }

   "shouldContainDuplicates" should {
      "succeed for unique List" {
         listOf(1, 2, 3, 4, null).shouldNotContainDuplicates()
         listOf(1, 2, 3, 4, null) shouldNot containDuplicates()
      }

      "succeed for arbitrary iterable" {
         Game("Risk", listOf("p1", "p2", "p3", "p4")).shouldNotContainDuplicates()
      }

      "fail for non unique list" {
         shouldThrowAny {
            listOf(1, 2, 3, null, null, 3, 2).shouldNotContainDuplicates()
         }.message shouldBe "List should not contain duplicates, but has some: [2, 3, <null>]"
      }

      "fail for arbitrary iterable" {
         shouldThrowAny {
            Game("Risk", listOf("p1", "p2", "p3", "p1")).shouldNotContainDuplicates()
         }.message shouldBe "List should not contain duplicates, but has some: [\"p1\"]"
      }
   }

})

private class Game(val name: String, players: Iterable<String>) : Iterable<String> by players
