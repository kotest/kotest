package io.kotlintest.matchers

import kotlin.reflect.KClass

object be : Keyword<be>

@Deprecated("Use `obj should beInstanceOf<String>` or `obj shouldBe instanceOf<String>`")
infix fun MatcherBuilder<be, *>.a(expected: KClass<*>): Unit = an(expected)

@Deprecated("Use `obj should beInstanceOf<String>` or `obj shouldBe instanceOf<String>`")
infix fun MatcherBuilder<be, *>.an(expected: KClass<*>): Unit {
  if (!expected.java.isAssignableFrom(value?.javaClass))
    throw AssertionError("$value is not of type $expected")
}

@Deprecated("Use `obj should beTheSameInstanceAs(other)`")
infix fun <T> MatcherBuilder<be, T>.theSameInstanceAs(ref: T): Unit {
  if (value !== ref)
    throw AssertionError("$value is not the same reference as $ref")
}

interface TypeMatchers {

  fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
    override fun test(value: T) = Result(value === ref, "$value should be the same reference as $ref")
  }
}