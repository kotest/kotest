package com.sksamuel.kotest.engine.autoclose

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class AutoCloseDslTest : FunSpec({

   var beforeTests = true
   var closed = false
   val closeme = AutoCloseable { closed = true }

   autoClose(lazy {
      beforeTests = false
      closeme
   })

   beforeTests.shouldBeTrue()

   test("auto close with dsl method") {
      closed.shouldBeFalse()
   }

   afterProject {
      closed.shouldBeTrue()
   }
})
