package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
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
