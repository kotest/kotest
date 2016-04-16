package io.kotlintest

import io.kotlintest.specs.WordSpec

class WordSpecTest : WordSpec() {
  init {
    "ListStack.pop" should {
      "remove the last element from stack" {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.size() shouldBe 2
        stack.pop() shouldBe "world"
        stack.size() shouldBe 1
      }
    }
    "ListStack.peek" should {
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