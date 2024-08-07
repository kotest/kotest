package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.shouldFailWithMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.equals.Equality
import io.kotest.equals.EqualityResult
import io.kotest.matchers.collections.CountMismatch
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.containExactlyInAnyOrder
import io.kotest.matchers.collections.countMismatch
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContainExactly
import io.kotest.matchers.collections.shouldNotContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.shuffle
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.of
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.time.Duration.Companion.seconds

class ShouldContainExactlyTest : WordSpec() {

   private val caseInsensitiveStringEquality: Equality<String> = object : Equality<String> {
      override fun name() = "Case Insensitive String Matcher"

      override fun verify(actual: String, expected: String): EqualityResult {
         return if (actual.uppercase() == expected.uppercase())
            EqualityResult.equal(actual, expected, this)
         else
            EqualityResult.notEqual(actual, expected, this)
      }
   }

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

         "test that a collection contains given elements exactly" {
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


         "Supports all sorted set types" {
            /**
             * Generates an [Exhaustive] for all (supported) sorted set implementations of the given elements.
             */
            fun <T> Exhaustive.Companion.sortedSetOf(vararg elements: T): Exhaustive<Set<T>> = Exhaustive.of(
               TreeSet(elements.asList()),
               ConcurrentSkipListSet(elements.asList()),
               linkedSetOf(*elements),
            )

            checkAll(Exhaustive.sortedSetOf(1, 2, 3)) { actual ->
               actual should containExactly(1, 2, 3)
               actual.shouldContainExactly(1, 2, 3)

               actual shouldNot containExactly(1, 2)
               actual.shouldNotContainExactly(1, 2)

               actual shouldNot containExactly(3, 2, 1)
               actual.shouldNotContainExactly(3, 2, 1)
               actual.shouldContainExactly(linkedSetOf(3, 2, 1))

               shouldThrow<AssertionError> {
                  actual should containExactly(1, 2)
               }

               shouldThrow<AssertionError> {
                  actual should containExactly(3, 2, 1)
               }
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

         "print errors unambiguously" {
            shouldThrow<AssertionError> {
               listOf<Any>(1L, 2L).shouldContainExactly(listOf<Any>(1, 2))
            } shouldHaveMessage
               """
                  |Collection should contain exactly: [1, 2] but was: [1L, 2L]
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
                  |Collection should contain exactly: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)]
                  |Some elements were unexpected: [Blonde(a=goo, b=true, c=51984, p=$expectedPath)]
                  |
                  |expected:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)]>
               """.trimMargin()
         }

         "include extras when too many" {
            val message = shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, inputPath)
               ).shouldContainExactly(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("woo", true, 97821, inputPath)
               )
            }.message?.trim()
            message shouldContain (
               """
                  |Collection should contain exactly: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath)]
                  |Some elements were missing: [Blonde(a=woo, b=true, c=97821, p=$expectedPath)]
               """.trimMargin()
               )

            message.shouldContain("Possible matches:")
            message.shouldContain("expected: Blonde(a=foo, b=true, c=23423, p=a/b/c),")
            message.shouldContain("but was: Blonde(a=woo, b=true, c=97821, p=a/b/c),")
            message.shouldContain("The following fields did not match:")
            message.shouldContain("\"a\" expected: <\"foo\">, but was: <\"woo\">")
            message.shouldContain("\"c\" expected: <23423>, but was: <97821>")

            message.shouldContain(
               """
                  |expected:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=woo, b=true, c=97821, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath)]>
               """.trimMargin()
            )
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
                  |Collection should contain exactly: [Blonde(a=woo, b=true, c=97821, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |Some elements were missing: [Blonde(a=woo, b=true, c=97821, p=$expectedPath)] and some elements were unexpected: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |
                  |expected:<[Blonde(a=woo, b=true, c=97821, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]>
               """.trimMargin()
         }

         "include missing and extras when not the right amount" {
            val message = shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 23423, inputPath),
                  Blonde("hoo", true, 96915, inputPath)
               ).shouldContainExactly(
                  Blonde("woo", true, 97821, inputPath),
                  Blonde("goo", true, 51984, inputPath)
               )
            }.message?.trim()
            message shouldStartWith
               """
                  |Collection should contain exactly: [Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)] but was: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
                  |Some elements were missing: [Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)] and some elements were unexpected: [Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]
               """.trimMargin()
            message.shouldContain("Possible matches:")
            message.shouldContain("expected: Blonde(a=goo, b=true, c=51984, p=a/b/c),")
            message.shouldContain("but was: Blonde(a=woo, b=true, c=97821, p=a/b/c),")
            message.shouldContain("The following fields did not match:")
            message.shouldContain("\"a\" expected: <\"goo\">, but was: <\"woo\">")
            message.shouldContain("\"c\" expected: <51984>, but was: <97821>")
            message shouldContain
               """
                  |expected:<[Blonde(a=woo, b=true, c=97821, p=$expectedPath), Blonde(a=goo, b=true, c=51984, p=$expectedPath)]> but was:<[Blonde(a=foo, b=true, c=23423, p=$expectedPath), Blonde(a=hoo, b=true, c=96915, p=$expectedPath)]>
               """.trimMargin()
         }

         "exclude full print with warning on large collections" {
            val message = shouldThrow<AssertionError> {
               listOf(
                  Blonde("foo", true, 1, inputPath),
                  Blonde("foo", true, 2, inputPath),
                  Blonde("foo", true, 3, inputPath),
                  Blonde("foo", true, 4, inputPath),
                  Blonde("foo", true, 5, inputPath),
                  Blonde("foo", true, 6, inputPath),
                  Blonde("foo", true, 7, inputPath),
                  Blonde("foo", true, 8, inputPath),
                  Blonde("foo", true, 9, inputPath),
                  Blonde("foo", true, 10, inputPath),
                  Blonde("foo", true, 11, inputPath),
                  Blonde("foo", true, 12, inputPath),
                  Blonde("foo", true, 13, inputPath),
                  Blonde("foo", true, 14, inputPath),
                  Blonde("foo", true, 15, inputPath),
                  Blonde("foo", true, 16, inputPath),
                  Blonde("foo", true, 17, inputPath),
                  Blonde("foo", true, 18, inputPath),
                  Blonde("foo", true, 19, inputPath),
                  Blonde("foo", true, 20, inputPath),
                  Blonde("foo", true, 21, inputPath),
               ).shouldContainExactly(
                  Blonde("foo", true, 77, inputPath),
                  Blonde("foo", true, 2, inputPath),
                  Blonde("foo", true, 3, inputPath),
                  Blonde("foo", true, 4, inputPath),
                  Blonde("foo", true, 5, inputPath),
                  Blonde("foo", true, 6, inputPath),
                  Blonde("foo", true, 7, inputPath),
                  Blonde("foo", true, 8, inputPath),
                  Blonde("foo", true, 9, inputPath),
                  Blonde("foo", true, 10, inputPath),
                  Blonde("foo", true, 11, inputPath),
                  Blonde("foo", true, 12, inputPath),
                  Blonde("foo", true, 13, inputPath),
                  Blonde("foo", true, 14, inputPath),
                  Blonde("foo", true, 15, inputPath),
                  Blonde("foo", true, 16, inputPath),
                  Blonde("foo", true, 17, inputPath),
                  Blonde("foo", true, 18, inputPath),
                  Blonde("foo", true, 19, inputPath),
                  Blonde("foo", true, 20, inputPath),
                  Blonde("foo", true, 21, inputPath),
               )
            }.message?.trim()
            message shouldContain
               """
                  |Collection should contain exactly: [Blonde(a=foo, b=true, c=77, p=$expectedPath), Blonde(a=foo, b=true, c=2, p=$expectedPath), Blonde(a=foo, b=true, c=3, p=$expectedPath), Blonde(a=foo, b=true, c=4, p=$expectedPath), Blonde(a=foo, b=true, c=5, p=$expectedPath), Blonde(a=foo, b=true, c=6, p=$expectedPath), Blonde(a=foo, b=true, c=7, p=$expectedPath), Blonde(a=foo, b=true, c=8, p=$expectedPath), Blonde(a=foo, b=true, c=9, p=$expectedPath), Blonde(a=foo, b=true, c=10, p=$expectedPath), Blonde(a=foo, b=true, c=11, p=$expectedPath), Blonde(a=foo, b=true, c=12, p=$expectedPath), Blonde(a=foo, b=true, c=13, p=$expectedPath), Blonde(a=foo, b=true, c=14, p=$expectedPath), Blonde(a=foo, b=true, c=15, p=$expectedPath), Blonde(a=foo, b=true, c=16, p=$expectedPath), Blonde(a=foo, b=true, c=17, p=$expectedPath), Blonde(a=foo, b=true, c=18, p=$expectedPath), Blonde(a=foo, b=true, c=19, p=$expectedPath), Blonde(a=foo, b=true, c=20, p=$expectedPath), ...and 1 more (set the 'kotest.assertions.collection.print.size' JVM property to see more / less items)] but was: [Blonde(a=foo, b=true, c=1, p=$expectedPath), Blonde(a=foo, b=true, c=2, p=$expectedPath), Blonde(a=foo, b=true, c=3, p=$expectedPath), Blonde(a=foo, b=true, c=4, p=$expectedPath), Blonde(a=foo, b=true, c=5, p=$expectedPath), Blonde(a=foo, b=true, c=6, p=$expectedPath), Blonde(a=foo, b=true, c=7, p=$expectedPath), Blonde(a=foo, b=true, c=8, p=$expectedPath), Blonde(a=foo, b=true, c=9, p=$expectedPath), Blonde(a=foo, b=true, c=10, p=$expectedPath), Blonde(a=foo, b=true, c=11, p=$expectedPath), Blonde(a=foo, b=true, c=12, p=$expectedPath), Blonde(a=foo, b=true, c=13, p=$expectedPath), Blonde(a=foo, b=true, c=14, p=$expectedPath), Blonde(a=foo, b=true, c=15, p=$expectedPath), Blonde(a=foo, b=true, c=16, p=$expectedPath), Blonde(a=foo, b=true, c=17, p=$expectedPath), Blonde(a=foo, b=true, c=18, p=$expectedPath), Blonde(a=foo, b=true, c=19, p=$expectedPath), Blonde(a=foo, b=true, c=20, p=$expectedPath), ...and 1 more (set the 'kotest.assertions.collection.print.size' JVM property to see more / less items)]
                  |Some elements were missing: [Blonde(a=foo, b=true, c=77, p=$expectedPath)] and some elements were unexpected: [Blonde(a=foo, b=true, c=1, p=$expectedPath)]
               """.trimMargin()
               message.shouldContain("Possible matches:")
               message.shouldContain("expected: Blonde(a=foo, b=true, c=2, p=a/b/c),")
               message.shouldContain("but was: Blonde(a=foo, b=true, c=77, p=a/b/c),")
               message.shouldContain("The following fields did not match:")
               message.shouldContain("\"c\" expected: <2>, but was: <77>")
            message shouldContain "Printed first 5 similarities out of 20, (set the 'kotest.assertions.similarity.print.size' JVM property to see full output for similarity)"
            message shouldContain
               """
                  |(set the 'kotest.assertions.collection.print.size' JVM property to see more / less items)
               """.trimMargin()
         }

         "pass with custom verifier" {
            listOf("Apple", "ORANGE", "apple") should containExactly(
               listOf("Apple", "orange", "APPLE"),
               caseInsensitiveStringEquality
            )
         }

         "fail with custom verifier" {
            listOf("Apple", "ORANGE", "orange") shouldNot containExactly(
               listOf("Apple", "orange", "APPLE"),
               caseInsensitiveStringEquality
            )
         }
      }

      "containExactlyInAnyOrder" should {
         "test that a collection contains given elements exactly in any order" {
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

         "print errors unambiguously" {
            shouldThrow<AssertionError> {
               listOf<Any>(1L, 2L).shouldContainExactlyInAnyOrder(listOf<Any>(1, 2))
            }.shouldHaveMessage(
               """
                  Collection should contain [1, 2] in any order, but was [1L, 2L]
                  Some elements were missing: [1, 2] and some elements were unexpected: [1L, 2L]
               """.trimIndent()
            )
         }

         "print count mismatches for not null keys" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 2, 3).shouldContainExactlyInAnyOrder(listOf(1, 2, 3, 3))
            }.shouldHaveMessage(
               """
                  Collection should contain [1, 2, 3, 3] in any order, but was [1, 2, 2, 3]
                  CountMismatches: Key="2", expected count: 1, but was: 2, Key="3", expected count: 2, but was: 1
               """.trimIndent()
            )
         }

         "print count mismatches for nullable keys" {
            shouldThrow<AssertionError> {
               listOf(1, null, null, 3).shouldContainExactlyInAnyOrder(listOf(1, null, 3, 3))
            }.shouldHaveMessage(
               """
                  Collection should contain [1, <null>, 3, 3] in any order, but was [1, <null>, <null>, 3]
                  CountMismatches: Key="null", expected count: 1, but was: 2, Key="3", expected count: 2, but was: 1
               """.trimIndent()
            )
         }

         "find similar elements for unexpected key" {
            val message = shouldThrow<AssertionError> {
               listOf(sweetGreenApple, sweetRedApple).shouldContainExactlyInAnyOrder(
                  listOf(
                     sweetGreenApple,
                     sweetGreenPear
                  )
               )
            }.message
            message shouldContain """
               |Possible matches for unexpected elements:
               |
               | expected: Fruit(name=apple, color=green, taste=sweet),
               |  but was: Fruit(name=apple, color=red, taste=sweet),
               |  The following fields did not match:
               |    "color" expected: <"green">, but was: <"red">
            """.trimMargin()
         }

         "disambiguate when using optional expected value" {
            val actual: List<String> = listOf("A", "B", "C")
            val expected: List<String>? = listOf("A", "B", "C")
            actual.shouldContainExactlyInAnyOrder(expected)
         }

         "maintain performance".config(timeout = 1.seconds) {
            checkAll(1000, Arb.shuffle(listOf("1", "2", "3", "4", "5", "6", "7"))) {
               it shouldContainExactlyInAnyOrder listOf("1", "2", "3", "4", "5", "6", "7")
            }
         }

         "use custom verifier correctly" {
            val caseInsensitiveStringEquality: Equality<String> = object : Equality<String> {
               override fun name() = "Case Insensitive String Matcher"

               override fun verify(actual: String, expected: String): EqualityResult {
                  return if (actual.uppercase() == expected.uppercase())
                     EqualityResult.equal(actual, expected, this)
                  else
                     EqualityResult.notEqual(actual, expected, this)
               }
            }
            listOf("apple", "orange", "Apple") should containExactlyInAnyOrder(
               listOf("APPLE", "APPLE", "Orange"),
               caseInsensitiveStringEquality
            )
            listOf("apple", "orange", "Orange") shouldNot containExactlyInAnyOrder(
               listOf("APPLE", "APPLE", "Orange"),
               caseInsensitiveStringEquality
            )
         }
      }

      "countMismatch" should {
         "return empty list for a complete match" {
            val counts = mapOf("apple" to 1, "orange" to 2)
            countMismatch(counts, counts, Equality.default()).shouldBeEmpty()
         }
         "return differences for not null key" {
            countMismatch(
               mapOf("apple" to 1, "orange" to 2, "banana" to 3),
               mapOf("apple" to 2, "orange" to 2, "peach" to 1),
               Equality.default()
            ) shouldBe listOf(
               CountMismatch("apple", 1, 2)
            )
         }
         "return differences for null key" {
            countMismatch(
               mapOf(null to 1, "orange" to 2, "banana" to 3),
               mapOf(null to 2, "orange" to 2, "peach" to 1),
               Equality.default()
            ) shouldBe listOf(
               CountMismatch(null, 1, 2)
            )
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
