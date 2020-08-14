package com.sksamuel.kotest.focusbang

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec

class BangDisableFun : FunSpec({
   test("!should not run") {
      fail("boom")
   }
})

class BangDisableString : StringSpec({
   "!should not run" {
      fail("boom")
   }
})

class BangDisableWord : WordSpec({
   "using the bang symbol" should {
      "!disable this test" {
         fail("boom")
      }
   }
})
