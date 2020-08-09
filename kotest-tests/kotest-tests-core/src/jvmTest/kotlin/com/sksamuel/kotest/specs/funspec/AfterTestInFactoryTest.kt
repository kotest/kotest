package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldContainExactly

var specAfterTest = mutableListOf<String>()
var factoryAfterTest = mutableListOf<String>()

private val factory = funSpec {
   afterTest {
      factoryAfterTest.add(it.a.displayName)
   }
   context("factory") {
      test("a") { }
      test("b") { }
   }
}

class AfterTestInFactoryTest : FunSpec({

   afterTest {
      specAfterTest.add(it.a.displayName)
   }

   afterSpec {
      specAfterTest.shouldContainExactly(listOf("c", "d", "root", "a", "b", "factory"))
      factoryAfterTest.shouldContainExactly(listOf("a", "b", "factory"))
   }

   include(factory)

   context("root") {
      test("c") { }
      test("d") { }
   }

})
