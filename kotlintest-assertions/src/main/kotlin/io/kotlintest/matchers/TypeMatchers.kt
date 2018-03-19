package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result
import kotlin.reflect.KClass

// alias for beInstanceOf
fun instanceOf(expected: KClass<*>): Matcher<Any> = beInstanceOf(expected)

fun beInstanceOf(expected: KClass<*>): Matcher<Any> = object : Matcher<Any> {
  override fun test(value: Any): Result =
      Result(
          expected.java.isAssignableFrom(value.javaClass),
          "$value is of type ${value.javaClass} but expected $expected",
          "$value should not be of type $expected"
      )
}

fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
  override fun test(value: T) = Result(value === ref, "$value should be the same reference as $ref", "$value should not be the same reference as $ref")
}

inline fun <reified T : Any> beInstanceOf(): Matcher<T> = object : Matcher<T> {

  override fun test(value: T): Result =
      Result(
          T::class.java.isAssignableFrom(value.javaClass),
          "$value is of type ${value.javaClass} but expected ${T::class.java.canonicalName}",
          "$value should not be an instance of ${T::class.java.canonicalName}")

}

inline fun <reified T : Any> beOfType() = object : Matcher<Any> {

  val className = T::class.qualifiedName

  override fun test(value: Any) =
      Result(value.javaClass == T::class.java, "$value should be of type $className", "$value should not be of type $className")
}