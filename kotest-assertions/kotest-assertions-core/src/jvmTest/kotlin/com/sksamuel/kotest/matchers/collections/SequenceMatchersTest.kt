package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.scopes.WordSpecShouldContainerScope
import io.kotest.core.spec.style.scopes.WordSpecTerminalScope
import io.kotest.core.spec.style.scopes.WordSpecWhenContainerScope
import io.kotest.matchers.sequences.shouldBeLargerThan
import io.kotest.matchers.sequences.shouldBeSameCountAs
import io.kotest.matchers.sequences.shouldBeSmallerThan
import io.kotest.matchers.sequences.shouldBeSorted
import io.kotest.matchers.sequences.shouldBeSortedWith
import io.kotest.matchers.sequences.shouldBeUnique
import io.kotest.matchers.sequences.shouldContain
import io.kotest.matchers.sequences.shouldContainAll
import io.kotest.matchers.sequences.shouldContainAllInAnyOrder
import io.kotest.matchers.sequences.shouldContainDuplicates
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.sequences.shouldContainInOrder
import io.kotest.matchers.sequences.shouldContainNoNulls
import io.kotest.matchers.sequences.shouldContainNull
import io.kotest.matchers.sequences.shouldContainOnlyNulls
import io.kotest.matchers.sequences.shouldExist
import io.kotest.matchers.sequences.shouldHaveAtLeastCount
import io.kotest.matchers.sequences.shouldHaveAtMostCount
import io.kotest.matchers.sequences.shouldHaveCount
import io.kotest.matchers.sequences.shouldHaveElementAt
import io.kotest.matchers.sequences.shouldHaveLowerBound
import io.kotest.matchers.sequences.shouldHaveSingleElement
import io.kotest.matchers.sequences.shouldHaveUpperBound
import io.kotest.matchers.sequences.shouldNotBeSorted
import io.kotest.matchers.sequences.shouldNotBeSortedWith
import io.kotest.matchers.sequences.shouldNotBeUnique
import io.kotest.matchers.sequences.shouldNotContain
import io.kotest.matchers.sequences.shouldNotContainAllInAnyOrder
import io.kotest.matchers.sequences.shouldNotContainDuplicates
import io.kotest.matchers.sequences.shouldNotContainExactly
import io.kotest.matchers.sequences.shouldNotContainNoNulls
import io.kotest.matchers.sequences.shouldNotContainNull
import io.kotest.matchers.sequences.shouldNotContainOnlyNulls
import io.kotest.matchers.sequences.shouldNotHaveCount
import io.kotest.matchers.sequences.shouldNotHaveElementAt
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage

class SequenceMatchersTest : WordSpec() {

   /* PassFail */
   private suspend fun WordSpecShouldContainerScope.pass(name: String, test: suspend WordSpecTerminalScope.() -> Unit) {
      ("succeed $name")(test)
   }

   private suspend fun WordSpecShouldContainerScope.succeed(name: String, test: suspend WordSpecTerminalScope.() -> Unit) = pass(name, test)

   fun WordSpecShouldContainerScope.fail(msg: String): Nothing = io.kotest.assertions.fail(msg)
   suspend fun WordSpecShouldContainerScope.fail(name: String, test: () -> Any?) {
      ("fail $name") { shouldThrow<AssertionError>(test) }
   }

   suspend inline fun <reified E : Throwable> WordSpecShouldContainerScope.abort(name: String, crossinline test: () -> Any?) {
      ("abort $name") { shouldThrow<E>(test) }
   }

   suspend inline fun <reified E : Throwable> WordSpecShouldContainerScope.`throw`(name: String, crossinline test: () -> Any?) = abort<E>(
      name,
      test)

   /* sample data */
   interface SampleData {
      val empty: Sequence<Int>
      val single: Sequence<Int>
      val nulls: Sequence<Int?>
      val sparse: Sequence<Int?>
      val countup: Sequence<Int>
      val countdown: Sequence<Int>
      val unique: Sequence<Int>
      val repeating: Sequence<Int>
   }

