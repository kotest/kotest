package com.sksamuel.ktest

interface Matchers {
  infix fun Any.shouldEqual(any: Any): Unit = shouldBe(any)
  infix fun Any.shouldBe(any: Any): Unit {
    if (!(this == any)) throw TestFailedException(this.toString() + " did not equal " + any)
  }
}