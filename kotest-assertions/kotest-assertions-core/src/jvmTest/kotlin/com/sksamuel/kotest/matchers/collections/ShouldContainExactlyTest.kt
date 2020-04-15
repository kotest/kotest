package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContainExactly
import io.kotest.matchers.collections.shouldNotContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.throwable.shouldHaveMessage
import java.nio.file.Path
import java.nio.file.Paths

class ShouldContainExactlyTest : WordSpec() {

   init {

      "containExactly" should {
         "test that an array contains given elements exactly" {
            val actual = arrayOf(1, 2, 3)
            actual.shouldContainExactly(1, 2, 3)
            actual shouldContainExactly arrayOf(1, 2, 3)
            actual.shouldNotContainExactly(3, 2, 1)
            actual shouldNotContainExactly arrayOf(3, 2, 1)

            shouldThrow<AssertionError> {
               actual.shouldContainExactly(3, 2, 1)
            }
            shouldThrow<AssertionError> {
               actual shouldContainExactly arrayOf(3, 2, 1)
            }
            shouldThrow<AssertionError> {
               actual.shouldNotContainExactly(1, 2, 3)
            }
            shouldThrow<AssertionError> {
               actual shouldNotContainExactly arrayOf(1, 2, 3)
            }

            val actualNull: Array<Int>? = null
            shouldThrow<AssertionError> {
               actualNull.shouldContainExactly(1, 2, 3)
            }.shouldHaveMessage("Expecting actual not to be null")
            shouldThrow<AssertionError> {
               actualNull.shouldNotContainExactly()
            }.shouldHaveMessage("Expecting actual not to be null")
         }

         "test that a collection contains given elements exactly"  {
            val actual = listOf(1, 2, 3)
            emptyList<Int>() should containExactly()
            actual should containExactly(1, 2, 3)
            actual.shouldContainExactly(1, 2, 3)
            actual.shouldContainExactly(linkedSetOf(1, 2, 3))

            actual shouldNot containExactly(1, 2)
            actual.shouldNotContainExactly(3, 2, 1)
            actual.shouldNotContainExactly(listOf(5, 6, 7))
            shouldThrow<AssertionError> {
               actual should containExactly(1, 2)
            }
            shouldThrow<AssertionError> {
               actual should containExactly(1, 2, 3, 4)
            }
            shouldThrow<AssertionError> {
               actual.shouldContainExactly(3, 2, 1)
            }
         }

         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf<Any>(1L, 2L).shouldContainExactly(listOf<Any>(1, 2))
            }.shouldHaveMessage(
               """Expecting: [
  1,
  2
] but was: [
  1L,
  2L
]
Some elements were missing: [
  1,
  2
] and some elements were unexpected: [
  1L,
  2L
]"""
            )
         }