   val nonConstrainedSampleData = object : SampleData {
      override val empty = emptySequence<Int>()
      override val single = sequenceOf(0)
      override val nulls = sequenceOf<Int?>(null, null, null, null)
      override val sparse = sequenceOf(null, null, null, 3)
      override val countup = (0..10).asSequence()
      override val countdown = (10 downTo 0).asSequence()
      override val unique = sequenceOf(3, 2, 1)
      override val repeating = sequenceOf(1, 2, 3, 1, 2, 3)
   }
   val constrainedSampleData = object : SampleData {
      override val empty: Sequence<Int>
         get() = nonConstrainedSampleData.empty.constrainOnce()
      override val single: Sequence<Int>
         get() = nonConstrainedSampleData.single.constrainOnce()
      override val nulls: Sequence<Int?>
         get() = nonConstrainedSampleData.nulls.constrainOnce()
      override val sparse: Sequence<Int?>
         get() = nonConstrainedSampleData.sparse.constrainOnce()
      override val countup: Sequence<Int>
         get() = nonConstrainedSampleData.countup.constrainOnce()
      override val countdown: Sequence<Int>
         get() = nonConstrainedSampleData.countdown.constrainOnce()
      override val unique: Sequence<Int>
         get() = nonConstrainedSampleData.unique.constrainOnce()
      override val repeating: Sequence<Int>
         get() = nonConstrainedSampleData.repeating.constrainOnce()

   }

   val asc = { a: Int, b: Int -> a - b }
   val desc = { a: Int, b: Int -> b - a }

   /* tests */
   init {

      "non-constrained" When {
         runTestsForSample(nonConstrainedSampleData)
      }
      "constrained" When {
         runTestsForSample(constrainedSampleData)
      }
   }

