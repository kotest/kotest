package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JvmPrintersTest : FunSpec() {
   init {
      test("should allow jvm printers registrations") {
         Printers.add(Wobble::class) { Printed("hello wobble ${it.a}") }
         PrintResolver.printFor(Wobble("foo")).print(Wobble("foo")) shouldBe Printed("hello wobble foo")
      }
   }
}

data class Wobble(val a: String)
