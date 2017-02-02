package io.kotlintest.specs

import io.kotlintest.ListStack
import io.kotlintest.matchers.shouldBe

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
      "remove the last element from stack again" {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.size() shouldBe 2
        stack.pop() shouldBe "world"
        stack.size() shouldBe 1
      }
    }
    "ListStack.peek" should {
      "should leave the stack unmodified" {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.size() shouldBe 2
        stack.peek() shouldBe "world"
        stack.size() shouldBe 2
      }
    }
    "WordSpec" should {
      "support config syntax" {
      }.config(invocations = 3)
    }
  }
}