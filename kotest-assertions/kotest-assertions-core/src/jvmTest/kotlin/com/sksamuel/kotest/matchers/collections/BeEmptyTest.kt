package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldNotBeEmpty
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

         "succeed for empty list" {
            listOf<Int>().shouldBeEmpty()
         }

         "succeed for empty set" {
            setOf<Int>().shouldBeEmpty()
         }

         "succeed for empty sequence" {
            emptySequence<Int>().shouldBeEmpty()
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
            }.message shouldBe "BooleanArray should be empty but contained true"
         }

         "fail for not empty byte array" {
            shouldThrowAny {
               byteArrayOf(1).shouldBeEmpty()
            }.message shouldBe "ByteArray should be empty but contained 1"
         }

         "fail for single element list" {
            shouldThrowAny {
               listOf(0).shouldBeEmpty()
            }.message shouldBe "Collection should be empty but contained 0"
         }

         "fail for single element set" {
            shouldThrowAny {
               setOf(0).shouldBeEmpty()
            }.message shouldBe "Collection should be empty but contained 0"
         }

         "fail for single element array" {
            shouldThrowAny {
               arrayOf(0).shouldBeEmpty()
            }.message shouldBe "Array should be empty but contained 0"
         }

         "fail for single element sequence" {
            shouldThrowAny {
               sequenceOf(0).shouldBeEmpty()
            }.message shouldBe "Sequence should be empty"
         }

         "fail for single element range" {
            shouldThrowAny {
               (1..1).shouldBeEmpty()
            }.message shouldBe "Range should be empty but contained 1"
         }

         "fail for single element open range" {
            shouldThrowAny {
               (1 until 2).shouldBeEmpty()
            }.message shouldBe "Range should be empty but contained 1"
         }

         "fail for sequence of nulls" {
            shouldThrowAny {
               sequenceOf<Int?>(null, null, null, null).shouldBeEmpty()
            }.message shouldBe "Sequence should be empty"
         }

         "fail for null list reference" {
            val maybeList: List<String>? = null
            shouldThrowAny {
               maybeList.shouldBeEmpty()
            }.message shouldBe "Expected Collection but was null"
         }

         "fail for null set reference" {
            val maybeSet: Set<String>? = null
            shouldThrowAny {
               maybeSet.shouldBeEmpty()
            }.message shouldBe "Expected Collection but was null"
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

         "fail for empty sequence" {
            shouldThrowAny {
               emptySequence<Int>().shouldNotBeEmpty()
            }
         }

         "fail for empty collection" {
            shouldThrowAny {
               emptyList<Int>().shouldNotBeEmpty()
            }.message shouldBe "Collection should not be empty"
         }

         "fail for empty typed array" {
            shouldThrowAny {
               emptyArray<Int>().shouldNotBeEmpty()
            }.message shouldBe "Array should not be empty"
         }

         "fail for empty closed range" {
            shouldThrowAny {
               (1..0).shouldNotBeEmpty()
            }.message shouldBe "Range should not be empty"
         }

         "fail for empty open range" {
            shouldThrowAny {
               (1 until 0).shouldNotBeEmpty()
            }.message shouldBe "Range should not be empty"
         }

         "fail for null reference" {
            val maybeList: List<String>? = null
            shouldThrowAny {
               maybeList.shouldNotBeEmpty()
            }.message shouldBe "Expected Collection but was null"
         }

         "succeed for non-null nullable reference" {
            val maybeList: List<Int>? = listOf(1)
            maybeList.shouldNotBeEmpty()
         }

         "chain for non-null nullable reference" {
            val maybeList: List<Int>? = listOf(1)
            maybeList.shouldNotBeEmpty().shouldHaveSize(1)
         }

         "succeed for single element sequence" {
            sequenceOf(0).shouldNotBeEmpty()
         }

         "succeed for multiple element sequence" {
            sequenceOf(1, 2, 3).shouldNotBeEmpty()
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
      }
   }
}
