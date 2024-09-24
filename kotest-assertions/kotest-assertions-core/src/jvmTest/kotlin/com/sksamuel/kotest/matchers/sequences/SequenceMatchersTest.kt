package com.sksamuel.kotest.matchers.sequences

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.sequences.shouldHaveSingleElement
import io.kotest.matchers.sequences.shouldNotBeEmpty
import io.kotest.matchers.sequences.shouldNotContainExactly
import io.kotest.matchers.sequences.shouldNotHaveSingleElement
import io.kotest.matchers.shouldBe

class SequenceMatchersTest : StringSpec({

   "beEmpty consumes at most 1 element" {
      val atMostOne = sequence { yield(1); throw Exception("Should not consume a second element") }
      atMostOne.shouldNotBeEmpty()
   }

   "contain exactly" {
      sequenceOf(1, 2, 3).shouldContainExactly(1, 2, 3)
      sequenceOf(1).shouldContainExactly(1)
      emptySequence<Any>().shouldContainExactly()

      shouldThrow<AssertionError> {
         sequenceOf(1, 2, 3).shouldContainExactly(1, 3, 5)
      }.message shouldBe """
         Sequence should contain exactly 1, 3, ... but was 1, 2, ....
         Expected 3 at index 1 but found 2.""".trimIndent()

      shouldThrow<AssertionError> {
         sequenceOf(1, 2, 3).shouldContainExactly(1, 2, 3, 4)
      }.message shouldBe """
         Sequence should contain exactly 1, 2, 3, 4 but was 1, 2, 3.
         Actual sequence has less elements than expected sequence: expected 4 at index 3.""".trimIndent()

      shouldThrow<AssertionError> {
         sequenceOf(1, 2, 3).shouldContainExactly(1, 2)
      }.message shouldBe """
         Sequence should contain exactly 1, 2 but was 1, 2, 3.
         Actual sequence has more elements than expected sequence: found 3 at index 2.""".trimIndent()

      shouldThrow<AssertionError> {
         generateSequence(1) { it + 1 }.shouldContainExactly(1, 2, 3)
      }.message shouldBe """
         Sequence should contain exactly 1, 2, 3 but was 1, 2, 3, 4, ....
         Actual sequence has more elements than expected sequence: found 4 at index 3.""".trimIndent()

      shouldThrow<AssertionError> {
         sequenceOf(1, 2, 3).shouldNotContainExactly(1, 2, 3)
      }.message shouldBe "Sequence should not contain exactly 1, 2, 3"
   }

   "single element" {
      sequenceOf(1).shouldHaveSingleElement(1)

      shouldThrow<AssertionError> {
         emptySequence<Int>().shouldHaveSingleElement(1)
      }.message shouldBe "Sequence should have a single element of 1 but is empty."

      shouldThrow<AssertionError> {
         sequenceOf(2).shouldHaveSingleElement(1)
      }.message shouldBe "Sequence should have a single element of 1 but has 2 as first element."

      shouldThrow<AssertionError> {
         sequenceOf(1, 2).shouldHaveSingleElement(1)
      }.message shouldBe "Sequence should have a single element of 1 but has more than one element."

      shouldThrow<AssertionError> {
         generateSequence(1) { it + 1 }.shouldHaveSingleElement(1)
      }.message shouldBe "Sequence should have a single element of 1 but has more than one element."

      shouldThrow<AssertionError> {
         sequenceOf(1).shouldNotHaveSingleElement(1)
      }.message shouldBe "Sequence should not have a single element of 1."
   }

   "should be empty" {
      shouldThrowAny {
         sequenceOf(0).shouldBeEmpty()
      }.message shouldBe "Sequence should be empty"

      shouldThrowAny {
         sequenceOf<Int?>(null, null, null, null).shouldBeEmpty()
      }.message shouldBe "Sequence should be empty"

      emptySequence<Int>().shouldBeEmpty()
   }

   "should not be empty" {
      shouldThrowAny {
         emptySequence<Int>().shouldNotBeEmpty()
      }.message shouldBe "Sequence should not be empty"

      sequenceOf(0).shouldNotBeEmpty()

      sequenceOf(1, 2, 3).shouldNotBeEmpty()
   }

})
