package com.sksamuel.kotest.matchers.tuples

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.tuples.shouldHaveFirst
import io.kotest.matchers.tuples.shouldHaveSecond
import io.kotest.matchers.tuples.shouldNotHaveFirst
import io.kotest.matchers.tuples.shouldNotHaveSecond

class PairTest : FunSpec() {
   init {

      test("pair should have first") {
         Pair(1, 2).shouldHaveFirst(1)
         Pair(1, 2).shouldNotHaveFirst(2)
         shouldFail {
            Pair(1, 2).shouldNotHaveFirst(1)
         }.message shouldBe "Pair (1, 2) should not have first value 1"
      }

      test("pair should have second") {
         Pair(1, 2).shouldHaveSecond(2)
         Pair(1, 2).shouldNotHaveSecond(1)
         shouldFail {
            Pair(1, 2).shouldNotHaveSecond(2)
         }.message shouldBe "Pair (1, 2) should not have second value 2"
      }
   }
}
