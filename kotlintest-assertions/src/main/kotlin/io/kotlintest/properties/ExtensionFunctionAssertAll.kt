package io.kotlintest.properties

import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

inline fun <reified A, R> KFunction1<A, R>.assertAll(crossinline test: PropertyContext.(A, R) -> Unit) {
  assertAll<A>(1000) { a ->
    val result = this@assertAll(a)
    test(a, result)
  }
}

inline fun <reified A, reified B, R> KFunction2<A, B, R>.assertAll(crossinline test: PropertyContext.(A, B, R) -> Unit) {

  assertAll<A, B>(1000) { a, b ->
    val result = this@assertAll(a, b)
    test(a, b, result)
  }
}