package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specAfterEach = mutableListOf<String>()
var factoryAfterEach = mutableListOf<String>()

private val factory = funSpec {
   afterEach {
      factoryAfterEach.add(it.a.displayName)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class AfterEachInFactoryTest : FunSpec({

   afterEach {
      specAfterEach.add(it.a.displayName)
   }

   afterSpec {
      specAfterEach.shouldContainExactly(listOf("a", "b", "c", "d"))
      factoryAfterEach.shouldContainExactly(listOf("a", "b"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
