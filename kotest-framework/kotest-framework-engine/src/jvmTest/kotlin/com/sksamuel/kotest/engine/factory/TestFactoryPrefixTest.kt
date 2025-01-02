package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe

private var names = mutableListOf<String>()

class TestFactoryPrefixTest : FunSpec({
   include("wibble", factory())
   include("wobble", factory())
   afterSpec {
      names shouldBe listOf("wibble a", "wobble a")
   }
})

private fun factory() = funSpec {
   test("a") {
      names.add(this.testCase.name.name)
   }
}
