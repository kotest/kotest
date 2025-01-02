package com.sksamuel.kotest.engine.spec.isolation.pertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class FunSpecInstancePerTestTest : FunSpec({

   afterProject {
      tests.size shouldBe 4
      specs.size shouldBe 4
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name.name)
   }

   isolationMode = IsolationMode.InstancePerTest

   var count = 0

   test("be 0") {
      count shouldBe 0
      count = 100
   }
   test("be 0 part 2") {
      count shouldBe 0
      count = 100
   }
   test("be 0 part 3") {
      count shouldBe 0
      count = 100
   }
   test("still be 0") {
      count shouldBe 0
   }
})
