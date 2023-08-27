package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.shouldFailWithMessage
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
import java.io.File
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
            actual.toSet().shouldContainExactly(linkedSetOf(1, 2, 3))

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

         "Iterable with non-stable iteration order gives an informative message" {

            // Ideally the last two newlines shouldn't be there, IMO.
            val expectedMessage = """
               Disallowed: Sets can only be compared to sets, unless both types provide a stable iteration order.
               HashSet does not provide a stable iteration order and was compared with ArrayList which is not a Set


            """.trimIndent()

            shouldFailWithMessage(expectedMessage) {
               hashSetOf(1, 2) shouldContainExactly listOf(1, 2)
            }

            shouldFailWithMessage(expectedMessage) {
               listOf(1, 2) shouldContainExactly hashSetOf(1, 2)
            }
         }

         "test contains exactly for byte arrays" {
            listOf("hello".toByteArray()) shouldContainExactly listOf("hello".toByteArray())
            listOf("helloworld".toByteArray()) shouldNotContainExactly listOf("hello".toByteArray())
         }

         "print errors unambiguously"  {
            shouldThrow<AssertionError> {
               listOf<Any>(1L, 2L).shouldContainExactly(listOf<Any>(1, 2))
            } shouldHaveMessage
               """
                  |Expecting: [1, 2] but was: [1L, 2L]
                  |Some elements were missing: [1, 2] and some elements were unexpected: [1L, 2L]
                  |
                  |expected:<[1, 2]> but was:<[1L, 2L]>
               """.trimMargin()
         }

         "print dataclasses" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("woo", true, 97821, inputPath),
                  Blonde("goo", true, 51984, inputPath)
               ).shouldContainExactly(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("woo", true, 97821, inputPath)
               )
            }.message?.trim() shouldBe
               """
                  |Expecting: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)]
                  |Some elements were unexpected: [Blonde(a=goo, b=true, c=51984, p=$expectedPath)]
                  |
                  |expected:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)]>
               """.trimMargin()
         }

         "include extras when too many" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, inputPath)
               ).shouldContainExactly(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("woo", true, 97821, inputPath)
               )
            }.message?.trim() shouldBe
               """
                  |Expecting: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath)]
                  |Some elements were missing: [Blonde(a=woo, b=true, c=97821, p=$expectedPath)]
                  |
                  |expected:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath)]>
               """.trimMargin()
         }

         "include missing when too few" {

            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("hoo", true, 96915, inputPath)
               ).shouldContainExactly(
                  Blonde("woo", true, 97821, inputPath)
               )
            }.message?.trim() shouldBe
               """
                  |Expecting: [Blonde(a=woo, b=true, c=97821, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |Some elements were missing: [Blonde(a=woo, b=true, c=97821, p=$expectedPath)] and some elements were unexpected: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |
                  |expected:<[Blonde(a=woo, b=true, c=97821, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]>
               """.trimMargin()
         }

         "include missing and extras when not the right amount" {
            shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("hoo", true, 96915, inputPath)
               ).shouldContainExactly(
                  Blonde("woo", true, 97821, inputPath),
                  Blonde("goo", true, 51984, inputPath)
               )
            }.message?.trim() shouldBe
               """
                  |Expecting: [Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |Some elements were missing: [Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)] and some elements were unexpected: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |
                  |expected:<[Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]>
               """.trimMargin()
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

         "disambiguate when using optional expected value" {
            val actual: List<String> = listOf("A", "B", "C")
            val expected: List<String>? = listOf("A", "B", "C")
            actual.shouldContainExactlyInAnyOrder(expected)
         }
      }
   }

   companion object {

      /** Note: [Path.toString] is platform-dependent, as the path separator is
       * `\` on Windows or `/` on Unix. There's no easy way to configure this.
       *
       * Tests in [ShouldContainExactlyTest] depends on result of `toString()`,
       * so use [expectedPath] instead of expecting a raw String `a/b/c` (this
       * will fail on Windows).
       */
      val inputPath: Path = Paths.get("a/b/c")

      /** The expected result of [inputPath]`.toString()` (with Windows or Unix path separators) */
      val expectedPath = listOf("a", "b", "c").joinToString(File.separator)

   }
}

data class Blonde(val a: String, val b: Boolean, val c: Int, val p: Path)
