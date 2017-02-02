package io.kotlintest.specs

import io.kotlintest.ListStack
import io.kotlintest.matchers.shouldBe

class BehaviorSpecTest : BehaviorSpec() {
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