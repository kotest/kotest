@file:Suppress("RemoveExplicitTypeArguments")

import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext

inline fun <A> testAndShrink(
        a: A, genA: Gen<A>,
        context: PropertyContext, fn: PropertyContext.(a: A) -> Unit
) {
  context.inc()
  try {
    context.fn(a)
  } catch (e: AssertionError) {
    shrinkInputs(a, genA.shrinker(), context, e, fn)
  }
}

inline fun <A, B> testAndShrink(
        a: A, b: B,
        genA: Gen<A>, genB: Gen<B>,
        context: PropertyContext, fn: PropertyContext.(a: A, b: B) -> Unit
) {
  context.inc()
  try {
    context.fn(a, b)
  } catch (e: AssertionError) {
    shrinkInputs(a, b, genA.shrinker(), genB.shrinker(), context, e, fn)
  }
}

inline fun <A, B, C> testAndShrink(
        a: A, b: B, c: C,
        genA: Gen<A>, genB: Gen<B>, genC: Gen<C>,
        context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C) -> Unit
) {
  context.inc()
  try {
    context.fn(a, b, c)
  } catch (e: AssertionError) {
    shrinkInputs(a, b, c, genA.shrinker(), genB.shrinker(), genC.shrinker(), context, e, fn)
  }
}

inline fun <A, B, C, D> testAndShrink(
        a: A, b: B, c: C, d: D,
        genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>,
        context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit
) {
  context.inc()
  try {
    context.fn(a, b, c, d)
  } catch (e: AssertionError) {
    shrinkInputs(a, b, c, d, genA.shrinker(), genB.shrinker(), genC.shrinker(), genD.shrinker(), context, e, fn)
  }
}

inline fun <A, B, C, D, E> testAndShrink(
        a: A, b: B, c: C, d: D, e: E,
        genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>,
        context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C, D, E) -> Unit
) {
  context.inc()
  try {
    context.fn(a, b, c, d, e)
  } catch (ex: AssertionError) {
    shrinkInputs(a, b, c, d, e, genA.shrinker(), genB.shrinker(), genC.shrinker(), genD.shrinker(), genE.shrinker(), context, ex, fn)
  }
}

inline fun <A, B, C, D, E, F> testAndShrink(
        a: A, b: B, c: C, d: D, e: E, f: F,
        genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>,
        context: PropertyContext, fn: PropertyContext.(a: A, b: B, c: C, D, E, F) -> Unit
) {
  context.inc()
  try {
    context.fn(a, b, c, d, e, f)
  } catch (ex: AssertionError) {
    shrinkInputs(a, b, c, d, e, f, genA.shrinker(), genB.shrinker(), genC.shrinker(), genD.shrinker(), genE.shrinker(), genF.shrinker(), context, ex, fn)
  }
}
