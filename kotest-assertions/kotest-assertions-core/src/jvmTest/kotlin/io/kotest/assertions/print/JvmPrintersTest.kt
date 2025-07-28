package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JvmPrintersTest : FunSpec() {
   init {
      test("should allow jvm printers registrations") {
         Printers.add(Wobble::class) { value, level -> Printed("hello wobble ${value.a}") }
         PrintResolver.printFor(Wobble("foo")).print(Wobble("foo"), 2) shouldBe Printed("hello wobble foo")
      }
   }
}

data class Wobble(val a: String)
