package io.kotlintest

import kotlin.reflect.KClass

interface TypeMatchers {

  infix fun Be<*>.a(expected: KClass<*>): (Any) -> Unit = an(expected)

  infix fun Be<*>.an(expected: KClass<*>): (Any) -> Unit {
    return { any ->
      if (!expected.java.isAssignableFrom(any.javaClass))
        throw TestFailedException("Value is not of type $expected")
    }
  }
}