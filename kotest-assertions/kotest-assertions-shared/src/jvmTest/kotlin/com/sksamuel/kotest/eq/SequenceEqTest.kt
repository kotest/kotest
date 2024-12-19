package com.sksamuel.kotest.eq

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.eq.SequenceEq
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class SequenceEqTest : FunSpec({

   test("built-in sequence equality is reflexive but not symmetric") {

      val seq1 = sequenceOf(1, 2, 3)

      // reflexive

      seq1.equals(seq1) shouldBe true
      (seq1.hashCode() == seq1.hashCode()) shouldBe true

      // symmetric

      val seq2 = sequenceOf(1, 2, 3)

      seq1.equals(seq2) shouldBe /* true, but is */ false
      (seq1.hashCode() == seq2.hashCode()) shouldBe /* true, but is */ false

      seq2.equals(seq1) shouldBe /* true, but is */ false
      (seq2.hashCode() == seq1.hashCode()) shouldBe /* true, but is */ false

   }

   // therefore...

   test("Sequence type is not supported") {
      val error = SequenceEq.equals(sequenceOf(1, 2, 3), sequenceOf(2, 3))

      assertSoftly {
         error.shouldNotBeNull()
         error.message?.startsWith("Sequence type is not supported") shouldBe true
      }
   }

})
