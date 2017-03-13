package io.kotlintest.matchers

import kotlin.reflect.KClass

fun beInstanceOf(expected: KClass<*>): Matcher<Any> = object : Matcher<Any> {
  override fun test(value: Any): Result =
      Result(
          expected.java.isAssignableFrom(value.javaClass),
          "$value is not of type $expected, but was ${value.javaClass}")
}

fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
  override fun test(value: T) = Result(value === ref, "$value should be the same reference as $ref")
}

inline fun <reified T : Any> beOfType() = object : Matcher<T> {

  val exceptionClassName = T::class.qualifiedName

  override fun test(value: T) =
      Result(value.javaClass == T::class.java, "$value should be of type $exceptionClassName")
}