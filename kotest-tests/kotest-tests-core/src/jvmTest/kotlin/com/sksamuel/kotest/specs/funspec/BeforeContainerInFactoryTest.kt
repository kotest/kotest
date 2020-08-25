package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specBeforeContainer = mutableListOf<String>()
var factoryBeforeContainer = mutableListOf<String>()

private val factory = funSpec {
   beforeContainer {
      factoryBeforeContainer.add(it.displayName)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class BeforeContainerInFactoryTest : FunSpec({

   beforeContainer {
      specBeforeContainer.add(it.displayName)
   }

   afterSpec {
      specBeforeContainer.shouldContainExactly(listOf("factory", "root"))
      factoryBeforeContainer.shouldContainExactly(listOf("factory"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
