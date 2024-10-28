package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ranges.Range
import io.kotest.matchers.shouldBe

class BeEmptyTest : WordSpec() {
   init {
      "shouldBeEmpty" should {
         "succeed for empty boolean array" {
            booleanArrayOf().shouldBeEmpty()
         }

         "succeed for empty byte array" {
            byteArrayOf().shouldBeEmpty()
         }

         "succeed for empty short array" {
            shortArrayOf().shouldBeEmpty()
         }

         "succeed for empty char array" {
            charArrayOf().shouldBeEmpty()
         }

         "succeed for empty int array" {
            intArrayOf().shouldBeEmpty()
         }

         "succeed for empty long array" {
            longArrayOf().shouldBeEmpty()
         }

         "succeed for empty float array" {
            floatArrayOf().shouldBeEmpty()
         }

         "succeed for empty double array" {
            doubleArrayOf().shouldBeEmpty()
         }

         "succeed for empty list" {
            listOf<Int>().shouldBeEmpty()
         }

         "succeed for empty set" {
            setOf<Int>().shouldBeEmpty()
         }

         "succeed for empty array" {
            arrayOf<Int>().shouldBeEmpty()
         }

         "succeed for empty range" {
            (1..0).shouldBeEmpty()
         }

         "succeed for empty open range" {
            (1 until 1).shouldBeEmpty()
         }

         "fail for not empty boolean array" {
            shouldThrowAny {
               booleanArrayOf(true).shouldBeEmpty()
            }.message shouldBe "BooleanArray should be empty but has 1 elements, first being: true"
         }

         "fail for not empty byte array" {
            shouldThrowAny {
               byteArrayOf(1).shouldBeEmpty()
            }.message shouldBe "ByteArray should be empty but has 1 elements, first being: 1"
         }

         "fail for not empty short array" {
            shouldThrowAny {
               shortArrayOf(1, 2).shouldBeEmpty()
            }.message shouldBe "ShortArray should be empty but has 2 elements, first being: 1"
         }

         "fail for not empty char array" {
            shouldThrowAny {
               charArrayOf('a').shouldBeEmpty()
            }.message shouldBe "CharArray should be empty but has 1 elements, first being: 'a'"
         }

         "fail for not empty int array" {
            shouldThrowAny {
               intArrayOf(1).shouldBeEmpty()
            }.message shouldBe "IntArray should be empty but has 1 elements, first being: 1"
         }

         "fail for not empty long array" {
            shouldThrowAny {
               longArrayOf(1).shouldBeEmpty()
            }.message shouldBe "LongArray should be empty but has 1 elements, first being: 1L"
         }

         "fail for not empty float array" {
            shouldThrowAny {
               floatArrayOf(1f).shouldBeEmpty()
            }.message shouldBe "FloatArray should be empty but has 1 elements, first being: 1.0f"
         }

         "fail for not empty double array" {
            shouldThrowAny {
               doubleArrayOf(1.0).shouldBeEmpty()
            }.message shouldBe "DoubleArray should be empty but has 1 elements, first being: 1.0"
         }

         "fail for single element list" {
            shouldThrowAny {
               listOf(0).shouldBeEmpty()
            }.message shouldBe "List should be empty but has 1 elements, first being: 0"
         }

         "fail for single element set" {
            shouldThrowAny {
               setOf(0).shouldBeEmpty()
            }.message shouldBe "Set should be empty but has 1 elements, first being: 0"
         }

         "fail for single element array" {
            shouldThrowAny {
               arrayOf(0).shouldBeEmpty()
            }.message shouldBe "Array should be empty but has 1 elements, first being: 0"
         }

         "fail for null list reference" {
            val maybeList: List<String>? = null
            shouldThrowAny {
               maybeList.shouldBeEmpty()
            }.message shouldBe "Expected Iterable but was null"
         }

         "fail for null set reference" {
            val maybeSet: Set<String>? = null
            shouldThrowAny {
               maybeSet.shouldBeEmpty()
            }.message shouldBe "Expected Iterable but was null"
         }

         "return non nullable reference" {
            val maybeList: List<Int>? = listOf()
            maybeList.shouldBeEmpty().size
         }
      }

      "shouldNotBeEmpty" should {
         "fail for empty boolean array" {
            shouldThrowAny {
               booleanArrayOf().shouldNotBeEmpty()
            }.message shouldBe "BooleanArray should not be empty"
         }

         "fail for empty byte array" {
            shouldThrowAny {
               byteArrayOf().shouldNotBeEmpty()
            }.message shouldBe "ByteArray should not be empty"
         }

         "fail for empty short array" {
            shouldThrowAny {
               shortArrayOf().shouldNotBeEmpty()
            }.message shouldBe "ShortArray should not be empty"
         }

         "fail for empty char array" {
            shouldThrowAny {
               charArrayOf().shouldNotBeEmpty()
            }.message shouldBe "CharArray should not be empty"
         }

         "fail for empty int array" {
            shouldThrowAny {
               intArrayOf().shouldNotBeEmpty()
            }.message shouldBe "IntArray should not be empty"
         }

         "fail for empty long array" {
            shouldThrowAny {
               longArrayOf().shouldNotBeEmpty()
            }.message shouldBe "LongArray should not be empty"
         }

         "fail for empty float array" {
            shouldThrowAny {
               floatArrayOf().shouldNotBeEmpty()
            }.message shouldBe "FloatArray should not be empty"
         }

         "fail for empty double array" {
            shouldThrowAny {
               doubleArrayOf().shouldNotBeEmpty()
            }.message shouldBe "DoubleArray should not be empty"
         }

         "fail for empty list" {
            shouldThrowAny {
               emptyList<Int>().shouldNotBeEmpty()
            }.message shouldBe "List should not be empty"
         }

         "fail for empty set" {
            shouldThrowAny {
               emptySet<Int>().shouldNotBeEmpty()
            }.message shouldBe "Set should not be empty"
         }

         "fail for empty typed array" {
            shouldThrowAny {
               emptyArray<Int>().shouldNotBeEmpty()
            }.message shouldBe "Array should not be empty"
         }

         "fail for null reference" {
            val maybeList: List<String>? = null
            shouldThrowAny {
               maybeList.shouldNotBeEmpty()
            }.message shouldBe "Expected Iterable but was null"
         }

         "succeed for non-null nullable reference" {
            val maybeList: List<Int>? = listOf(1)
            maybeList.shouldNotBeEmpty()
         }

         "chain for non-null nullable reference" {
            val maybeList: List<Int>? = listOf(1)
            maybeList.shouldNotBeEmpty().shouldHaveSize(1)
         }

         "succeed for single element list" {
            listOf(0).shouldNotBeEmpty()
         }

         "succeed for multiple element list" {
            listOf(1, 2, 3).shouldNotBeEmpty()
         }

         "succeed for single element set" {
            setOf(0).shouldNotBeEmpty()
         }

         "succeed for multiple element set" {
            setOf(1, 2, 3).shouldNotBeEmpty()
         }

         "succeed for single element array" {
            arrayOf(0).shouldNotBeEmpty()
         }

         "succeed for multiple element array" {
            arrayOf(1, 2, 3).shouldNotBeEmpty()
         }

         "succeed for not empty boolean array" {
            booleanArrayOf(true).shouldNotBeEmpty()
         }

         "succeed for not empty byte array" {
            byteArrayOf(1).shouldNotBeEmpty()
         }

         "succeed for not empty short array" {
            shortArrayOf(1).shouldNotBeEmpty()
         }

         "succeed for not empty char array" {
            charArrayOf('a').shouldNotBeEmpty()
         }

         "succeed for not empty int array" {
            intArrayOf(1).shouldNotBeEmpty()
         }

         "succeed for not empty long array" {
            longArrayOf(1).shouldNotBeEmpty()
         }

         "succeed for not empty float array" {
            floatArrayOf(1f).shouldNotBeEmpty()
         }

         "succeed for not empty double array" {
            doubleArrayOf(1.0).shouldNotBeEmpty()
         }

         "not deadlock on infinite iterable" {
            InfiniteIterable().shouldNotBeEmpty()
         }
      }
   }
}
