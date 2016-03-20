package com.sksamuel.kotlintest

import kotlin.reflect.KClass

class TypeMatchers(val any: Any) {
  public infix fun a(expected: KClass<*>): Unit = an(expected)
  public infix fun an(expected: KClass<*>) {
    if (!expected.java.isAssignableFrom(any.javaClass)) {
      throw TestFailedException("Value is not of type $expected")
    }
  }
}