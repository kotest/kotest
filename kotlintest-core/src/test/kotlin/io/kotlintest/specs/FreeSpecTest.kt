package io.kotlintest.specs

import io.kotlintest.ListStack
import io.kotlintest.matchers.shouldBe

class FreeSpecTest : FreeSpec() {
  init {
    "given a ListStack" - {
      "pop" - {
        "should remove the last element from stack" {
          val stack = ListStack<String>()
          stack.push("hello")
          stack.push("world")
          stack.size() shouldBe 2
          stack.pop() shouldBe "world"
          stack.size() shouldBe 1
        }
      }
      "peek" - {
        "should leave the stack unmodified" {
          val stack = ListStack<String>()
          stack.push("hello")
          stack.push("world")
          stack.size() shouldBe 2
          stack.peek() shouldBe "world"
          stack.size() shouldBe 2
        }
      }
    }

    "params" - {
      "support config" {
      }.config(invocations = 5)
    }
  }
}