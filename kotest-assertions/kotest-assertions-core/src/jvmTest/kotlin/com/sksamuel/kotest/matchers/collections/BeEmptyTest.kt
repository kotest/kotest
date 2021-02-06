package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldNotBeEmpty

class BeEmptyTest : WordSpec() {
   init {
      "shouldBeEmpty" should {

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

         "fail for single element list" {
            shouldThrowAny {
               listOf(0).shouldBeEmpty()
            }
         }

         "fail for single element set" {
            shouldThrowAny {
               setOf(0).shouldBeEmpty()
            }
         }

         "fail for single element array" {
            shouldThrowAny {
               arrayOf(0).shouldBeEmpty()
            }
         }

         "fail for single element sequence" {
            shouldThrowAny {
               sequenceOf(0).shouldBeEmpty()
            }
         }

         "fail for nulls" {
            shouldThrowAny {
               sequenceOf<Int?>(null, null, null, null).shouldBeEmpty()
            }
         }

         "fail for null reference" {
            val maybeList: List<String>? = null
            shouldThrowAny {
               maybeList.shouldBeEmpty()
            }
         }

         "chain for non-null nullable reference" {
            val maybeList: List<Int>? = listOf()
            maybeList.shouldBeEmpty().shouldHaveSize(0)
         }
      }

      "shouldNotBeEmpty" should {

         "fail for empty" {
            shouldThrowAny {
               emptySequence<Int>().shouldNotBeEmpty()
            }
         }

         "fail for null reference" {
            val maybeList: List<String>? = null
            shouldThrowAny {
               maybeList.shouldNotBeEmpty()
            }
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
      }
   }
}
