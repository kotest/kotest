package com.sksamuel.kotest.factory

import io.kotest.core.spec.FunSpec
import io.kotest.core.spec.funSpec
import io.kotest.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

private fun factory(ref: AtomicBoolean) = funSpec {
   test("this should becalled") {
      ref.set(true)
   }
}

class TestFactoryTest : FunSpec({
   val ref = AtomicBoolean(false)
   include(factory(ref))
   afterSpec {
      ref.get() shouldBe true
   }
})
