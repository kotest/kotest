package com.sksamuel.ktest

interface Matchers {
    infix fun Any.shouldBe(any: Any): Unit {
        if (!(this == any)) throw RuntimeException(this.toString() + " did not equal " + any)
    }
}