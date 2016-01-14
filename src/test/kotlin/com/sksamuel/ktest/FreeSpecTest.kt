package com.sksamuel.ktest

import org.junit.runner.RunWith

class FreeSpecTest : Matchers, FreeSpec() {
  init {
    "given a ListStack" - {
      "pop" - {
        "should remove the last element from stack" with {
          val stack = ListStack<String>()
          stack.push("hello")
          stack.push("world")
          stack.size() shouldBe 2
          stack.pop() shouldBe "world"
          stack.size() shouldBe 1
        }
      }
      "peek" - {
        "should leave the stack unmodified" with {
          val stack = ListStack<String>()
          stack.push("hello")
          stack.push("world")
          stack.size() shouldBe 2
          stack.peek() shouldBe "world"
          stack.size() shouldBe 2
        }
      }
    }
  }
}