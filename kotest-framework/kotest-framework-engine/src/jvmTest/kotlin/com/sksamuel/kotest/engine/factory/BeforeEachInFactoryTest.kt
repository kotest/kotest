package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specBeforeEach = mutableListOf<String>()
var factoryBeforeEach = mutableListOf<String>()

private val factory = funSpec {
   beforeEach {
      factoryBeforeEach.add(it.name.name)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class BeforeEachInFactoryTest : FunSpec({

   beforeEach {
      specBeforeEach.add(it.name.name)
   }

   afterSpec {
      specBeforeEach.shouldContainExactly(listOf("a", "b", "c", "d"))
      factoryBeforeEach.shouldContainExactly(listOf("a", "b"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
