package com.sksamuel.kotest.engine.active

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec

class BangDisableFunSpec : FunSpec({
   test("!should not run") {
      fail("boom")
   }
})

class BangDisableStringString : StringSpec({
   "!should not run" {
      fail("boom")
   }
})

class BangDisableShouldSpec : ShouldSpec({
   should("!should not run") {
      fail("boom")
   }
})
