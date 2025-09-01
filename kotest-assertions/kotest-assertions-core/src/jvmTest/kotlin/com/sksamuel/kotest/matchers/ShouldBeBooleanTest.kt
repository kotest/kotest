package com.sksamuel.kotest.matchers

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldBeBooleanTest : FunSpec() {
   init {
      test("a shouldBe true should have comparison") {
         val a = false
         shouldThrowAny {
            a shouldBe true
         }.shouldHaveMessage("expected:<true> but was:<false>")
      }
   }
}
