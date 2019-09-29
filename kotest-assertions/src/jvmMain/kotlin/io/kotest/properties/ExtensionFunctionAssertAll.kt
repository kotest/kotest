package io.kotest.properties

import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

inline fun <reified A, R> KFunction1<A, R>.assertAll(crossinline test: PropertyContext.(A, R) -> Unit) {
  val gena = Gen.default<A>()
  val fn: PropertyContext.(A) -> Unit = { a ->
    val r = this@assertAll.invoke(a)
    this.test(a, r)
  }
  _assertAll(1000, gena.constants().asSequence() + gena.random(), gena.shrinker(), fn)
}

inline fun <reified A, reified B, R> KFunction2<A, B, R>.assertAll(crossinline test: PropertyContext.(A, B, R) -> Unit) {

  val gena = Gen.default<A>()
  val genb = Gen.default<B>()

  val fn: PropertyContext.(A, B) -> Unit = { a, b ->
    val r = this@assertAll.invoke(a, b)
    this.test(a, b, r)
  }

  val values = gena.constants().flatMap { a ->
    genb.constants().map { b ->
      Pair(a, b)
    }
  }.asSequence() + gena.random().zip(genb.random())

  _assertAll(1000, values, gena.shrinker(), genb.shrinker(), fn)
}