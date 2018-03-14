package io.kotlintest.specs

import io.kotlintest.ListStack
import io.kotlintest.matchers.shouldBe

class ShouldSpecTest : ShouldSpec() {

  init {
    // should allow multi nested
    "List" {
      "pop" {
        should("remove the last element from stack") {
          val stack = ListStack<String>()
          stack.push("hello")
          stack.push("world")
          stack.size() shouldBe 2
          stack.pop() shouldBe "world"
          stack.size() shouldBe 1
        }
      }
      should("remove the last element from stack") {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.size() shouldBe 2
        stack.pop() shouldBe "world"
        stack.size() shouldBe 1
      }
    }

    // should allow nested
    "List.pop" {
      should("remove the last element from stack") {
        val stack = ListStack<String>()
        stack.push("hello")
        stack.push("world")
        stack.size() shouldBe 2
        stack.pop() shouldBe "world"
        stack.size() shouldBe 1
      }
    }

    // and un-nested
    should("leave the stack unmodified") {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.size() shouldBe 2
      stack.peek() shouldBe "world"
      stack.size() shouldBe 2
    }

    should("support config") {
    }.config(invocations = 5)
  }
}