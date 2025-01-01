package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specAfterTest = mutableListOf<String>()
var factoryAfterTest = mutableListOf<String>()

private val factory = funSpec {
   afterTest {
      factoryAfterTest.add(it.a.name.name)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class AfterTestInFactoryTest : FunSpec({

   afterTest {
      specAfterTest.add(it.a.name.name)
   }

   afterSpec {
      specAfterTest.shouldContainExactly(listOf("a", "b", "factory", "c", "d", "root"))
      factoryAfterTest.shouldContainExactly(listOf("a", "b", "factory"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
