package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.scopes.WordSpecTerminalScope
import io.kotest.core.spec.style.scopes.WordSpecShouldScope
import io.kotest.matchers.sequences.shouldBeEmpty
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
import io.kotest.matchers.sequences.shouldNotBeEmpty
import io.kotest.matchers.sequences.shouldNotBeSorted
import io.kotest.matchers.sequences.shouldNotBeSortedWith
import io.kotest.matchers.sequences.shouldNotBeUnique
import io.kotest.matchers.sequences.shouldNotContain
import io.kotest.matchers.sequences.shouldNotContainAllInAnyOrder
import io.kotest.matchers.sequences.shouldNotContainExactly
import io.kotest.matchers.sequences.shouldNotContainNoNulls
import io.kotest.matchers.sequences.shouldNotContainNull
import io.kotest.matchers.sequences.shouldNotContainOnlyNulls
import io.kotest.matchers.sequences.shouldNotHaveCount
import io.kotest.matchers.sequences.shouldNotHaveElementAt

class SequenceMatchersTest : WordSpec() {

   /* PassFail */
   private suspend fun WordSpecShouldScope.pass(name: String, test: suspend WordSpecTerminalScope.() -> Unit) {
      ("succeed $name")(test)
   }

   private suspend fun WordSpecShouldScope.succeed(name: String, test: suspend WordSpecTerminalScope.() -> Unit) = pass(name, test)

   fun WordSpecShouldScope.fail(msg: String): Nothing = io.kotest.assertions.fail(msg)
   suspend fun WordSpecShouldScope.fail(name: String, test: () -> Any?) {
      ("fail $name") { shouldThrowAny(test) }
   }

   suspend inline fun <reified E : Throwable> WordSpecShouldScope.abort(name: String, crossinline test: () -> Any?) {
      ("abort $name") { shouldThrow<E>(test) }
   }

   suspend inline fun <reified E : Throwable> WordSpecShouldScope.`throw`(name: String, crossinline test: () -> Any?) = abort<E>(
      name,
      test)

   /* sample data */
   val empty = emptySequence<Int>()
   val single = sequenceOf(0)
   val nulls = sequenceOf<Int?>(null, null, null, null)
   val sparse = sequenceOf(null, null, null, 3)
   val countup = (0..10).asSequence()
   val countdown = (10 downTo 0).asSequence()
   val unique = sequenceOf(3, 2, 1)
   val repeating = sequenceOf(1, 2, 3, 1, 2, 3)

   val asc = { a: Int, b: Int -> a - b }
   val desc = { a: Int, b: Int -> b - a }

