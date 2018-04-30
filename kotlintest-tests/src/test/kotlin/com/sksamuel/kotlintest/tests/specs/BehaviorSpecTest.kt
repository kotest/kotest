package com.sksamuel.kotlintest.tests.specs

import com.sksamuel.kotlintest.tests.ListStack
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractBehaviorSpec

class BehaviorSpecTest : AbstractBehaviorSpec() {
  init {
    given("a ListStack") {
      `when`("pop is invoked") {
        then("the last element is removed") {
          val stack = ListStack<String>()
          stack.push("hello")
          stack.push("world")
          stack.size() shouldBe 2
          stack.pop() shouldBe "world"
          stack.size() shouldBe 1
        }
      }
    }
  }
}