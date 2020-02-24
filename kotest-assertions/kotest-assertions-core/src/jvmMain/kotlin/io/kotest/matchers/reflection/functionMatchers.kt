package io.kotest.matchers.reflection

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

fun KFunction<*>.shouldHaveAnnotations() = this should haveFunctionAnnotations()
fun KFunction<*>.shouldNotHaveAnnotations() = this shouldNot haveFunctionAnnotations()
infix fun KFunction<*>.shouldHaveAnnotations(count: Int) = this should haveFunctionAnnotations(count)
infix fun KFunction<*>.shouldNotHaveAnnotations(count: Int) = this shouldNot haveFunctionAnnotations(count)
fun haveFunctionAnnotations(count: Int = -1) = object : Matcher<KFunction<*>> {
  override fun test(value: KFunction<*>) = if (count < 0) {
    MatcherResult(
       value.annotations.isNotEmpty(),
        "Function $value should have annotations",
        "Function $value should not have annotations"
    )
  } else {
    MatcherResult(
        value.annotations.size == count,
        "Function $value should have $count annotations",
        "Function $value should not have $count annotations"
    )
  }
}

inline fun <reified T : Annotation> KFunction<*>.shouldBeAnnotatedWith(block: (T) -> Unit = {}) {
  this should beAnnotatedWith<T>()
  findAnnotation<T>()?.let(block)
}

inline fun <reified T : Annotation> KFunction<*>.shouldNotBeAnnotatedWith() = this shouldNot beAnnotatedWith<T>()
inline fun <reified T : Annotation> beAnnotatedWith() = object : Matcher<KFunction<*>> {
  override fun test(value: KFunction<*>) = MatcherResult(
      value.findAnnotation<T>() != null,
      "Function $value should have annotation ${T::class}",
      "Function $value should not have annotation ${T::class}"
  )
}

inline fun <reified T> KFunction<*>.shouldHaveReturnType() = this.returnType.shouldBeOfType<T>()
inline fun <reified T> KFunction<*>.shouldNotHaveReturnType() = this.returnType.shouldNotBeOfType<T>()

fun KFunction<*>.shouldBeInline() = this should beInline()
fun KFunction<*>.shouldNotBeInline() = this shouldNot beInline()
fun beInline() = object : Matcher<KFunction<*>> {
  override fun test(value: KFunction<*>) = MatcherResult(
      value.isInline,
      "Function $value should be inline",
      "Function $value should not be inline"
  )
}

fun KFunction<*>.shouldBeInfix() = this should beInfix()
fun KFunction<*>.shouldNotBeInfix() = this shouldNot beInfix()
fun beInfix() = object : Matcher<KFunction<*>> {
  override fun test(value: KFunction<*>) = MatcherResult(
      value.isInfix,
      "Function $value should be infix",
      "Function $value should not be infix"
  )
}