   /* tests */
   init {
      "empty" should {
         succeed("for empty") {
            empty.shouldBeEmpty()
         }

         fail("for single") {
            single.shouldBeEmpty()
         }

         fail("for nulls") {
            nulls.shouldBeEmpty()
         }
      }

      "non-empty" should {
         fail("for empty") {
            empty.shouldNotBeEmpty()
         }

         succeed("for single") {
            single.shouldNotBeEmpty()
         }

         succeed("for multiple") {
            nulls.shouldNotBeEmpty()
         }
      }

      /* count */
      "have count" should {
         succeed("for empty when 0") {
            empty.shouldHaveCount(0)
         }

         fail("for empty when non-zero") {
            empty.shouldHaveCount(1)
         }

         succeed("for single when 1") {
            single.shouldHaveCount(1)
         }

         fail("for single when 0") {
            single.shouldHaveCount(0)
         }

         "match count() for multiple" {
            sparse.shouldHaveCount(sparse.count())
         }

         fail("to mis-match count() for multiple") {
            sparse.shouldHaveCount(sparse.count() - 1)
         }
      }

      "not have count" should {
         fail("for empty when non-zero") {
            empty.shouldNotHaveCount(0)
         }

         succeed("for empty when non-zero") {
            empty.shouldNotHaveCount(1)
         }

         fail("for single when 1") {
            single.shouldNotHaveCount(1)
         }

         succeed("for single when 0") {
            single.shouldNotHaveCount(0)
         }

         fail("to match count() for multiple") {
            sparse.shouldNotHaveCount(sparse.count())
         }

         "mis-match count() for multiple" {
            sparse.shouldNotHaveCount(sparse.count() - 1)
         }
      }

      "larger than" should {
         fail("for empty") {
            empty.shouldBeLargerThan(single)
         }

         succeed("with empty") {
            single.shouldBeLargerThan(empty)
         }

         fail("for smaller") {
            nulls.shouldBeLargerThan(countup)
         }

         fail("for same count") {
            countup.shouldBeLargerThan(countdown)
         }

         succeed("for larger") {
            countup.shouldBeLargerThan(nulls)
         }
      }

      "smaller than" should {
         succeed("for empty") {
            empty.shouldBeSmallerThan(single)
         }

         fail("with empty") {
            single.shouldBeSmallerThan(empty)
         }

         succeed("for smaller") {
            nulls.shouldBeSmallerThan(countup)
         }

         fail("for same count") {
            countup.shouldBeSmallerThan(countdown)
         }

         fail("for larger") {
            countup.shouldBeSmallerThan(nulls)
         }
      }

      "same count" should {
         fail("for empty with any") {
            empty.shouldBeSameCountAs(single)
         }

         fail("for any with empty") {
            nulls.shouldBeSameCountAs(empty)
         }

         fail("for smaller") {
            nulls.shouldBeSameCountAs(countup)
         }

         succeed("with same count") {
            countup.shouldBeSameCountAs(countdown)
         }

         fail("for larger") {
            countup.shouldBeSmallerThan(nulls)
         }
      }

      "at least count" should {
         succeed("for empty with -1") {
            empty.shouldHaveAtLeastCount(-1)
         }

         succeed("for any with -1") {
            countup.shouldHaveAtLeastCount(-1)
         }

         succeed("for empty with 0") {
            empty.shouldHaveAtLeastCount(0)
         }

         fail("for empty with 1") {
            empty.shouldHaveAtLeastCount(1)
         }

         succeed("for smaller count") {
            single.shouldHaveAtLeastCount(0)
         }

         succeed("for same count") {
            nulls.shouldHaveAtLeastCount(nulls.count())
         }

         fail("for larger count") {
            countup.shouldHaveAtLeastCount(countup.count() + 1)
         }
      }

      "at most count" should {
         fail("for empty with -1") {
            empty.shouldHaveAtMostCount(-1)
         }

         succeed("for empty with 0") {
            empty.shouldHaveAtMostCount(0)
         }

         succeed("for empty with 1") {
            empty.shouldHaveAtMostCount(1)
         }

         fail("for smaller count") {
            countup.shouldHaveAtMostCount(countup.count() - 1)
         }

         succeed("for same count") {
            countup.shouldHaveAtMostCount(countup.count())
         }

         succeed("for larger count") {
            countup.shouldHaveAtMostCount(countup.count() + 1)
         }
      }


      /* contain */
      /** null */
      "contain only nulls" should {
         succeed("for empty") {
            empty.shouldContainOnlyNulls()
         }

         fail("for single") {
            single.shouldContainOnlyNulls()
         }

         succeed("for nulls") {
            nulls.shouldContainOnlyNulls()
         }

         fail("for sparse") {
            sparse.shouldContainOnlyNulls()
         }
      }

      "not contain only nulls" should {
         fail("for empty") {
            empty.shouldNotContainOnlyNulls()
         }

         "fail for single" {
            single.shouldNotContainOnlyNulls()
         }

         fail("for nulls") {
            nulls.shouldNotContainOnlyNulls()
         }

         succeed("for sparse") {
            sparse.shouldNotContainOnlyNulls()
         }
      }

      "contain a null" should {
         fail("for empty") {
            empty.shouldContainNull()
         }

         fail("for non-nulls") {
            single.shouldContainNull()
         }

         succeed("for nulls") {
            nulls.shouldContainNull()
         }

         succeed("for sparse") {
            sparse.shouldContainNull()
         }
      }

      "not contain a null" should {
         succeed("for empty") {
            empty.shouldNotContainNull()
         }

         succeed("for non-nulls") {
            single.shouldNotContainNull()
         }

         fail("for nulls") {
            nulls.shouldNotContainNull()
         }

         fail("for sparse") {
            sparse.shouldNotContainNull()
         }
      }

      "contain no nulls" should {
         succeed("for empty") {
            empty.shouldContainNoNulls()
         }

         succeed("for non-nulls") {
            single.shouldContainNoNulls()
         }

         fail("for nulls") {
            nulls.shouldContainNoNulls()
         }

         fail("for sparse") {
            sparse.shouldContainNoNulls()
         }
      }

      "not contain no nulls" should {
         fail("for empty") {
            empty.shouldNotContainNoNulls()
         }

         fail("for non-nulls") {
            single.shouldNotContainNoNulls()
         }

         succeed("for nulls") {
            nulls.shouldNotContainNoNulls()
         }

         succeed("for sparse") {
            sparse.shouldNotContainNoNulls()
         }
      }

      /** single-value */
      "single element" should {
         fail("for empty") {
            empty.shouldHaveSingleElement(null)
         }

         succeed("for single") {
            single.shouldHaveSingleElement(single.first())
         }

         fail("for multiple") {
            nulls.shouldHaveSingleElement(null)
         }
      }

      "have element at" should {
         abort<IndexOutOfBoundsException>("for empty") {
            empty.shouldHaveElementAt(empty.count(), 0)
         }

         abort<IndexOutOfBoundsException>("when an element after the end is requested") {
            nulls.shouldHaveElementAt(nulls.count(), 0)
         }

         succeed("when the sequence has the element") {
            countup.shouldHaveElementAt(10, 10)
         }

         fail("when the sequence doesn't have the element") {
            countdown.shouldHaveElementAt(10, 10)
         }
      }

      "not have element at" should {
         abort<IndexOutOfBoundsException>("for empty") {
            empty.shouldNotHaveElementAt(empty.count(), 0)
         }

         abort<IndexOutOfBoundsException>("when an element after the end is requested") {
            nulls.shouldNotHaveElementAt(nulls.count(), 0)
         }

         fail("when the sequence has the element") {
            countup.shouldNotHaveElementAt(10, 10)
         }

         succeed("when the sequence doesn't have the element") {
            countdown.shouldNotHaveElementAt(10, 10)
         }
      }

      "contain" should {
         fail("for empty") {
            empty.shouldContain(0)
         }

         succeed("when the sequence contains the value") {
            countup.shouldContain(2)
         }

         fail("when the sequence doesn't contain the value") {
            sparse.shouldContain(2)
         }
      }

      "not contain" should {
         succeed("for empty") {
            empty.shouldNotContain(0)
         }

         fail("when the sequence contains the value") {
            countup.shouldNotContain(2)
         }

         succeed("when the sequence doesn't contain the value") {
            sparse.shouldNotContain(2)
         }
      }

      "exist" should {
         fail("for empty") {
            empty.shouldExist { true }
         }

         succeed("when always true") {
            single.shouldExist { true }
         }

         fail("when always false") {
            countup.shouldExist { false }
         }

         succeed("when matches at least one") {
            countdown.shouldExist { it % 5 == 4 }
         }

         fail("when matches none") {
            countdown.shouldExist { it > 20 }
         }
      }

      /** multiple-value */
      "contain all" should {
         succeed("for empty with empty") {
            empty.shouldContainAll(empty)
         }

         succeed("for empty with empty (variadic)") {
            empty.shouldContainAll()
         }

         fail("for empty with any other") {
            empty.shouldContainAll(single)
         }

         succeed("for any with empty") {
            single.shouldContainAll(empty)
         }

         succeed("for any with empty (variadic)") {
            single.shouldContainAll()
         }

         succeed("for subset of nulls") {
            sparse.shouldContainAll(nulls)
         }

         succeed("for subset of nulls (variadic)") {
            sparse.shouldContainAll(null, null)
         }

         succeed("for subset in order (variadic)") {
            countdown.shouldContainAll(2, 3, 5, 7)
         }

         succeed("for subset not in order (variadic)") {
            countdown.shouldContainAll(2, 5, 3, 7)
         }

         succeed("for same elements") {
            repeating.shouldContainAll(unique)
         }

         succeed("for same elements (variadic)") {
            repeating.shouldContainAll(2, 3, 1)
         }

         succeed("for same elements, repeated") {
            unique.shouldContainAll(repeating)
         }

         succeed("for same elements, repeated (variadic)") {
            unique.shouldContainAll(1, 2, 3, 1, 2, 3)
         }
      }

      "contain exactly empty" should {
         succeed("for empty") {
            empty.shouldContainExactly(sequenceOf<Int>())
         }

         succeed("for empty (variadic)") {
            empty.shouldContainExactly()
         }

         fail("for single") {
            single.shouldContainExactly(empty)
         }

         "fail for single (variadic)" {
            shouldThrowAny {
               single.shouldContainExactly()
            }
         }

         fail("for multiple") {
            nulls.shouldContainExactly(empty)
         }

         fail("for multiple (variadic)") {
            nulls.shouldContainExactly()
         }
      }

      "contain exactly non-empty" should {
         val nonempty = sparse;

         fail("for empty") {
            empty.shouldContainExactly(nonempty)
         }

         fail("for empty (variadic)") {
            empty.shouldContainExactly(*nonempty.toList().toTypedArray())
         }

         succeed("for same") {
            sparse.shouldContainExactly(nonempty)
         }

         succeed("for same (variadic)") {
            sparse.shouldContainExactly(*sparse.toList().toTypedArray())
         }

         fail("for another of different size") {
            countup.shouldContainExactly(nonempty)
         }

         fail("for another of different size (variadic)") {
            countup.shouldContainExactly(*nonempty.toList().toTypedArray())
         }

         fail("for another of same size") {
            nulls.shouldContainExactly(nonempty)
         }

         fail("for another of same size (variadic)") {
            nulls.shouldContainExactly(*nonempty.toList().toTypedArray())
         }

         fail("for same elements but different order") {
            repeating.shouldContainExactly(unique + unique)
         }

         fail("for same elements but different order (variadic)") {
            repeating.shouldContainExactly(1, 1, 2, 2, 3, 3)
         }
      }

      "not contain exactly empty" should {
         fail("for empty") {
            empty.shouldNotContainExactly(sequenceOf<Int>())
         }

         succeed("for single") {
            single.shouldNotContainExactly(empty)
         }

         succeed("for multiple") {
            nulls.shouldNotContainExactly(empty)
         }
      }

      "not contain exactly non-empty" should {
         val nonempty = sparse;

         succeed("for empty") {
            empty.shouldNotContainExactly(nonempty)
         }

         fail("for same") {
            sparse.shouldNotContainExactly(nonempty)
         }

         succeed("for another of different size") {
            countup.shouldNotContainExactly(nonempty)
         }

         succeed("for another of same size") {
            nulls.shouldNotContainExactly(nonempty)
         }

         succeed("for same elements but different order") {
            repeating.shouldNotContainExactly(unique + unique)
         }

         succeed("for same elements but different order (variadic)") {
            repeating.shouldNotContainExactly(1, 1, 2, 2, 3, 3)
         }
      }

      "contain in any order" should {
         succeed("for empty with empty") {
            empty.shouldContainAllInAnyOrder(empty)
         }

         fail("for empty with any other") {
            empty.shouldContainAllInAnyOrder(nulls)
         }

         succeed("when elements are same") {
            countdown.shouldContainAllInAnyOrder(countup)
         }

         fail("for overlapping sequence") {
            countup.shouldContainAllInAnyOrder((5..15).asSequence())
         }

         succeed("for subset, same count with nulls") {
            sparse.shouldContainAllInAnyOrder(nulls)
         }

         succeed("for subset, same count") {
            repeating.shouldContainAllInAnyOrder(unique + unique)
         }

         succeed("for subset, same count (variadic)") {
            repeating.shouldContainAllInAnyOrder(1, 1, 2, 2, 3, 3)
         }

         fail("for subset, different count with nulls") {
            sparse.shouldContainAllInAnyOrder(sparse.toSet().asSequence())
         }

         fail("for same, different count") {
            repeating.shouldContainAllInAnyOrder(unique)
         }
      }

      "not contain in any order" should {
         fail("for empty with empty") {
            empty.shouldNotContainAllInAnyOrder(empty)
         }

         succeed("for empty with any other") {
            empty.shouldNotContainAllInAnyOrder(nulls)
         }

         fail("when elements are same") {
            countdown.shouldNotContainAllInAnyOrder(countup)
         }

         succeed("for overlapping sequence") {
            countup.shouldNotContainAllInAnyOrder((5..15).asSequence())
         }

         fail("for subset, same count with nulls") {
            sparse.shouldNotContainAllInAnyOrder(nulls)
         }

         fail("for subset, same count") {
            repeating.shouldNotContainAllInAnyOrder(unique + unique)
         }

         fail("for subset, same count (variadic)") {
            repeating.shouldNotContainAllInAnyOrder(1, 1, 2, 2, 3, 3)
         }

         succeed("for subset, different count with nulls") {
            sparse.shouldNotContainAllInAnyOrder(sparse.toSet().asSequence())
         }

         succeed("for same, different count") {
            repeating.shouldNotContainAllInAnyOrder(unique)
         }
      }

      "contain in order" should {

         "with empty" {
            shouldThrowAny {
               countup.shouldContainInOrder(empty)
            }
         }

         fail("with empty (variadic)") {
            countup.shouldContainInOrder()
         }

         fail("for overlapping sequence") {
            countup.shouldContainInOrder((5..15).asSequence())
         }

         fail("for overlapping sequence (variadic)") {
            countup.shouldContainInOrder(*(5..15).toList().toTypedArray())
         }

         succeed("for subset in order") {
            countup.shouldContainInOrder(sequenceOf(2, 3, 5, 7))
         }

         succeed("for subset in order (variadic)") {
            countup.shouldContainInOrder(2, 3, 5, 7)
         }

         succeed("for subset in order with repeats") {
            repeating.shouldContainInOrder(sequenceOf(1, 3, 1, 2))
         }

         succeed("for subset in order with repeats (variadic)") {
            repeating.shouldContainInOrder(1, 3, 1, 2)
         }

         fail("for subset in order with too many repeats") {
            repeating.shouldContainInOrder(sequenceOf(1, 3, 1, 2, 2))
         }

         fail("for subset in order with too many repeats (variadic)") {
            repeating.shouldContainInOrder(1, 3, 1, 2, 2)
         }

         fail("for subset not in order") {
            countup.shouldContainInOrder(sequenceOf(2, 5, 3, 7))
         }

         fail("for subset not in order (variadic)") {
            countup.shouldContainInOrder(2, 5, 3, 7)
         }
      }


      /** unique */
      "unique" should {
         succeed("for empty") {
            empty.shouldBeUnique()
         }

         succeed("for single") {
            single.shouldBeUnique()
         }

         fail("with repeated nulls") {
            sparse.shouldBeUnique()
         }

         fail("with repeats") {
            repeating.shouldBeUnique()
         }

         succeed("for multiple unique") {
            countup.shouldBeUnique()
         }
      }

      "not unique" should {
         fail("for empty") {
            empty.shouldNotBeUnique()
         }

         fail("for single") {
            single.shouldNotBeUnique()
         }

         succeed("with repeated nulls") {
            sparse.shouldNotBeUnique()
         }

         succeed("with repeats") {
            repeating.shouldNotBeUnique()
         }

         fail("for multiple unique") {
            countup.shouldNotBeUnique()
         }
      }

      "duplicates" should {
         fail("for empty") {
            empty.shouldContainDuplicates()
         }

         fail("for single") {
            single.shouldContainDuplicates()
         }

         succeed("with repeated nulls") {
            sparse.shouldContainDuplicates()
         }

         succeed("with repeats") {
            repeating.shouldNotBeUnique()
         }

         fail("for multiple unique") {
            countup.shouldContainDuplicates()
         }
      }

      /* comparable */
      /** bound */
      "have an upper bound" should {
         succeed("for empty") {
            empty.shouldHaveUpperBound(Int.MIN_VALUE)
         }

         succeed("for single") {
            single.shouldHaveUpperBound(0)
         }

         fail("for single with wrong bound") {
            single.shouldHaveUpperBound(-1)
         }

         succeed("for multiple") {
            countup.shouldHaveUpperBound(countup.max() ?: Int.MAX_VALUE)
         }

         fail("for multiple with wrong bound") {
            countup.shouldHaveUpperBound((countup.max() ?: Int.MAX_VALUE) - 1)
         }
      }

      "have a lower bound" should {
         succeed("for empty") {
            empty.shouldHaveLowerBound(Int.MAX_VALUE)
         }

         succeed("for single") {
            single.shouldHaveLowerBound(0)
         }

         fail("for single with wrong bound") {
            single.shouldHaveLowerBound(1)
         }

         succeed("for multiple") {
            countup.shouldHaveLowerBound(countup.min() ?: Int.MIN_VALUE)
         }

         fail("for multiple with wrong bound") {
            countup.shouldHaveLowerBound((countup.min() ?: Int.MIN_VALUE) + 1)
         }
      }


      /** sort */
      "sorted" should {
         succeed("for empty") {
            empty.shouldBeSorted()
         }

         succeed("for single") {
            single.shouldBeSorted()
         }

         fail("for repeating") {
            repeating.shouldBeSorted()
         }

         succeed("for count-up") {
            countup.shouldBeSorted()
         }

         fail("for count-down") {
            countdown.shouldBeSorted()
         }
      }

      "not sorted" should {
         fail("for empty") {
            empty.shouldNotBeSorted()
         }

         fail("for single") {
            single.shouldNotBeSorted()
         }

         succeed("for repeating") {
            repeating.shouldNotBeSorted()
         }

         fail("for count-up") {
            countup.shouldNotBeSorted()
         }

         succeed("for count-down") {
            countdown.shouldNotBeSorted()
         }
      }

      "sorted ascending" should {
         val dir = asc

         succeed("for empty") {
            empty.shouldBeSortedWith(dir)
         }

         succeed("for single") {
            single.shouldBeSortedWith(dir)
         }

         fail("for repeating") {
            repeating.shouldBeSortedWith(dir)
         }

         succeed("for count-up") {
            countup.shouldBeSortedWith(dir)
         }

         fail("for count-down") {
            countdown.shouldBeSortedWith(dir)
         }

      }

      "sorted descending" should {
         val dir = desc

         succeed("for empty") {
            empty.shouldBeSortedWith(dir)
         }

         succeed("for single") {
            single.shouldBeSortedWith(dir)
         }

         fail("for repeating") {
            repeating.shouldBeSortedWith(dir)
         }

         fail("for count-up") {
            countup.shouldBeSortedWith(dir)
         }

         succeed("for count-down") {
            countdown.shouldBeSortedWith(dir)
         }
      }

      "not sorted ascending" should {
         val dir = asc

         fail("for empty") {
            empty.shouldNotBeSortedWith(dir)
         }

         fail("for single") {
            single.shouldNotBeSortedWith(dir)
         }

         succeed("for repeating") {
            repeating.shouldNotBeSortedWith(dir)
         }

         fail("for count-up") {
            countup.shouldNotBeSortedWith(dir)
         }

         succeed("for count-down") {
            countdown.shouldNotBeSortedWith(dir)
         }
      }

      "not sorted descending" should {
         val dir = desc

         fail("for empty") {
            empty.shouldNotBeSortedWith(dir)
         }

         fail("for single") {
            single.shouldNotBeSortedWith(dir)
         }

         succeed("for repeating") {
            repeating.shouldNotBeSortedWith(dir)
         }

         succeed("for count-up") {
            countup.shouldNotBeSortedWith(dir)
         }

         fail("for count-down") {
            countdown.shouldNotBeSortedWith(dir)
         }
      }
   }
}
