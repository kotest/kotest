package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specBeforeTest = mutableListOf<String>()
var factoryBeforeTest = mutableListOf<String>()

private val factory = funSpec {
   beforeTest {
      factoryBeforeTest.add(it.name.name)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class BeforeTestInFactoryTest : FunSpec({

   beforeTest {
      specBeforeTest.add(it.name.name)
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
