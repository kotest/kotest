package com.sksamuel.kotest.matchers.iterator

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.iterator.shouldBeEmpty

class IteratorMatchersTest: WordSpec() {

   init {

      "shouldBeEmpty" should {
         "return true when the iterator does not have a next element" {
            emptyList<Int>().iterator().shouldBeEmpty()
         }

         "return false when the iterator has a next element" {
            listOf(1).iterator().shouldNotBeEmpty()
         }
      }

   }

}
