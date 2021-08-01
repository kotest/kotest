package com.sksamuel.kotest.engine.autoclose

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class AutoCloseDslTest : FunSpec({

   var beforeTests = true
   var closed = false
   val closeme = AutoCloseable { closed = true }
   autoClose(closeme)

   val lazyClosed by autoClose(lazy {
      beforeTests = false
      LazyCloseable()
   })

   beforeTests.shouldBeTrue()

   test("auto close with dsl method") {
      closed.shouldBeFalse()
   }

   test("lazy auto close with dsl method") {
      lazyClosed.closed.shouldBeFalse()
   }

   afterSpec {
      closed.shouldBeTrue()
      lazyClosed.closed.shouldBeTrue()
   }
}) {
   class LazyCloseable : AutoCloseable {
      var closed = false
      override fun close() {
         closed = true
      }
   }
}

