package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.collections.shouldNotBeOneOf
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.throwable.shouldHaveMessage

class OneOfTest : DescribeSpec() {
   init {
      describe("Be one of") {
         it("Pass when the element instance is in the list") {
            val fooz = Fooz("Bar")
            val list = listOf(fooz)

            fooz shouldBeOneOf list
         }

         it("Fail when the element instance is not in the list") {
            val fooz1 = Fooz("Bar")
            val fooz2 = Fooz("Booz")

            val list = listOf(fooz1)
            shouldThrow<AssertionError> {
               fooz2.shouldBeOneOf(list)
            }.shouldHaveMessage("Collection should contain the instance Fooz(bar=Booz) with hashcode 12345.")
         }

         it("Fail when there's an equal element, but not the same instance in the list") {
            val fooz1 = Fooz("Bar")
            val fooz2 = Fooz("Bar")

            val list = listOf(fooz1)
            shouldThrow<AssertionError> {
               fooz2 shouldBeOneOf list
            }.message.shouldContainInOrder(
               "Collection should contain the instance Fooz(bar=Bar) with hashcode 12345.",
               "Found equal but not the same element(s) at index(es): [0]"
               )
         }

         it("Fail when the list is empty") {
            val fooz = Fooz("Bar")

            val list = emptyList<Fooz>()
            shouldThrow<AssertionError> {
               fooz shouldBeOneOf list
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }
      }

      describe("Be one of (negative)") {
         it("Fail when the element instance is in the list") {
            val fooz = Fooz("Bar")
            val list = listOf(fooz)

            shouldThrow<AssertionError> {
               fooz shouldNotBeOneOf list
            }.shouldHaveMessage("Collection should not contain the instance Fooz(bar=Bar) with hashcode 12345.")
         }

         it("Pass when the element instance is not in the list") {
            val fooz1 = Fooz("Bar")
            val fooz2 = Fooz("Booz")

            val list = listOf(fooz1)
            fooz2.shouldNotBeOneOf(list)
         }

         it("Pass when there's an equal element, but not the same instance in the list") {
            val fooz1 = Fooz("Bar")
            val fooz2 = Fooz("Bar")

            val list = listOf(fooz1)
            fooz2 shouldNotBeOneOf list
         }

         it("Fail when the list is empty") {
            val fooz = Fooz("Bar")

            val list = emptyList<Fooz>()
            shouldThrow<AssertionError> {
               fooz shouldNotBeOneOf list
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }

      }
   }
}

@Suppress("EqualsOrHashCode")
private data class Fooz(val bar: String) {
   override fun hashCode(): Int = 12345
}
