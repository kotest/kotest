package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldNotBeUnique
import io.kotest.matchers.shouldBe

class UniqueTest : WordSpec({

   "shouldBeUnique" should {
      "succeed for unique BooleanArray" {
         booleanArrayOf().shouldBeUnique()
         booleanArrayOf(true).shouldBeUnique()
         booleanArrayOf(true, false).shouldBeUnique()
      }

      "succeed for unique ByteArray" {
         byteArrayOf().shouldBeUnique()
         byteArrayOf(1).shouldBeUnique()
         byteArrayOf(1, 2).shouldBeUnique()
      }

      "succeed for unique ShortArray" {
         shortArrayOf().shouldBeUnique()
         shortArrayOf(1).shouldBeUnique()
         shortArrayOf(1, 2).shouldBeUnique()
      }

      "succeed for unique CharArray" {
         charArrayOf().shouldBeUnique()
         charArrayOf('1').shouldBeUnique()
         charArrayOf('1', '2').shouldBeUnique()
      }

      "succeed for unique IntArray" {
         intArrayOf().shouldBeUnique()
         intArrayOf(1).shouldBeUnique()
         intArrayOf(1, 2).shouldBeUnique()
      }

      "succeed for unique LongArray" {
         longArrayOf().shouldBeUnique()
         longArrayOf(1).shouldBeUnique()
         longArrayOf(1, 2).shouldBeUnique()
      }

      "succeed for unique FloatArray" {
         floatArrayOf().shouldBeUnique()
         floatArrayOf(1f).shouldBeUnique()
         floatArrayOf(1f, 2f).shouldBeUnique()
      }

      "succeed for unique DoubleArray" {
         doubleArrayOf().shouldBeUnique()
         doubleArrayOf(0.0).shouldBeUnique()
         doubleArrayOf(0.1, 0.2).shouldBeUnique()
      }

      "succeed for unique Array" {
         arrayOf<Int>().shouldBeUnique()
         arrayOf(1).shouldBeUnique()
         arrayOf<Int?>(null).shouldBeUnique()
         arrayOf(1, 2, null).shouldBeUnique()
         arrayOf(1, 2, 3, 4).shouldBeUnique()
      }
      "succeed for unique List" {
         listOf<Int>().shouldBeUnique()
         listOf(1).shouldBeUnique()
         listOf<Int?>(null).shouldBeUnique()
         listOf(1, 2, null).shouldBeUnique()
         listOf(1, 2, 3, 4).shouldBeUnique()
      }

      fun msg(name: String) = "$name should be unique, but has:\n1 at indexes: [0, 2]\n2 at indexes: [1, 3]"

      "fail for non unique BooleanArray" {
         shouldThrowAny {
            booleanArrayOf(true, false, true, false).shouldBeUnique()
         }.message shouldBe "BooleanArray should be unique, but has:\ntrue at indexes: [0, 2]\nfalse at indexes: [1, 3]"
      }

      "fail for non unique ByteArray" {
         shouldThrowAny { byteArrayOf(1, 2, 1, 2, 3).shouldBeUnique() }.message shouldBe msg("ByteArray")
      }

      "fail for non unique ShortArray" {
         shouldThrowAny { shortArrayOf(1, 2, 1, 2, 3).shouldBeUnique() }.message shouldBe msg("ShortArray")
      }

      "fail for non unique CharArray" {
         shouldThrowAny {
            charArrayOf('1', '1').shouldBeUnique()
         }.message shouldBe "CharArray should be unique, but has:\n'1' at indexes: [0, 1]"
      }

      "fail for non unique IntArray" {
         shouldThrowAny { intArrayOf(1, 2, 1, 2, 3).shouldBeUnique() }.message shouldBe msg("IntArray")
      }

      "fail for non unique LongArray" {
         shouldThrowAny {
            longArrayOf(1, 2, 1, 2, 3).shouldBeUnique()
         }.message shouldBe "LongArray should be unique, but has:\n1L at indexes: [0, 2]\n2L at indexes: [1, 3]"
      }

      "fail for non unique FloatArray" {
         shouldThrowAny {
            floatArrayOf(0.1f, 0.1f).shouldBeUnique()
         }.message shouldBe "FloatArray should be unique, but has:\n0.1f at indexes: [0, 1]"
      }

      "fail for non unique DoubleArray" {
         shouldThrowAny {
            doubleArrayOf(
               0.000000000000000000000000000000000000001,
               0.000000000000000000000000000000000000001
            ).shouldBeUnique()
         }.message shouldBe "DoubleArray should be unique, but has:\n1.0E-39 at indexes: [0, 1]"

         shouldThrowAny {
            doubleArrayOf(1234.056789, 1234.056789).shouldBeUnique()
         }.message shouldBe "DoubleArray should be unique, but has:\n1234.056789 at indexes: [0, 1]"
      }

      fun nullMsg(name: String) =
         "$name should be unique, but has:\n2 at indexes: [1, 6]\n3 at indexes: [2, 5]\n<null> at indexes: [3, 4]"

      "fail for non unique Array" {
         shouldThrowAny { arrayOf(1, 2, 3, null, null, 3, 2).shouldBeUnique() }.message shouldBe nullMsg("Array")
      }

      "fail for non unique List" {
         shouldThrowAny { listOf(1, 2, 3, null, null, 3, 2).shouldBeUnique() }.message shouldBe nullMsg("List")
      }
   }

   "shouldNotBeUnique" should {
      fun msg(name: String) = "$name should contain duplicates, but all elements are unique"

      "fail for unique BooleanArray" {
         shouldThrowAny { booleanArrayOf().shouldNotBeUnique() }.message shouldBe msg("BooleanArray")
         shouldThrowAny { booleanArrayOf(true).shouldNotBeUnique() }.message shouldBe msg("BooleanArray")
         shouldThrowAny { booleanArrayOf(false, true).shouldNotBeUnique() }.message shouldBe msg("BooleanArray")
      }

      "fail for unique ByteArray" {
         shouldThrowAny { byteArrayOf().shouldNotBeUnique() }.message shouldBe msg("ByteArray")
         shouldThrowAny { byteArrayOf(1).shouldNotBeUnique() }.message shouldBe msg("ByteArray")
         shouldThrowAny { byteArrayOf(1, 2).shouldNotBeUnique() }.message shouldBe msg("ByteArray")
      }

      "fail for unique ShortArray" {
         shouldThrowAny { shortArrayOf().shouldNotBeUnique() }.message shouldBe msg("ShortArray")
         shouldThrowAny { shortArrayOf(1).shouldNotBeUnique() }.message shouldBe msg("ShortArray")
         shouldThrowAny { shortArrayOf(1, 2).shouldNotBeUnique() }.message shouldBe msg("ShortArray")
      }

      "fail for unique CharArray" {
         shouldThrowAny { charArrayOf().shouldNotBeUnique() }.message shouldBe msg("CharArray")
         shouldThrowAny { charArrayOf('a').shouldNotBeUnique() }.message shouldBe msg("CharArray")
         shouldThrowAny { charArrayOf('a', 'b').shouldNotBeUnique() }.message shouldBe msg("CharArray")
      }

      "fail for unique IntArray" {
         shouldThrowAny { intArrayOf().shouldNotBeUnique() }.message shouldBe msg("IntArray")
         shouldThrowAny { intArrayOf(1).shouldNotBeUnique() }.message shouldBe msg("IntArray")
         shouldThrowAny { intArrayOf(1, 2).shouldNotBeUnique() }.message shouldBe msg("IntArray")
      }

      "fail for unique LongArray" {
         shouldThrowAny { longArrayOf().shouldNotBeUnique() }.message shouldBe msg("LongArray")
         shouldThrowAny { longArrayOf(1).shouldNotBeUnique() }.message shouldBe msg("LongArray")
         shouldThrowAny { longArrayOf(1, 2).shouldNotBeUnique() }.message shouldBe msg("LongArray")
      }

      "fail for unique FloatArray" {
         shouldThrowAny { floatArrayOf().shouldNotBeUnique() }.message shouldBe msg("FloatArray")
         shouldThrowAny { floatArrayOf(1.00001f).shouldNotBeUnique() }.message shouldBe msg("FloatArray")
         shouldThrowAny { floatArrayOf(1.00001f, 1.000001f).shouldNotBeUnique() }.message shouldBe msg("FloatArray")
      }

      "fail for unique DoubleArray" {
         shouldThrowAny { doubleArrayOf().shouldNotBeUnique() }.message shouldBe msg("DoubleArray")
         shouldThrowAny { doubleArrayOf(1.0000001).shouldNotBeUnique() }.message shouldBe msg("DoubleArray")
         shouldThrowAny { doubleArrayOf(1.0000001, 1.00000001).shouldNotBeUnique() }.message shouldBe msg("DoubleArray")
      }

      "fail for unique Array" {
         shouldThrowAny { arrayOf<Int>().shouldNotBeUnique() }.message shouldBe msg("Array")
         shouldThrowAny { arrayOf(1).shouldNotBeUnique() }.message shouldBe msg("Array")
         shouldThrowAny { arrayOf<Int?>(null).shouldNotBeUnique() }.message shouldBe msg("Array")
         shouldThrowAny { arrayOf(1, 2, 3, 4, null).shouldNotBeUnique() }.message shouldBe msg("Array")
      }

      "fail for unique List" {
         shouldThrowAny { listOf<Int>().shouldNotBeUnique() }.message shouldBe msg("List")
         shouldThrowAny { listOf(1).shouldNotBeUnique() }.message shouldBe msg("List")
         shouldThrowAny { listOf<Int?>(null).shouldNotBeUnique() }.message shouldBe msg("List")
         shouldThrowAny { listOf(1, 2, 3, 4, null).shouldNotBeUnique() }.message shouldBe msg("List")
      }

      "fail for any Set" {
         shouldThrowAny { setOf<Int>().shouldNotBeUnique() }.message shouldBe msg("Set")
         shouldThrowAny { setOf(1, 2, 3, 4, null).shouldNotBeUnique() }.message shouldBe msg("Set")
         shouldThrowAny { setOf(8, 9, 1, 1, null).shouldNotBeUnique() }.message shouldBe msg("Set")
         shouldThrowAny { setOf<Int?>(null, null).shouldNotBeUnique() }.message shouldBe msg("Set")
      }

      "succeed for non unique BooleanArray" {
         booleanArrayOf(true, false, true).shouldNotBeUnique()
      }

      "succeed for non unique ByteArray" {
         byteArrayOf(1, 1).shouldNotBeUnique()
      }

      "succeed for non unique ShortArray" {
         shortArrayOf(1, 1).shouldNotBeUnique()
      }

      "succeed for non unique CharArray" {
         charArrayOf('a', 'a').shouldNotBeUnique()
      }

      "succeed for non unique IntArray" {
         intArrayOf(1, 1).shouldNotBeUnique()
      }

      "succeed for non unique LongArray" {
         longArrayOf(1, 1).shouldNotBeUnique()
      }

      "succeed for non unique FloatArray" {
         floatArrayOf(1.0000001f, 1.0000001f).shouldNotBeUnique()
      }

      "succeed for non unique DoubleArray" {
         doubleArrayOf(1.000000000000001, 1.000000000000001).shouldNotBeUnique()
      }

      "succeed for non unique Array" {
         arrayOf<Int?>(null, null).shouldNotBeUnique()
         arrayOf(1, 2, 3, null, null, 3, 2).shouldNotBeUnique()
      }

      "succeed for non unique List" {
         listOf<Int?>(null, null).shouldNotBeUnique()
         listOf(1, 2, 3, null, null, 3, 2).shouldNotBeUnique()
      }

      "succeed for misbehaving Set" {
         NonUniqueSet().shouldNotBeUnique()
      }
   }

   "shouldBeUnique using COMPARATOR" should {
      "succeed for unique Array" {
         listOf(1, 1, 1).shouldBeUnique(Comparator { _, _ -> 1 })
      }
      "succeed for unique List" {
         listOf(1, 1, 1).shouldBeUnique(Comparator { _, _ -> 1 })
      }

      "fail for non unique Array" {
         shouldThrowWithMessage<AssertionError>("Array should be unique by comparison, but has:\n1 at indexes: [0, 1]") {
            arrayOf(1, 1, 2).shouldBeUnique(compareBy { it })
         }
         shouldThrowWithMessage<AssertionError>("Array should be unique by comparison, but has:\n<null> at indexes: [0, 1]") {
            arrayOf<Int?>(null, null).shouldBeUnique(compareBy { it })
         }
      }
      "fail for non unique List" {
         shouldThrowWithMessage<AssertionError>("List should be unique by comparison, but has:\n1 at indexes: [0, 1]") {
            listOf(1, 1, 2).shouldBeUnique(compareBy { it })
         }
         shouldThrowWithMessage<AssertionError>("List should be unique by comparison, but has:\n<null> at indexes: [0, 1]") {
            listOf<Int?>(null, null).shouldBeUnique(compareBy { it })
         }
      }
   }
})
