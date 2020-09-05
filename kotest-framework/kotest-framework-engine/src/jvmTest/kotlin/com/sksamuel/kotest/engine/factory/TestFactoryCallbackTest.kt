package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

private val factory = funSpec {

   var before = false
   var after = false

   beforeTest {
      before = true
   }

   afterTest {
      after = true
   }

   test("checking that before was called and after was not yet") {
      before.shouldBeTrue()
      after.shouldBeFalse()
   }

   test("checking that after was called") {
      after.shouldBeTrue()
   }
}

class TestFactoryCallbackTest : FunSpec() {
   init {
      include(factory)
   }
}
