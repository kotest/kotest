package io.kotlintest.matchers

import kotlin.reflect.KClass

object be : ShouldKeyword<be>

infix fun ShouldBuilder<be, *>.a(expected: KClass<*>): Unit = an(expected)

infix fun ShouldBuilder<be, *>.an(expected: KClass<*>): Unit {
  if (!expected.java.isAssignableFrom(value?.javaClass))
    throw AssertionError("$value is not of type $expected")
}

infix fun <T> ShouldBuilder<be, T>.theSameInstanceAs(ref: T): Unit {
  if (value !== ref)
    throw AssertionError("$value is not the same reference as $ref")
}

interface TypeMatchers {

  infix fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
    override fun test(value: T) {
      if (value !== ref)
        throw AssertionError("$value is not the same reference as $ref")
    }
  }
}