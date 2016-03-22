package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import kotlin.reflect.KClass

interface TypeMatchers {

  infix fun Be<*>.a(expected: KClass<*>): Unit = an(expected)

  infix fun Be<*>.an(expected: KClass<*>): Unit {
    if (!expected.java.isAssignableFrom(value?.javaClass))
      throw TestFailedException("$value is not of type $expected")
  }

  infix fun <T> Be<T>.theSameInstanceAs(ref: T): Unit {
    if (value !== ref)
      throw TestFailedException("$value is not the same reference as $ref")
  }
}