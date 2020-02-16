package com.sksamuel.kotest

import io.kotest.core.spec.autoClose
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class AutoCloseDslTest : FunSpec({

   var closed = false
   val closeme = AutoCloseable { closed = true }

   autoClose(closeme)

   test("auto close with dsl method") {
      closed.shouldBeFalse()
   }

   afterProject {
      closed.shouldBeTrue()
   }
})
