package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import kotlin.reflect.KClass

interface TypeMatchers {

  infix fun BeWrapper<*>.a(expected: KClass<*>): Unit = an(expected)

  infix fun BeWrapper<*>.an(expected: KClass<*>): Unit {
    if (!expected.java.isAssignableFrom(value?.javaClass))
      throw TestFailedException("$value is not of type $expected")
  }

  infix fun <T> BeWrapper<T>.theSameInstanceAs(ref: T): Unit {
    if (value !== ref)
      throw TestFailedException("$value is not the same reference as $ref")
  }

  infix fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
    override fun test(value: T) {
      if (value !== ref)
        throw TestFailedException("$value is not the same reference as $ref")
    }
  }
}