         "print dataclasses" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, Paths.get("a/b/c")),
                  Blonde("woo", true, 97821, Paths.get("a/b/c")),
                  Blonde("goo", true, 51984, Paths.get("a/b/c"))
               ).shouldContainExactly(
                  Blonde("foo", true, 23423, Paths.get("a/b/c")),
                  Blonde("woo", true, 97821, Paths.get("a/b/c"))
               )
            }.message?.trim() shouldBe ("""Expecting: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=woo, b=true, c=97821, p=a/b/c)
] but was: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=woo, b=true, c=97821, p=a/b/c),
  Blonde(a=goo, b=true, c=51984, p=a/b/c)
]
Some elements were unexpected: [
  Blonde(a=goo, b=true, c=51984, p=a/b/c)
]""")
         }

         "include extras when too many" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, Paths.get("a/b/c"))
               ).shouldContainExactly(
                  Blonde("foo", true, 23423, Paths.get("a/b/c")),
                  Blonde("woo", true, 97821, Paths.get("a/b/c"))
               )
            }.message?.trim() shouldBe (
               """Expecting: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=woo, b=true, c=97821, p=a/b/c)
] but was: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c)
]
Some elements were missing: [
  Blonde(a=woo, b=true, c=97821, p=a/b/c)
]"""
               )
         }

         "include missing when too few" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, Paths.get("a/b/c")),
                  Blonde("hoo", true, 96915, Paths.get("a/b/c"))
               ).shouldContainExactly(
                  Blonde("woo", true, 97821, Paths.get("a/b/c"))
               )
            }.message?.trim() shouldBe ("""Expecting: [
  Blonde(a=woo, b=true, c=97821, p=a/b/c)
] but was: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=hoo, b=true, c=96915, p=a/b/c)
]
Some elements were missing: [
  Blonde(a=woo, b=true, c=97821, p=a/b/c)
] and some elements were unexpected: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=hoo, b=true, c=96915, p=a/b/c)
]""")
         }

         "include missing and extras when not the right amount" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, Paths.get("a/b/c")),
                  Blonde("hoo", true, 96915, Paths.get("a/b/c"))
               ).shouldContainExactly(
                  Blonde("woo", true, 97821, Paths.get("a/b/c")),
                  Blonde("goo", true, 51984, Paths.get("a/b/c"))
               )
            }.message?.trim() shouldBe """Expecting: [
  Blonde(a=woo, b=true, c=97821, p=a/b/c),
  Blonde(a=goo, b=true, c=51984, p=a/b/c)
] but was: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=hoo, b=true, c=96915, p=a/b/c)
]
Some elements were missing: [
  Blonde(a=woo, b=true, c=97821, p=a/b/c),
  Blonde(a=goo, b=true, c=51984, p=a/b/c)
] and some elements were unexpected: [
  Blonde(a=foo, b=true, c=23423, p=a/b/c),
  Blonde(a=hoo, b=true, c=96915, p=a/b/c)
]"""
         }
      }

      "containExactlyInAnyOrder" should {
         "test that a collection contains given elements exactly in any order"  {
            val actual = listOf(1, 2, 3)
            actual should containExactlyInAnyOrder(1, 2, 3)
            actual.shouldContainExactlyInAnyOrder(3, 2, 1)
            actual.shouldContainExactlyInAnyOrder(linkedSetOf(2, 1, 3))

            actual shouldNot containExactlyInAnyOrder(1, 2)
            actual.shouldNotContainExactlyInAnyOrder(1, 2, 3, 4)
            actual.shouldNotContainExactlyInAnyOrder(listOf(5, 6, 7))
            actual.shouldNotContainExactlyInAnyOrder(1, 1, 1)
            actual.shouldNotContainExactlyInAnyOrder(listOf(2, 2, 3))
            actual.shouldNotContainExactlyInAnyOrder(listOf(1, 1, 2, 3))

            val actualDuplicates = listOf(1, 1, 2)
            actualDuplicates.shouldContainExactlyInAnyOrder(1, 2, 1)
            actualDuplicates.shouldContainExactlyInAnyOrder(2, 1, 1)

            actualDuplicates.shouldNotContainExactlyInAnyOrder(1, 2)
            actualDuplicates.shouldNotContainExactlyInAnyOrder(1, 2, 2)
            actualDuplicates.shouldNotContainExactlyInAnyOrder(1, 1, 2, 2)
            actualDuplicates.shouldNotContainExactlyInAnyOrder(1, 2, 7)

            shouldThrow<AssertionError> {
               actual should containExactlyInAnyOrder(1, 2)
            }
            shouldThrow<AssertionError> {
               actual should containExactlyInAnyOrder(1, 2, 3, 4)
            }
            shouldThrow<AssertionError> {
               actual should containExactlyInAnyOrder(1, 1, 1)
            }
            shouldThrow<AssertionError> {
               actual should containExactlyInAnyOrder(1, 1, 2, 3)
            }
            shouldThrow<AssertionError> {
               actualDuplicates should containExactlyInAnyOrder(1, 2, 2)
            }
         }

         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf<Any>(1L, 2L).shouldContainExactlyInAnyOrder(listOf<Any>(1, 2))
            }.shouldHaveMessage("Collection should contain [1, 2] in any order, but was [1L, 2L]")
         }
      }
   }
}

data class Blonde(val a: String, val b: Boolean, val c: Int, val p: Path)
