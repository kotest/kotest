package com.sksamuel.kotest.matchers.iterator

import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.iterator.shouldNotBeEmpty
import io.kotest.specs.WordSpec

class IteratorMatchersTest: WordSpec() {

   init {

      "shouldBeEmpty" should {
         "return true when the iterator does not have a next element" {
            val emptyIterator =  object : Iterator<Int> {
               override fun hasNext(): Boolean {
                  return false
               }

               override fun next(): Int {
                  return 0
               }

            }

            emptyIterator.shouldBeEmpty()
         }

         "return false when the iterator has a next element" {
            val nonEmptyIterator =  object : Iterator<Int> {
               override fun hasNext(): Boolean {
                  return true
               }

               override fun next(): Int {
                  return 0
               }

            }

            nonEmptyIterator.shouldNotBeEmpty()
         }
      }

   }

}
