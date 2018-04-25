package com.sksamuel.kotlintest.tests.specs

import com.sksamuel.kotlintest.tests.ListStack
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FunSpecTest : FunSpec() {

  init {

    test("ListStack.pop should remove the last element from stack") {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.size() shouldBe 2
      stack.pop() shouldBe "world"
      stack.size() shouldBe 1
    }

    test("ListStack.peek should leave the stack unmodified") {
      val stack = ListStack<String>()
      stack.push("hello")
      stack.push("world")
      stack.size() shouldBe 2
      stack.peek() shouldBe "world"
      stack.size() shouldBe 2
    }

    var count = 0

    test("FunSpec should support config syntax").config(invocations = 5) {
      count += 1
    }

    count shouldBe 5
  }
}