package com.sksamuel.kotest.engine.active

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec

class BangDisableFunSpec : FunSpec({
   test("!should not run") {
      AssertionErrorBuilder.fail("boom")
   }
})

class BangDisableStringString : StringSpec({
   "!should not run" {
      AssertionErrorBuilder.fail("boom")
   }
})

class BangDisableShouldSpec : ShouldSpec({
   should("!should not run") {
      AssertionErrorBuilder.fail("boom")
   }
})
