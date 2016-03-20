package io.kotlintest

import io.kotlintest.specs.FlatSpec

class FlatSpecTest : FlatSpec() {
  init {
    "ListStack.pop" should "return the last element from stack" with {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.pop() shouldBe "world"
    }
    "ListStack.pop" should "remove the element from the stack" with {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.pop()
      stack.size() shouldBe 1
    }
    "ListStack.peek" should "should leave the stack unmodified" with {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.size() shouldBe 2
      stack.peek() shouldBe "world"
      stack.size() shouldBe 2
    }
  }
}