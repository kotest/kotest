package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe

private var str = ""

private val factory = funSpec {

   beforeTest {
      str += "before-"
   }

   afterTest {
      str += "after-"
   }

   test("a") {
      str shouldBe "before-"
      str += "a-"
   }

   test("b") {
      str shouldBe "before-a-after-before-"
      str += "b-"
   }
}

class TestFactoryCallbackTest : FunSpec() {
   init {
      include(factory)
      test("this should not trigger the before/after callbacks on the factory") {}
      afterSpec {
         str shouldBe "before-a-after-before-b-after-"
      }
   }
}
