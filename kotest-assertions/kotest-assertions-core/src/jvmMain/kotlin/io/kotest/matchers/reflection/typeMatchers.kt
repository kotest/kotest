package io.kotest.matchers.reflection

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

inline fun <reified T> KType.shouldBeOfType() = this should beOfType<T>()
inline fun <reified T> KType.shouldNotBeOfType() = this shouldNot beOfType<T>()
inline fun <reified T> beOfType() = object : Matcher<KType> {
  override fun test(value: KType) = MatcherResult(
      value.isSubtypeOf(T::class.starProjectedType),
      "Type $value should be ${T::class}",
      "Type $value should not be ${T::class}"
  )
}
