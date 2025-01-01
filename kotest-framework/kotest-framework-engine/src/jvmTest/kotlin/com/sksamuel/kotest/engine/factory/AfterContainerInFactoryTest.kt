package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specAfterContainer = mutableListOf<String>()
var factoryAfterContainer = mutableListOf<String>()

private val factory = funSpec {
   afterContainer {
      factoryAfterContainer.add(it.a.name.name)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class AfterContainerInFactoryTest : FunSpec({

   afterContainer {
      specAfterContainer.add(it.a.name.name)
   }

   afterSpec {
      specAfterContainer.shouldContainExactly(listOf("factory", "root"))
      factoryAfterContainer.shouldContainExactly(listOf("factory"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
