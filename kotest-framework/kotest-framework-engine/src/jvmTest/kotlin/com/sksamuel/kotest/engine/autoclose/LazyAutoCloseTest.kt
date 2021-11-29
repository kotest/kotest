package com.sksamuel.kotest.engine.autoclose

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class LazyAutoCloseTest : StringSpec({

   var fullLazy = true
   var closed = false
   val closeme = AutoCloseable { closed = true }

   autoClose(lazy {
      fullLazy = false
      closeme
   })

   "autoClose lazy should be lazy" {
      fullLazy.shouldBeTrue()
      closed.shouldBeFalse()
   }

   afterProject {
      // If never used in the Spec it should never get initialised, or closed
      fullLazy.shouldBeTrue()
      closed.shouldBeFalse()
   }
})
