package io.kotlintest.specs

import io.kotlintest.ListStack
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FlatSpecTest : FlatSpec() {

  init {

    "ListStack.pop" should "return the last element from stack" {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.pop() shouldBe "world"
    }
    "ListStack.pop" should "remove the element from the stack" {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.pop()
      stack.size() shouldBe 1
    }
    "ListStack.peek" should "should leave the stack unmodified" {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.size() shouldBe 2
      stack.peek() shouldBe "world"
      stack.size() shouldBe 2
    }
  }
}