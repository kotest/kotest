package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specBeforeTest = mutableListOf<String>()
var factoryBeforeTest = mutableListOf<String>()

private val factory = funSpec {
   beforeTest {
      factoryBeforeTest.add(it.displayName)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class BeforeTestInFactoryTest : FunSpec({

   beforeTest {
      specBeforeTest.add(it.displayName)
   }

   afterSpec {
      specBeforeTest.shouldContainExactly(listOf("factory", "a", "b", "root", "c", "d"))
      factoryBeforeTest.shouldContainExactly(listOf("factory", "a", "b"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
