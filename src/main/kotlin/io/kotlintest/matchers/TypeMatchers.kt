package io.kotlintest.matchers

import kotlin.reflect.KClass

object be : Keyword<be>

interface TypeMatchers {

  fun beInstanceOf(expected: KClass<*>): Matcher<Any> = object: Matcher<Any> {
    override fun test(value: Any): Result =
        Result(
            expected.java.isAssignableFrom(value.javaClass),
            "$value is not of type $expected, but was ${value.javaClass}")
  }

  fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
    override fun test(value: T) = Result(value === ref, "$value should be the same reference as $ref")
  }
}