   private suspend fun WordSpecWhenContainerScope.runTestsForSample(sampleData: SampleData) {
      /* count */
      "have count" should {
         succeed("for empty when 0") {
            sampleData.empty.shouldHaveCount(0)
         }

         fail("for empty when non-zero") {
            sampleData.empty.shouldHaveCount(1)
         }

         succeed("for single when 1") {
            sampleData.single.shouldHaveCount(1)
         }

         fail("for single when 0") {
            sampleData.single.shouldHaveCount(0)
         }

         "match count() for multiple" {
            sampleData.sparse.shouldHaveCount(sampleData.sparse.count())
         }

         fail("to mis-match count() for multiple") {
            sampleData.sparse.shouldHaveCount(sampleData.sparse.count() - 1)
         }
      }

      "not have count" should {
         fail("for empty when non-zero") {
            sampleData.empty.shouldNotHaveCount(0)
         }

         succeed("for empty when non-zero") {
            sampleData.empty.shouldNotHaveCount(1)
         }

         fail("for single when 1") {
            sampleData.single.shouldNotHaveCount(1)
         }

         succeed("for single when 0") {
            sampleData.single.shouldNotHaveCount(0)
         }

         fail("to match count() for multiple") {
            sampleData.sparse.shouldNotHaveCount(sampleData.sparse.count())
         }

         "mis-match count() for multiple" {
            sampleData.sparse.shouldNotHaveCount(sampleData.sparse.count() - 1)
         }
      }

      "larger than" should {
         fail("for empty") {
            sampleData.empty.shouldBeLargerThan(sampleData.single)
         }

         succeed("with empty") {
            sampleData.single.shouldBeLargerThan(sampleData.empty)
         }

         fail("for smaller") {
            sampleData.nulls.shouldBeLargerThan(sampleData.countup)
         }

         fail("for same count") {
            sampleData.countup.shouldBeLargerThan(sampleData.countdown)
         }

         succeed("for larger") {
            sampleData.countup.shouldBeLargerThan(sampleData.nulls)
         }
      }

      "smaller than" should {
         succeed("for empty") {
            sampleData.empty.shouldBeSmallerThan(sampleData.single)
         }

         fail("with empty") {
            sampleData.single.shouldBeSmallerThan(sampleData.empty)
         }

         succeed("for smaller") {
            sampleData.nulls.shouldBeSmallerThan(sampleData.countup)
         }

         fail("for same count") {
            sampleData.countup.shouldBeSmallerThan(sampleData.countdown)
         }

         fail("for larger") {
            sampleData.countup.shouldBeSmallerThan(sampleData.nulls)
         }
      }

      "same count" should {
         fail("for empty with any") {
            sampleData.empty.shouldBeSameCountAs(sampleData.single)
         }

         fail("for any with empty") {
            sampleData.nulls.shouldBeSameCountAs(sampleData.empty)
         }

         fail("for smaller") {
            sampleData.nulls.shouldBeSameCountAs(sampleData.countup)
         }

         succeed("with same count") {
            sampleData.countup.shouldBeSameCountAs(sampleData.countdown)
         }

         fail("for larger") {
            sampleData.countup.shouldBeSameCountAs(sampleData.nulls)
         }
      }

      "at least count" should {
         succeed("for empty with -1") {
            sampleData.empty.shouldHaveAtLeastCount(-1)
         }

         succeed("for any with -1") {
            sampleData.countup.shouldHaveAtLeastCount(-1)
         }

         succeed("for empty with 0") {
            sampleData.empty.shouldHaveAtLeastCount(0)
         }

         fail("for empty with 1") {
            sampleData.empty.shouldHaveAtLeastCount(1)
         }

         succeed("for smaller count") {
            sampleData.single.shouldHaveAtLeastCount(0)
         }

         succeed("for same count") {
            sampleData.nulls.shouldHaveAtLeastCount(sampleData.nulls.count())
         }

         fail("for larger count") {
            sampleData.countup.shouldHaveAtLeastCount(sampleData.countup.count() + 1)
         }
      }

      "at most count" should {
         fail("for empty with -1") {
            sampleData.empty.shouldHaveAtMostCount(-1)
         }

         succeed("for empty with 0") {
            sampleData.empty.shouldHaveAtMostCount(0)
         }

         succeed("for empty with 1") {
            sampleData.empty.shouldHaveAtMostCount(1)
         }

         fail("for smaller count") {
            sampleData.countup.shouldHaveAtMostCount(sampleData.countup.count() - 1)
         }

         succeed("for same count") {
            sampleData.countup.shouldHaveAtMostCount(sampleData.countup.count())
         }

         succeed("for larger count") {
            sampleData.countup.shouldHaveAtMostCount(sampleData.countup.count() + 1)
         }
      }


      /* contain */
      /** null */
      "contain only nulls" should {
         succeed("for empty") {
            sampleData.empty.shouldContainOnlyNulls()
         }

         fail("for single") {
            sampleData.single.shouldContainOnlyNulls()
         }

         succeed("for nulls") {
            sampleData.nulls.shouldContainOnlyNulls()
         }

         fail("for sparse") {
            sampleData.sparse.shouldContainOnlyNulls()
         }
      }

      "not contain only nulls" should {
         fail("for empty") {
            sampleData.empty.shouldNotContainOnlyNulls()
         }

         "fail for single" {
            sampleData.single.shouldNotContainOnlyNulls()
         }

         fail("for nulls") {
            sampleData.nulls.shouldNotContainOnlyNulls()
         }

         succeed("for sparse") {
            sampleData.sparse.shouldNotContainOnlyNulls()
         }
      }

      "contain a null" should {
         fail("for empty") {
            sampleData.empty.shouldContainNull()
         }

         fail("for non-nulls") {
            sampleData.single.shouldContainNull()
         }

         succeed("for nulls") {
            sampleData.nulls.shouldContainNull()
         }

         succeed("for sparse") {
            sampleData.sparse.shouldContainNull()
         }
      }

      "not contain a null" should {
         succeed("for empty") {
            sampleData.empty.shouldNotContainNull()
         }

         succeed("for non-nulls") {
            sampleData.single.shouldNotContainNull()
         }

         fail("for nulls") {
            sampleData.nulls.shouldNotContainNull()
         }

         fail("for sparse") {
            sampleData.sparse.shouldNotContainNull()
         }
      }

      "contain no nulls" should {
         succeed("for empty") {
            sampleData.empty.shouldContainNoNulls()
         }

         succeed("for non-nulls") {
            sampleData.single.shouldContainNoNulls()
         }

         fail("for nulls") {
            sampleData.nulls.shouldContainNoNulls()
         }

         fail("for sparse") {
            sampleData.sparse.shouldContainNoNulls()
         }
      }

      "not contain no nulls" should {
         fail("for empty") {
            sampleData.empty.shouldNotContainNoNulls()
         }

         fail("for non-nulls") {
            sampleData.single.shouldNotContainNoNulls()
         }

         succeed("for nulls") {
            sampleData.nulls.shouldNotContainNoNulls()
         }

         succeed("for sparse") {
            sampleData.sparse.shouldNotContainNoNulls()
         }
      }

      /** single-value */
      "single element" should {
         fail("for empty") {
            sampleData.empty.shouldHaveSingleElement(null)
         }

         succeed("for single") {
            sampleData.single.shouldHaveSingleElement(sampleData.single.first())
         }

         fail("for multiple") {
            sampleData.nulls.shouldHaveSingleElement(null)
         }
      }

      "have element at" should {
         abort<IndexOutOfBoundsException>("for empty") {
            sampleData.empty.shouldHaveElementAt(sampleData.empty.count(), 0)
         }

         abort<IndexOutOfBoundsException>("when an element after the end is requested") {
            sampleData.nulls.shouldHaveElementAt(sampleData.nulls.count(), 0)
         }

         succeed("when the sequence has the element") {
            sampleData.countup.shouldHaveElementAt(10, 10)
         }

         fail("when the sequence doesn't have the element") {
            sampleData.countdown.shouldHaveElementAt(10, 10)
         }
      }

      "not have element at" should {
         abort<IndexOutOfBoundsException>("for empty") {
            sampleData.empty.shouldNotHaveElementAt(sampleData.empty.count(), 0)
         }

         abort<IndexOutOfBoundsException>("when an element after the end is requested") {
            sampleData.nulls.shouldNotHaveElementAt(sampleData.nulls.count(), 0)
         }

         fail("when the sequence has the element") {
            sampleData.countup.shouldNotHaveElementAt(10, 10)
         }

         succeed("when the sequence doesn't have the element") {
            sampleData.countdown.shouldNotHaveElementAt(10, 10)
         }
      }

      "contain" should {
         fail("for empty") {
            sampleData.empty.shouldContain(0)
         }

         succeed("when the sequence contains the value") {
            sampleData.countup.shouldContain(2)
         }

         fail("when the sequence doesn't contain the value") {
            sampleData.sparse.shouldContain(2)
         }
      }

      "not contain" should {
         succeed("for empty") {
            sampleData.empty.shouldNotContain(0)
         }

         fail("when the sequence contains the value") {
            sampleData.countup.shouldNotContain(2)
         }

         succeed("when the sequence doesn't contain the value") {
            sampleData.sparse.shouldNotContain(2)
         }
      }

      "exist" should {
         fail("for empty") {
            sampleData.empty.shouldExist { true }
         }

         succeed("when always true") {
            sampleData.single.shouldExist { true }
         }

         fail("when always false") {
            sampleData.countup.shouldExist { false }
         }

         succeed("when matches at least one") {
            sampleData.countdown.shouldExist { it % 5 == 4 }
         }

         fail("when matches none") {
            sampleData.countdown.shouldExist { it > 20 }
         }
      }

      /** multiple-value */
      "contain all" should {
         succeed("for empty with empty") {
            sampleData.empty.shouldContainAll(sampleData.empty)
         }

         succeed("for empty with empty (variadic)") {
            sampleData.empty.shouldContainAll()
         }

         fail("for empty with any other") {
            sampleData.empty.shouldContainAll(sampleData.single)
         }

         succeed("for any with empty") {
            sampleData.single.shouldContainAll(sampleData.empty)
         }

         succeed("for any with empty (variadic)") {
            sampleData.single.shouldContainAll()
         }

         succeed("for subset of nulls") {
            sampleData.sparse.shouldContainAll(sampleData.nulls)
         }

         succeed("for subset of nulls (variadic)") {
            sampleData.sparse.shouldContainAll(null, null)
         }

         succeed("for subset in order (variadic)") {
            sampleData.countdown.shouldContainAll(2, 3, 5, 7)
         }

         succeed("for subset not in order (variadic)") {
            sampleData.countdown.shouldContainAll(2, 5, 3, 7)
         }

         succeed("for same elements") {
            sampleData.repeating.shouldContainAll(sampleData.unique)
         }

         succeed("for same elements (variadic)") {
            sampleData.repeating.shouldContainAll(2, 3, 1)
         }

         succeed("for same elements, repeated") {
            sampleData.unique.shouldContainAll(sampleData.repeating)
         }

         succeed("for same elements, repeated (variadic)") {
            sampleData.unique.shouldContainAll(1, 2, 3, 1, 2, 3)
         }
      }

      "contain exactly empty" should {
         succeed("for empty") {
            sampleData.empty.shouldContainExactly(sequenceOf<Int>())
         }

         succeed("for empty (variadic)") {
            sampleData.empty.shouldContainExactly()
         }

         fail("for single") {
            sampleData.single.shouldContainExactly(sampleData.empty)
         }

         "fail for single (variadic)" {
            shouldThrowAny {
               sampleData.single.shouldContainExactly()
            }
         }

         fail("for multiple") {
            sampleData.nulls.shouldContainExactly(sampleData.empty)
         }

         fail("for multiple (variadic)") {
            sampleData.nulls.shouldContainExactly()
         }
      }

      "contain exactly non-empty" should {
         fun nonempty() = sampleData.sparse

         fail("for empty") {
            sampleData.empty.shouldContainExactly(nonempty())
         }

         fail("for empty (variadic)") {
            sampleData.empty.shouldContainExactly(*nonempty().toList().toTypedArray())
         }

         succeed("for same") {
            sampleData.sparse.shouldContainExactly(nonempty())
         }

         succeed("for same (variadic)") {
            sampleData.sparse.shouldContainExactly(*sampleData.sparse.toList().toTypedArray())
         }

         fail("for another of different size") {
            sampleData.countup.shouldContainExactly(nonempty())
         }

         fail("for another of different size (variadic)") {
            sampleData.countup.shouldContainExactly(*nonempty().toList().toTypedArray())
         }

         fail("for another of same size") {
            sampleData.nulls.shouldContainExactly(nonempty())
         }

         fail("for another of same size (variadic)") {
            sampleData.nulls.shouldContainExactly(*nonempty().toList().toTypedArray())
         }

         fail("for same elements but different order") {
            sampleData.repeating.shouldContainExactly(sampleData.unique + sampleData.unique)
         }

         fail("for same elements but different order (variadic)") {
            sampleData.repeating.shouldContainExactly(1, 1, 2, 2, 3, 3)
         }
      }

      "not contain exactly empty" should {
         fail("for empty") {
            sampleData.empty.shouldNotContainExactly(sequenceOf<Int>())
         }

         succeed("for single") {
            sampleData.single.shouldNotContainExactly(sampleData.empty)
         }

         succeed("for multiple") {
            sampleData.nulls.shouldNotContainExactly(sampleData.empty)
         }
      }

      "not contain exactly non-empty" should {
         fun nonempty() = sampleData.sparse

         succeed("for empty") {
            sampleData.empty.shouldNotContainExactly(nonempty())
         }

         fail("for same") {
            sampleData.sparse.shouldNotContainExactly(nonempty())
         }

         succeed("for another of different size") {
            sampleData.countup.shouldNotContainExactly(nonempty())
         }

         succeed("for another of same size") {
            sampleData.nulls.shouldNotContainExactly(nonempty())
         }

         succeed("for same elements but different order") {
            sampleData.repeating.shouldNotContainExactly(sampleData.unique + sampleData.unique)
         }

         succeed("for same elements but different order (variadic)") {
            sampleData.repeating.shouldNotContainExactly(1, 1, 2, 2, 3, 3)
         }

         succeed("for single traversable equal sequence") {
            var count1 = 0
            var count2 = 0
            val seq1 = generateSequence { if (count1 < 5) count1++ else null }
            val seq2 = generateSequence { if (count2 < 5) count2++ else null }

            seq1.shouldContainExactly(seq2)
         }

         fail("for single traversable unequal sequence") {
            var count1 = 0
            var count2 = 0
            val seq1 = generateSequence { if (count1 < 5) count1++ else null }
            val seq2 = generateSequence { if (count2 < 6) count2++ else null }

            seq1.shouldContainExactly(seq2)
         }

      }

      "contain in any order" should {
         succeed("for empty with empty") {
            sampleData.empty.shouldContainAllInAnyOrder(sampleData.empty)
         }

         fail("for empty with any other") {
            sampleData.empty.shouldContainAllInAnyOrder(sampleData.nulls)
         }

         succeed("when elements are same") {
            sampleData.countdown.shouldContainAllInAnyOrder(sampleData.countup)
         }

         fail("for overlapping sequence") {
            sampleData.countup.shouldContainAllInAnyOrder((5..15).asSequence())
         }

         succeed("for subset, same count with nulls") {
            sampleData.sparse.shouldContainAllInAnyOrder(sampleData.nulls)
         }

         succeed("for subset, same count") {
            sampleData.repeating.shouldContainAllInAnyOrder(sampleData.unique + sampleData.unique)
         }

         succeed("for subset, same count (variadic)") {
            sampleData.repeating.shouldContainAllInAnyOrder(1, 1, 2, 2, 3, 3)
         }

         fail("for subset, different count with nulls") {
            sampleData.sparse.shouldContainAllInAnyOrder(sampleData.sparse.toSet().asSequence())
         }

         fail("for same, different count") {
            sampleData.repeating.shouldContainAllInAnyOrder(sampleData.unique)
         }
      }

      "not contain in any order" should {
         fail("for empty with empty") {
            sampleData.empty.shouldNotContainAllInAnyOrder(sampleData.empty)
         }

         succeed("for empty with any other") {
            sampleData.empty.shouldNotContainAllInAnyOrder(sampleData.nulls)
         }

         fail("when elements are same") {
            sampleData.countdown.shouldNotContainAllInAnyOrder(sampleData.countup)
         }

         succeed("for overlapping sequence") {
            sampleData.countup.shouldNotContainAllInAnyOrder((5..15).asSequence())
         }

         fail("for subset, same count with nulls") {
            sampleData.sparse.shouldNotContainAllInAnyOrder(sampleData.nulls)
         }

         fail("for subset, same count") {
            sampleData.repeating.shouldNotContainAllInAnyOrder(sampleData.unique + sampleData.unique)
         }

         fail("for subset, same count (variadic)") {
            sampleData.repeating.shouldNotContainAllInAnyOrder(1, 1, 2, 2, 3, 3)
         }

         succeed("for subset, different count with nulls") {
            sampleData.sparse.shouldNotContainAllInAnyOrder(sampleData.sparse.toSet().asSequence())
         }

         succeed("for same, different count") {
            sampleData.repeating.shouldNotContainAllInAnyOrder(sampleData.unique)
         }

      }

      "contain in order" should {

         "with empty" {
            shouldThrowAny {
               sampleData.countup.shouldContainInOrder(sampleData.empty)
            }
         }

         abort<IllegalArgumentException>("with empty (variadic)") {
            sampleData.countup.shouldContainInOrder()
         }

         fail("for overlapping sequence") {
            sampleData.countup.shouldContainInOrder((5..15).asSequence())
         }

         "describe first unmatched element" {
            shouldThrowAny {
               sequenceOf(1, 2, 3).shouldContainInOrder(sequenceOf(2, 3, 4, 5))
            }.message shouldContain "did not contain the elements [[2, 3, 4, 5]] in order, could not match element 4 at index 2"
         }

         fail("for overlapping sequence (variadic)") {
            sampleData.countup.shouldContainInOrder(*(5..15).toList().toTypedArray())
         }

         succeed("for subset in order") {
            sampleData.countup.shouldContainInOrder(sequenceOf(2, 3, 5, 7))
         }

         succeed("for subset in order (variadic)") {
            sampleData.countup.shouldContainInOrder(2, 3, 5, 7)
         }

         succeed("for subset in order with repeats") {
            sampleData.repeating.shouldContainInOrder(sequenceOf(1, 3, 1, 2))
         }

         succeed("for subset in order with repeats (variadic)") {
            sampleData.repeating.shouldContainInOrder(1, 3, 1, 2)
         }

         fail("for subset in order with too many repeats") {
            sampleData.repeating.shouldContainInOrder(sequenceOf(1, 3, 1, 2, 2))
         }

         fail("for subset in order with too many repeats (variadic)") {
            sampleData.repeating.shouldContainInOrder(1, 3, 1, 2, 2)
         }

         fail("for subset not in order") {
            sampleData.countup.shouldContainInOrder(sequenceOf(2, 5, 3, 7))
         }

         fail("for subset not in order (variadic)") {
            sampleData.countup.shouldContainInOrder(2, 5, 3, 7)
         }
      }


      /** unique */
      "unique" should {
         succeed("for empty") {
            sampleData.empty.shouldBeUnique()
         }

         succeed("for single") {
            sampleData.single.shouldBeUnique()
         }

         "fail with repeated nulls" {
            shouldThrowAny {
               sampleData.sparse.shouldBeUnique()
            }.shouldHaveMessage("Sequence should be Unique, but has duplicates: [<null>]")
         }

         "fail with repeats" {
            shouldThrowAny {
               sampleData.repeating.shouldBeUnique()
            }.shouldHaveMessage("Sequence should be Unique, but has duplicates: [1, 2, 3]")
         }

         succeed("for multiple unique") {
            sampleData.countup.shouldBeUnique()
         }
      }

      "not unique" should {
         fail("for empty") {
            sampleData.empty.shouldNotBeUnique()
         }

         fail("for single") {
            sampleData.single.shouldNotBeUnique()
         }

         succeed("with repeated nulls") {
            sampleData.sparse.shouldNotBeUnique()
         }

         succeed("with repeats") {
            sampleData.repeating.shouldNotBeUnique()
         }

         fail("for multiple unique") {
            sampleData.countup.shouldNotBeUnique()
         }
      }

      "duplicates" should {
         fail("for empty") {
            sampleData.empty.shouldContainDuplicates()
         }

         fail("for single") {
            sampleData.single.shouldContainDuplicates()
         }

         succeed("with repeated nulls") {
            sampleData.sparse.shouldContainDuplicates()
         }

         succeed("with repeats") {
            sampleData.repeating.shouldContainDuplicates()
         }

         fail("for multiple unique") {
            sampleData.countup.shouldContainDuplicates()
         }

         "fail with repeats" {
            shouldThrowAny {
               sampleData.repeating.shouldNotContainDuplicates()
            }.shouldHaveMessage("Sequence should not contain duplicates, but has some: [1, 2, 3]")
         }
      }

      /* comparable */
      /** bound */
      "have an upper bound" should {
         succeed("for empty") {
            sampleData.empty.shouldHaveUpperBound(Int.MIN_VALUE)
         }

         succeed("for single") {
            sampleData.single.shouldHaveUpperBound(0)
         }

         "fail for single with wrong bound" {
            shouldThrowAny {
               sampleData.single.shouldHaveUpperBound(-1)
            }.shouldHaveMessage("Sequence should have upper bound -1, but element at index 0 was: 0")
         }

         succeed("for multiple") {
            sampleData.countup.shouldHaveUpperBound(sampleData.countup.maxOrNull() ?: Int.MAX_VALUE)
         }

         "fail for multiple with wrong bound" {
            shouldThrowAny {
               sampleData.countup.shouldHaveUpperBound((sampleData.countup.maxOrNull() ?: Int.MAX_VALUE) - 1)
            }.shouldHaveMessage("Sequence should have upper bound 9, but element at index 10 was: 10")
         }
      }

      "have a lower bound" should {
         succeed("for empty") {
            sampleData.empty.shouldHaveLowerBound(Int.MAX_VALUE)
         }

         succeed("for single") {
            sampleData.single.shouldHaveLowerBound(0)
         }

         "fail for single with wrong bound" {
            shouldThrowAny {
               sampleData.single.shouldHaveLowerBound(1)
            }.shouldHaveMessage("Sequence should have lower bound 1, but element at index 0 was: 0")
         }

         succeed("for multiple") {
            sampleData.countup.shouldHaveLowerBound(sampleData.countup.minOrNull() ?: Int.MIN_VALUE)
         }

         "fail for multiple with wrong bound" {
            shouldThrowAny {
               sampleData.countup.shouldHaveLowerBound((sampleData.countup.minOrNull() ?: Int.MIN_VALUE) + 1)
            }.shouldHaveMessage("Sequence should have lower bound 1, but element at index 0 was: 0")
         }
      }


      /** sort */
      "sorted" should {
         succeed("for empty") {
            sampleData.empty.shouldBeSorted()
         }

         succeed("for single") {
            sampleData.single.shouldBeSorted()
         }

         fail("for repeating") {
            sampleData.repeating.shouldBeSorted()
         }

         succeed("for count-up") {
            sampleData.countup.shouldBeSorted()
         }

         fail("for count-down") {
            sampleData.countdown.shouldBeSorted()
         }
      }

      "not sorted" should {
         fail("for empty") {
            sampleData.empty.shouldNotBeSorted()
         }

         fail("for single") {
            sampleData.single.shouldNotBeSorted()
         }

         succeed("for repeating") {
            sampleData.repeating.shouldNotBeSorted()
         }

         fail("for count-up") {
            sampleData.countup.shouldNotBeSorted()
         }

         succeed("for count-down") {
            sampleData.countdown.shouldNotBeSorted()
         }
      }

      "sorted ascending" should {
         val dir = asc

         succeed("for empty") {
            sampleData.empty.shouldBeSortedWith(dir)
         }

         succeed("for single") {
            sampleData.single.shouldBeSortedWith(dir)
         }

         fail("for repeating") {
            sampleData.repeating.shouldBeSortedWith(dir)
         }

         succeed("for count-up") {
            sampleData.countup.shouldBeSortedWith(dir)
         }

         fail("for count-down") {
            sampleData.countdown.shouldBeSortedWith(dir)
         }

      }

      "sorted descending" should {
         val dir = desc

         succeed("for empty") {
            sampleData.empty.shouldBeSortedWith(dir)
         }

         succeed("for single") {
            sampleData.single.shouldBeSortedWith(dir)
         }

         fail("for repeating") {
            sampleData.repeating.shouldBeSortedWith(dir)
         }

         fail("for count-up") {
            sampleData.countup.shouldBeSortedWith(dir)
         }

         succeed("for count-down") {
            sampleData.countdown.shouldBeSortedWith(dir)
         }
      }

      "not sorted ascending" should {
         val dir = asc

         fail("for empty") {
            sampleData.empty.shouldNotBeSortedWith(dir)
         }

         fail("for single") {
            sampleData.single.shouldNotBeSortedWith(dir)
         }

         succeed("for repeating") {
            sampleData.repeating.shouldNotBeSortedWith(dir)
         }

         fail("for count-up") {
            sampleData.countup.shouldNotBeSortedWith(dir)
         }

         succeed("for count-down") {
            sampleData.countdown.shouldNotBeSortedWith(dir)
         }
      }

      "not sorted descending" should {
         val dir = desc

         fail("for empty") {
            sampleData.empty.shouldNotBeSortedWith(dir)
         }

         fail("for single") {
            sampleData.single.shouldNotBeSortedWith(dir)
         }

         succeed("for repeating") {
            sampleData.repeating.shouldNotBeSortedWith(dir)
         }

         succeed("for count-up") {
            sampleData.countup.shouldNotBeSortedWith(dir)
         }

         fail("for count-down") {
            sampleData.countdown.shouldNotBeSortedWith(dir)
         }
      }
   }
}
