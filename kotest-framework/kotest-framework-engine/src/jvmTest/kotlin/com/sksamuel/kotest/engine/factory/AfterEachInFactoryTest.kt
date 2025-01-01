package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specAfterEach = mutableListOf<String>()
var factoryAfterEach = mutableListOf<String>()

private val factory = funSpec {

   afterEach {
      factoryAfterEach.add(it.a.name.name)
   }

   test("a") {}
   test("b") {}

   context("factory") {
      test("c") { }
      test("d") { }
   }
}

class AfterEachInFactoryTest : FunSpec({

   afterEach {
      specAfterEach.add(it.a.name.name)
   }

   afterSpec {
      specAfterEach.shouldContainExactly(listOf("a", "b", "c", "d", "e", "f", "g", "h"))
      factoryAfterEach.shouldContainExactly(listOf("a", "b", "c", "d"))
   }

   include(factory)

   test("e") { }
   test("f") { }

   context("root") {
      test("g") { }
      test("h") { }
   }

})
