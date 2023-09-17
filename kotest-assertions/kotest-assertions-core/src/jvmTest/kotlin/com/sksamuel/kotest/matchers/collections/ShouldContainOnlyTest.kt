package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldNotContainOnly
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldContainOnlyTest : WordSpec() {

   init {
      "containOnly" should {
         "test that an array contains given elements only" {
            val actual = arrayOf(1, 1, 2, 2, 3)
            actual.shouldContainOnly(1, 2, 3)
            actual shouldContainOnly arrayOf(1, 2, 3)

            shouldThrow<AssertionError> {
               actual.shouldContainOnly(3, 2, 3, 2)
            }.shouldHaveMessage(
               """
                  Collection should contain only: [3, 2, 3, 2] but was: [1, 1, 2, 2, 3]
                  Some elements were unexpected: [1]
                   """.trimIndent()
            )

            shouldThrow<AssertionError> {
               actual shouldContainOnly arrayOf(3, 2, 4, 3)
            }.shouldHaveMessage(
               """
                  Collection should contain only: [3, 2, 4, 3] but was: [1, 1, 2, 2, 3]
                  Some elements were missing: [4] and some elements were unexpected: [1]
                   """.trimIndent()
            )

            shouldThrow<AssertionError> {
               actual shouldContainOnly arrayOf(1, 3, 2, 4, 1)
            }.shouldHaveMessage(
               """
                  Collection should contain only: [1, 3, 2, 4, 1] but was: [1, 1, 2, 2, 3]
                  Some elements were missing: [4]
                   """.trimIndent()
            )

            val actualNull: Array<Int>? = null
            shouldThrow<AssertionError> {
               actualNull.shouldContainOnly(1, 2, 1, 3)
            }.shouldHaveMessage("Expecting actual not to be null")
         }

         "test that a collection contains given elements only" {
            val actual = listOf(1, 1, 2, 2, 3)
            actual should containOnly(1, 2, 3)
            actual shouldContainOnly listOf(1, 2, 3)

            shouldThrow<AssertionError> {
               actual shouldContainOnly listOf(3, 2, 4)
            }.shouldHaveMessage(
               """
                  Collection should contain only: [3, 2, 4] but was: [1, 1, 2, 2, 3]
                  Some elements were missing: [4] and some elements were unexpected: [1]
               """.trimIndent()
            )

            shouldThrow<AssertionError> {
               actual shouldContainOnly listOf(1, 3, 2, 4)
            }.shouldHaveMessage(
               """
                  Collection should contain only: [1, 3, 2, 4] but was: [1, 1, 2, 2, 3]
                  Some elements were missing: [4]
               """.trimIndent()
            )

            val actualNull: Collection<Int>? = null
            shouldThrow<AssertionError> {
               actualNull.shouldContainOnly(1, 2, 3)
            }.shouldHaveMessage("Expecting actual not to be null")
         }

         "test that an array contains not just the given elements" {
            val actual = arrayOf(1, 1, 2, 2, 3)

            actual.shouldNotContainOnly(3, 2)
            actual shouldNotContainOnly arrayOf(3)

            shouldThrow<AssertionError> {
               actual.shouldNotContainOnly(1, 2, 3)
            }.shouldHaveMessage("Collection should not contain only [1, 2, 3]")

            shouldThrow<AssertionError> {
               actual shouldNotContainOnly arrayOf(1, 2, 3)
            }.shouldHaveMessage("Collection should not contain only [1, 2, 3]")

            val actualNull: Array<Int>? = null
            shouldThrow<AssertionError> {
               actualNull.shouldNotContainOnly()
            }.shouldHaveMessage("Expecting actual not to be null")
         }

         "test that a collection contains not just the given elements" {
            val actual = listOf<Any>(1, 1, 2, 2, 3)

            actual shouldNot containOnly(3, 2)
            actual shouldNotContainOnly listOf(3)

            shouldThrow<AssertionError> {
               actual shouldNotContainOnly listOf(1, 2, 3)
            }.shouldHaveMessage("Collection should not contain only [1, 2, 3]")

            val actualNull: Collection<Int>? = null
            shouldThrow<AssertionError> {
               actualNull.shouldNotContainOnly()
            }.shouldHaveMessage("Expecting actual not to be null")
         }
      }
   }
}
