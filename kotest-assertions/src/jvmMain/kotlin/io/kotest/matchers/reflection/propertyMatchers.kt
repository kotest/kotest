package io.kotest.matchers.reflection

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KProperty

inline fun <reified T> KProperty<*>.shouldBeOfType() = this.returnType.shouldBeOfType<T>()
inline fun <reified T> KProperty<*>.shouldNotBeOfType() = this.returnType.shouldNotBeOfType<T>()

fun KProperty<*>.shouldBeConst() = this should beConst()
fun KProperty<*>.shouldNotBeConst() = this shouldNot beConst()
fun beConst() = object : Matcher<KProperty<*>> {
  override fun test(value: KProperty<*>) = MatcherResult(
      value.isConst,
      "Property $value should be const",
      "Property $value should not be const"
  )
}

fun KProperty<*>.shouldBeLateInit() = this should beLateInit()
fun KProperty<*>.shouldNotBeLateInit() = this shouldNot beLateInit()
fun beLateInit() = object : Matcher<KProperty<*>> {
  override fun test(value: KProperty<*>) = MatcherResult(
      value.isLateinit,
      "Property $value should be lateinit",
      "Property $value should not be lateinit"
  )
}
