package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

private var tests = mutableSetOf<String>()
private var specs = mutableSetOf<Int>()

class StringSpecInstancePerRootTest : StringSpec({

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

   isolationMode = IsolationMode.InstancePerRoot

   var count = 0

   "be 0" {
      count shouldBe 0
      count = 100
   }
   "be 0 part 2" {
      count shouldBe 0
      count = 100
   }
   "be 0 part 3" {
      count shouldBe 0
      count = 100
   }
   "still be 0" {
      count shouldBe 0
   }
})
