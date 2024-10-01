package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.duplicationReport
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe

class DuplicatesTest : WordSpec({
   "duplicates" should {
      "be empty for unique values" {
         listOf(1, 2, 3, 4, null).duplicationReport().hasDuplicates().shouldBeFalse()
      }

      "return not null duplicates" {
         listOf(1, 2, 3, 4, 3, 2).duplicationReport().duplicates
            .shouldContainExactly(mapOf(2 to listOf(1, 5), 3 to listOf(2, 4)))
      }

      "return null duplicates" {
         listOf(null, 2, 3, null, 2).duplicationReport().duplicates
            .shouldContainExactly(mapOf(null to listOf(0, 3), 2 to listOf(1, 4)))
      }

      "return null duplicates" {
         listOf(null, null).duplicationReport().duplicates.shouldContainExactly(mapOf(null to listOf(0, 1)))
      }
   }

   "shouldContainDuplicates" should {
      "fail for unique BooleanArray" {
         shouldThrowAny {
            booleanArrayOf().shouldContainDuplicates()
         }.message shouldBe "BooleanArray should contain duplicates"

         shouldThrowAny {
            booleanArrayOf(true).shouldContainDuplicates()
         }.message shouldBe "BooleanArray should contain duplicates"

         shouldThrowAny {
            booleanArrayOf(false, true).shouldContainDuplicates()
         }.message shouldBe "BooleanArray should contain duplicates"
      }

      "fail for unique ByteArray" {
         shouldThrowAny {
            byteArrayOf().shouldContainDuplicates()
         }.message shouldBe "ByteArray should contain duplicates"

         shouldThrowAny {
            byteArrayOf(1).shouldContainDuplicates()
         }.message shouldBe "ByteArray should contain duplicates"

         shouldThrowAny {
            byteArrayOf(1, 2).shouldContainDuplicates()
         }.message shouldBe "ByteArray should contain duplicates"
      }

      "fail for unique ShortArray" {
         shouldThrowAny {
            shortArrayOf().shouldContainDuplicates()
         }.message shouldBe "ShortArray should contain duplicates"

         shouldThrowAny {
            shortArrayOf(1).shouldContainDuplicates()
         }.message shouldBe "ShortArray should contain duplicates"

         shouldThrowAny {
            shortArrayOf(1, 2).shouldContainDuplicates()
         }.message shouldBe "ShortArray should contain duplicates"
      }

      "fail for unique CharArray" {
         shouldThrowAny {
            charArrayOf().shouldContainDuplicates()
         }.message shouldBe "CharArray should contain duplicates"

         shouldThrowAny {
            charArrayOf('a').shouldContainDuplicates()
         }.message shouldBe "CharArray should contain duplicates"

         shouldThrowAny {
            charArrayOf('a', 'b').shouldContainDuplicates()
         }.message shouldBe "CharArray should contain duplicates"
      }

      "fail for unique IntArray" {
         shouldThrowAny {
            intArrayOf().shouldContainDuplicates()
         }.message shouldBe "IntArray should contain duplicates"

         shouldThrowAny {
            intArrayOf(1).shouldContainDuplicates()
         }.message shouldBe "IntArray should contain duplicates"

         shouldThrowAny {
            intArrayOf(1, 2).shouldContainDuplicates()
         }.message shouldBe "IntArray should contain duplicates"
      }

      "fail for unique LongArray" {
         shouldThrowAny {
            longArrayOf().shouldContainDuplicates()
         }.message shouldBe "LongArray should contain duplicates"

         shouldThrowAny {
            longArrayOf(1).shouldContainDuplicates()
         }.message shouldBe "LongArray should contain duplicates"

         shouldThrowAny {
            longArrayOf(1, 2).shouldContainDuplicates()
         }.message shouldBe "LongArray should contain duplicates"
      }

      "fail for unique FloatArray" {
         shouldThrowAny {
            floatArrayOf().shouldContainDuplicates()
         }.message shouldBe "FloatArray should contain duplicates"

         shouldThrowAny {
            floatArrayOf(1.00001f).shouldContainDuplicates()
         }.message shouldBe "FloatArray should contain duplicates"

         shouldThrowAny {
            floatArrayOf(1.00001f, 1.000001f).shouldContainDuplicates()
         }.message shouldBe "FloatArray should contain duplicates"
      }

      "fail for unique DoubleArray" {
         shouldThrowAny {
            doubleArrayOf().shouldContainDuplicates()
         }.message shouldBe "DoubleArray should contain duplicates"

         shouldThrowAny {
            doubleArrayOf(1.0000001).shouldContainDuplicates()
         }.message shouldBe "DoubleArray should contain duplicates"

         shouldThrowAny {
            doubleArrayOf(1.0000001, 1.00000001).shouldContainDuplicates()
         }.message shouldBe "DoubleArray should contain duplicates"
      }

      "fail for unique Array" {
         shouldThrowAny {
            arrayOf<Int>().shouldContainDuplicates()
         }.message shouldBe "Array should contain duplicates"

         shouldThrowAny {
            arrayOf(1).shouldContainDuplicates()
         }.message shouldBe "Array should contain duplicates"

         shouldThrowAny {
            arrayOf<Int?>(null).shouldContainDuplicates()
         }.message shouldBe "Array should contain duplicates"

         shouldThrowAny {
            arrayOf(1, 2, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "Array should contain duplicates"
      }

      "fail for unique List" {
         shouldThrowAny {
            listOf<Int>().shouldContainDuplicates()
         }.message shouldBe "List should contain duplicates"

         shouldThrowAny {
            listOf(1).shouldContainDuplicates()
         }.message shouldBe "List should contain duplicates"

         shouldThrowAny {
            listOf<Int?>(null).shouldContainDuplicates()
         }.message shouldBe "List should contain duplicates"

         shouldThrowAny {
            listOf(1, 2, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "List should contain duplicates"
      }

      "fail for any Set" {
         shouldThrowAny {
            setOf<Int>().shouldContainDuplicates()
         }.message shouldBe "Set should contain duplicates"

         shouldThrowAny {
            setOf(1, 2, 3, 4, null).shouldContainDuplicates()
         }.message shouldBe "Set should contain duplicates"

         shouldThrowAny {
            setOf(8, 9, 1, 1, null).shouldContainDuplicates()
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

      "succeed for non unique BooleanArray" {
         booleanArrayOf(true, false, true).shouldContainDuplicates()
      }

      "succeed for non unique ByteArray" {
         byteArrayOf(1, 1).shouldContainDuplicates()
      }

      "succeed for non unique ShortArray" {
         shortArrayOf(1, 1).shouldContainDuplicates()
      }

      "succeed for non unique CharArray" {
         charArrayOf('a', 'a').shouldContainDuplicates()
      }

      "succeed for non unique IntArray" {
         intArrayOf(1, 1).shouldContainDuplicates()
      }

      "succeed for non unique LongArray" {
         longArrayOf(1, 1).shouldContainDuplicates()
      }

      "succeed for non unique FloatArray" {
         floatArrayOf(1.0000001f, 1.0000001f).shouldContainDuplicates()
      }

      "succeed for non unique DoubleArray" {
         doubleArrayOf(1.000000000000001, 1.000000000000001).shouldContainDuplicates()
      }

      "succeed for non unique Array" {
         arrayOf<Int?>(null, null).shouldContainDuplicates()
         arrayOf(1, 2, 3, null, null, 3, 2).shouldContainDuplicates()
      }

      "succeed for non unique List" {
         listOf<Int?>(null, null).shouldContainDuplicates()
         listOf(1, 2, 3, null, null, 3, 2).shouldContainDuplicates()
      }

      "succeed for non unique arbitrary Iterable" {
         Game("Risk", listOf("p1", "p2", "p3", "p4", "p4")).shouldContainDuplicates()
      }

      "succeed for misbehaving Set" {
         (NonUniqueSet() as Set<Int>).shouldContainDuplicates()
      }
   }

   "shouldNotContainDuplicates" should {
      "succeed for unique BooleanArray" {
         booleanArrayOf().shouldNotContainDuplicates()
         booleanArrayOf(true).shouldNotContainDuplicates()
         booleanArrayOf(true, false).shouldNotContainDuplicates()
      }

      "succeed for unique ByteArray" {
         byteArrayOf().shouldNotContainDuplicates()
         byteArrayOf(1).shouldNotContainDuplicates()
         byteArrayOf(1, 2).shouldNotContainDuplicates()
      }

      "succeed for unique ShortArray" {
         shortArrayOf().shouldNotContainDuplicates()
         shortArrayOf(1).shouldNotContainDuplicates()
         shortArrayOf(1, 2).shouldNotContainDuplicates()
      }

      "succeed for unique CharArray" {
         charArrayOf().shouldNotContainDuplicates()
         charArrayOf('1').shouldNotContainDuplicates()
         charArrayOf('1', '2').shouldNotContainDuplicates()
      }

      "succeed for unique IntArray" {
         intArrayOf().shouldNotContainDuplicates()
         intArrayOf(1).shouldNotContainDuplicates()
         intArrayOf(1, 2).shouldNotContainDuplicates()
      }

      "succeed for unique LongArray" {
         longArrayOf().shouldNotContainDuplicates()
         longArrayOf(1).shouldNotContainDuplicates()
         longArrayOf(1, 2).shouldNotContainDuplicates()
      }

      "succeed for unique FloatArray" {
         floatArrayOf().shouldNotContainDuplicates()
         floatArrayOf(1f).shouldNotContainDuplicates()
         floatArrayOf(1f, 2f).shouldNotContainDuplicates()
      }

      "succeed for unique DoubleArray" {
         doubleArrayOf().shouldNotContainDuplicates()
         doubleArrayOf(0.0).shouldNotContainDuplicates()
         doubleArrayOf(0.1, 0.2).shouldNotContainDuplicates()
      }

      "succeed for unique Array" {
         arrayOf<Int>().shouldNotContainDuplicates()
         arrayOf<Int?>(null).shouldNotContainDuplicates()
         arrayOf(1, 2, null).shouldNotContainDuplicates()
      }

      "succeed for unique List" {
         listOf<Int>().shouldNotContainDuplicates()
         listOf<Int?>(null).shouldNotContainDuplicates()
         listOf(1, 2, null).shouldNotContainDuplicates()
      }

      "succeed for any Set" {
         setOf<Int>().shouldNotContainDuplicates()
         setOf<Int?>(null).shouldNotContainDuplicates()
         setOf(1, 2, null).shouldNotContainDuplicates()
         setOf(1, 1, 1, 1).shouldNotContainDuplicates()
      }

      "succeed for arbitrary unique Iterable" {
         Game("Risk", listOf("p1", "p2", "p3", "p4")).shouldNotContainDuplicates()
      }

      "fail for non unique BooleanArray" {
         shouldThrowAny {
            booleanArrayOf(true, false, true, false).shouldNotContainDuplicates()
         }.message shouldBe "BooleanArray should not contain duplicates, but has:\ntrue at indexes: [0, 2]\nfalse at indexes: [1, 3]"
      }

      "fail for non unique ByteArray" {
         shouldThrowAny {
            byteArrayOf(1, 1).shouldNotContainDuplicates()
         }.message shouldBe "ByteArray should not contain duplicates, but has:\n1 at indexes: [0, 1]"
      }

      "fail for non unique ShortArray" {
         shouldThrowAny {
            shortArrayOf(1, 1).shouldNotContainDuplicates()
         }.message shouldBe "ShortArray should not contain duplicates, but has:\n1 at indexes: [0, 1]"
      }

      "fail for non unique CharArray" {
         shouldThrowAny {
            charArrayOf('1', '1').shouldNotContainDuplicates()
         }.message shouldBe "CharArray should not contain duplicates, but has:\n'1' at indexes: [0, 1]"
      }

      "fail for non unique IntArray" {
         shouldThrowAny {
            intArrayOf(1, 1).shouldNotContainDuplicates()
         }.message shouldBe "IntArray should not contain duplicates, but has:\n1 at indexes: [0, 1]"
      }

      "fail for non unique LongArray" {
         shouldThrowAny {
            longArrayOf(1, 1).shouldNotContainDuplicates()
         }.message shouldBe "LongArray should not contain duplicates, but has:\n1L at indexes: [0, 1]"
      }

      "fail for non unique FloatArray" {
         shouldThrowAny {
            floatArrayOf(0.1f, 0.1f).shouldNotContainDuplicates()
         }.message shouldBe "FloatArray should not contain duplicates, but has:\n0.1f at indexes: [0, 1]"
      }

      "fail for non unique DoubleArray" {
         shouldThrowAny {
            doubleArrayOf(
               0.000000000000000000000000000000000000001,
               0.000000000000000000000000000000000000001
            ).shouldNotContainDuplicates()
         }.message shouldBe "DoubleArray should not contain duplicates, but has:\n1.0E-39 at indexes: [0, 1]"

         shouldThrowAny {
            doubleArrayOf(1234.056789, 1234.056789).shouldNotContainDuplicates()
         }.message shouldBe "DoubleArray should not contain duplicates, but has:\n1234.056789 at indexes: [0, 1]"
      }

      "fail for non unique Array" {
         shouldThrowAny {
            arrayOf(1, 2, 3, null, null, 3, 2).shouldNotContainDuplicates()
         }.message shouldBe "Array should not contain duplicates, but has:\n2 at indexes: [1, 6]\n3 at indexes: [2, 5]\n<null> at indexes: [3, 4]"
      }

      "fail for non unique List" {
         shouldThrowAny {
            listOf(1, 2, 3, null, null, 3, 2).shouldNotContainDuplicates()
         }.message shouldBe "List should not contain duplicates, but has:\n2 at indexes: [1, 6]\n3 at indexes: [2, 5]\n<null> at indexes: [3, 4]"
      }

      "fail for arbitrary non unique Iterable" {
         shouldThrowAny {
            Game("Risk", listOf("p1", "p2", "p3", "p1")).shouldNotContainDuplicates()
         }.message shouldBe "Iterable should not contain duplicates, but has:\n\"p1\" at indexes: [0, 3]"
      }

      "fail for misbehaving set" {
         shouldThrowAny {
            (NonUniqueSet() as Set<Int>).shouldNotContainDuplicates()
         }.message shouldBe "Set should not contain duplicates, but has:\n1 at indexes: [0, 1]"
      }
   }
})

private class Game(val name: String, players: Iterable<String>) : Iterable<String> by players

private class NonUniqueSet : Set<Int> {
   private val elements = listOf(1, 1)

   override val size: Int = elements.size

   override fun contains(element: Int): Boolean = elements.contains(element)

   override fun containsAll(elements: Collection<Int>): Boolean = elements.containsAll(elements)

   override fun isEmpty(): Boolean = false

   override fun iterator(): Iterator<Int> = elements.iterator()
}
