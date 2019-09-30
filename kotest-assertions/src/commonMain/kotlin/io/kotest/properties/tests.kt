@file:Suppress("RemoveExplicitTypeArguments")

package io.kotest.properties

import io.kotest.properties.shrinking.Shrinker
import io.kotest.properties.shrinking.shrink
import io.kotest.properties.shrinking.shrink2
import io.kotest.properties.shrinking.shrinkInputs

fun <A> testAndShrink(a: A, shrinkera: Shrinker<A>?, context: PropertyContext, fn: PropertyContext.(a: A) -> Unit) {
  context.inc()
  try {
    context.fn(a)
  } catch (e: AssertionError) {
    val smallestA = shrink2(a, shrinkera) { context.fn(it) }
    val inputs = listOf(PropertyFailureInput(a, smallestA))
    throw propertyAssertionError(e, context.attempts(), inputs)
  }
}

fun <A, B> testAndShrink(a: A, b: B, shrinkera: Shrinker<A>?, shrinkerb: Shrinker<B>?, context: PropertyContext, fn: PropertyContext.(a: A, b: B) -> Unit) {
  context.inc()
  try {
    context.fn(a, b)
  } catch (e: AssertionError) {
    val smallestA = shrink2(a, shrinkera) { context.fn(it, b) }
    val smallestB = shrink2(b, shrinkerb) { context.fn(smallestA, it) }
    val inputs = listOf(
        PropertyFailureInput<A>(a, smallestA),
        PropertyFailureInput<B>(b, smallestB)
    )
    throw propertyAssertionError(e, context.attempts(), inputs)
  }
}

fun <A, B, C> testAndShrink(a: A, b: B, c: C, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  context.inc()
  try {
    context.fn(a, b, c)
  } catch (e: AssertionError) {
    val smallestA = shrink(a, gena) { context.fn(it, b, c) }
    val smallestB = shrink(b, genb) { context.fn(smallestA, it, c) }
    val smallestC = shrink(c, genc) { context.fn(smallestA, smallestB, c) }
    val inputs = listOf(
        PropertyFailureInput<A>(a, smallestA),
        PropertyFailureInput<B>(b, smallestB),
        PropertyFailureInput<C>(c, smallestC)
    )
    throw propertyAssertionError(e, context.attempts(), inputs)
  }
}

fun <A, B, C, D> testAndShrink(a: A, b: B, c: C, d: D, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) {
  context.inc()
  try {
    context.fn(a, b, c, d)
  } catch (e: AssertionError) {
    shrinkInputs(a, b, c, d, gena, genb, genc, gend, context, fn, e)
  }
}

fun <A, B, C, D, E> testAndShrink(a: A, b: B, c: C, d: D, e: E, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C, D, E) -> Unit) {
  context.inc()
  try {
    context.fn(a, b, c, d, e)
  } catch (ex: AssertionError) {
    val smallestA = shrink(a, gena) { context.fn(it, b, c, d, e) }
    val smallestB = shrink(b, genb) { context.fn(smallestA, it, c, d, e) }
    val smallestC = shrink(c, genc) { context.fn(smallestA, smallestB, it, d, e) }
    val smallestD = shrink(d, gend) { context.fn(smallestA, smallestB, smallestC, it, e) }
    val smallestE = shrink(e, gene) { context.fn(smallestA, smallestB, smallestC, smallestD, it) }
    val inputs = listOf(
        PropertyFailureInput<A>(a, smallestA),
        PropertyFailureInput<B>(b, smallestB),
        PropertyFailureInput<C>(c, smallestC),
        PropertyFailureInput<D>(d, smallestD),
        PropertyFailureInput<E>(e, smallestE)
    )
    throw propertyAssertionError(ex, context.attempts(), inputs)
  }
}
