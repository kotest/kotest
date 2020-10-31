package com.sksamuel.kotest.concurrency.test

import io.kotest.core.spec.Order
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

private var results = ""

/*
   the tests in this file should run strictly in order because each spec should be submitted sequentially
   since we are using ConcurrencyMode.Test
 */

@Order(1)
class Spec1 : FunSpec() {

   init {

      afterProject {
         results shouldBe "abc"
      }

      test("foo") {
         delay(100) // delay here to ensure that if sequentiality was not being honoured, "b" and "c" would run first
         results += "a"
      }
   }
}

@Order(2)
class Spec2 : FunSpec() {
   init {
      test("foo") {
         results += "b"
      }
   }
}

@Order(3)
class Spec3 : FunSpec() {
   init {
      test("foo") {
         results += "c"
      }
   }
}
