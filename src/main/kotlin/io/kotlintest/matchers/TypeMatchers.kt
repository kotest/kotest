package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import kotlin.reflect.KClass

interface TypeMatchers {

  infix fun Be<*>.a(expected: KClass<*>): Unit = an(expected)

  infix fun Be<*>.an(expected: KClass<*>): Unit {
    if (!expected.java.isAssignableFrom(value?.javaClass))
      throw TestFailedException("Value is not of type $expected")
  }
}