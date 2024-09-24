package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.duplicates
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.shouldBe

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
      "fail for unique Array" {
         shouldThrowAny {
            arrayOf(1, 2, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "Array should contain duplicates"
      }

      "fail for unique List" {
         shouldThrowAny {
            listOf(1, 2, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "List should contain duplicates"
      }

      "fail for any Set" {
         shouldThrowAny {
            setOf(1, 2, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "Set should contain duplicates"

         shouldThrowAny {
            setOf(1, 1, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "Set should contain duplicates"

         shouldThrowAny {
            setOf<Int?>(null, null).shouldContainDuplicates()
         }.message shouldBe "Set should contain duplicates"
      }

      "fail for arbitrary unique Iterable" {
         shouldThrowAny {
            Game("Risk", listOf("p1", "p2", "p3")).shouldContainDuplicates()
         }.message shouldBe "Iterable should contain duplicates"
      }

      "fail for empty" {
         shouldThrowAny {
            arrayOf<Int>().shouldContainDuplicates()
         }.message shouldBe "Array should contain duplicates"

         shouldThrowAny {
            listOf<Int>().shouldContainDuplicates()
         }.message shouldBe "List should contain duplicates"

         shouldThrowAny {
            setOf<Int>().shouldContainDuplicates()
         }.message shouldBe "Set should contain duplicates"
      }

      "succeed for non unique List" {
         listOf(1, 2, 3, null, null, 3, 2).shouldContainDuplicates()
      }

      "succeed for non unique arbitrary Iterable" {
         Game("Risk", listOf("p1", "p2", "p3", "p4", "p4")).shouldContainDuplicates()
      }

      "succeed for non unique Array" {
         listOf(1, 2, 3, null, null, 3, 2).shouldContainDuplicates()
      }
   }

   "shouldNotContainDuplicates" should {
      "succeed for unique Array" {
         arrayOf(1, 2, 3, 4, null).shouldNotContainDuplicates()
      }

      "succeed for unique List" {
         listOf(1, 2, 3, 4, null).shouldNotContainDuplicates()
      }

      "succeed for arbitrary unique Iterable" {
         Game("Risk", listOf("p1", "p2", "p3", "p4")).shouldNotContainDuplicates()
      }

      "succeed for empty" {
         arrayOf<Int>().shouldNotContainDuplicates()
         listOf<Int>().shouldNotContainDuplicates()
         setOf<Int>().shouldNotContainDuplicates()
      }

      "fail for non unique Array" {
         shouldThrowAny {
            arrayOf(1, 2, 3, null, null, 3, 2).shouldNotContainDuplicates()
         }.message shouldBe "Array should not contain duplicates, but has some: [2, 3, <null>]"
      }

      "fail for non unique List" {
         shouldThrowAny {
            listOf(1, 2, 3, null, null, 3, 2).shouldNotContainDuplicates()
         }.message shouldBe "List should not contain duplicates, but has some: [2, 3, <null>]"
      }

      "fail for arbitrary non unique Iterable" {
         shouldThrowAny {
            Game("Risk", listOf("p1", "p2", "p3", "p1")).shouldNotContainDuplicates()
         }.message shouldBe "Iterable should not contain duplicates, but has some: [\"p1\"]"
      }
   }
})

private class Game(val name: String, players: Iterable<String>) : Iterable<String> by players
