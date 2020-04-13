package com.sksamuel.kotest.focusbang

import io.kotest.assertions.fail
import io.kotest.core.spec.CompositeSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.core.spec.style.wordSpec

val bangDisableFun = funSpec {
   test("!should not run") {
      fail("boom")
   }
}

val bangDisableString = stringSpec {
   "!should not run" {
      fail("boom")
   }
}

val bangDisableWord = wordSpec {
   "using the bang symbol" should {
      "!disable this test" {
         fail("boom")
      }
   }
}

class BangDisableTest : CompositeSpec(
   bangDisableFun,
   bangDisableWord,
   bangDisableString
)
