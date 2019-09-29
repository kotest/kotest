@file:Suppress("RemoveExplicitTypeArguments")

package io.kotest.properties

import io.kotest.properties.shrinking.Shrinker
import io.kotest.properties.shrinking.shrink

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) {
  override fun toString(): String {
    return "($a, $b, $c, $d)"
  }
}
data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
  override fun toString(): String {
    return "($a, $b, $c, $d, $e)"
  }
}
data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) {
  override fun toString(): String {
    return "($a, $b, $c, $d, $e, $f)"
  }
}

inline fun <reified A> assertAll(noinline fn: PropertyContext.(a: A) -> Unit) = assertAll(1000, fn)
inline fun <reified A> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A) -> Unit) {
  assertAll(iterations, Gen.default(), fn)
}

fun <A> assertAll(gena: Gen<A>, fn: PropertyContext.(a: A) -> Unit) = assertAll(1000, gena, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a: A) -> Unit) = assertAll(iterations, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A) -> Unit) = assertAll(iterations, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A) -> Unit) = assertAll(iterations, this, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A) -> Unit) = assertAll(iterations, this, this, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A) -> Unit) = assertAll(iterations, this, this, this, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A, a5: A) -> Unit) = assertAll(iterations, this, this, this, this, this, this, fn)
fun <A> assertAll(iterations: Int, gena: Gen<A>, fn: PropertyContext.(a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val values = gena.constants().asSequence() + gena.random()
  _assertAll(iterations, values, gena.shrinker(), fn)
}

fun <A> _assertAll(iterations: Int,
                   values: Sequence<A>,
                   shrinkera: Shrinker<A>?,
                   fn: PropertyContext.(a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  values.take(iterations).forEach { a ->
    context.addValue(a)
    testAndShrink(a, shrinkera, context, fn)
  }
  outputValues(context)
  outputClassifications(context)
}

inline fun <reified A, reified B> assertAll(noinline fn: PropertyContext.(a: A, b: B) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> assertAll(gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Unit) = assertAll(1000, gena, genb, fn)
fun <A, B> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Unit) {
  val values = gena.constants().flatMap { a ->
    genb.constants().map { b ->
      Pair(a, b)
    }
  }.asSequence() + gena.random().zip(genb.random())
  _assertAll(iterations, values, gena.shrinker(), genb.shrinker(), fn)
}

fun <A, B> _assertAll(iterations: Int,
                      values: Sequence<Pair<A, B>>,
                      shrinkera: Shrinker<A>?,
                      shrinkerb: Shrinker<B>?,
                      fn: PropertyContext.(a: A, b: B) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  values.take(iterations).forEach {
    context.addValue(it)
    val (a, b) = it
    testAndShrink(a, b, shrinkera, shrinkerb, context, fn)
  }
  outputValues(context)
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C> assertAll(noinline fn: PropertyContext.(a: A, b: B, c: C) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B, reified C> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) =
    assertAll(1000, gena, genb, genc, fn)

fun <A, B, C> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        context.addValue(Triple(a, b, c))
        testAndShrink(a, b, c, gena, genb, genc, context, fn)
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    context.addValue(Triple(a, b, c))
    testAndShrink(a, b, c, gena, genb, genc, context, fn)
  }
  outputValues(context)
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D> assertAll(noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) {
  assertAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit) =
    assertAll(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  val context = PropertyContext()

  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        for (d in gend.constants()) {
          context.addValue(Tuple4(a, b, c, d))
          testAndShrink(a, b, c, d, gena, genb, genc, gend, context, fn)
        }
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  val dvalues = gend.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    val d = dvalues.next()
    context.addValue(Tuple4(a, b, c, d))
    testAndShrink(a, b, c, d, gena, genb, genc, gend, context, fn)
  }
  outputValues(context)
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) =
    assertAll(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        for (d in gend.constants()) {
          for (e in gene.constants()) {
            context.addValue(Tuple5(a, b, c, d, e))
            testAndShrink(a, b, c, d, e, gena, genb, genc, gend, gene, context, fn)
          }
        }
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  val dvalues = gend.random().iterator()
  val evalues = gene.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    val d = dvalues.next()
    val e = evalues.next()
    context.addValue(Tuple5(a, b, c, d, e))
    testAndShrink(a, b, c, d, e, gena, genb, genc, gend, gene, context, fn)
  }
  outputValues(context)
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) =
    assertAll(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>,
                                 fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  val context = PropertyContext()

  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    context.inc()
    try {
      context.fn(a, b, c, d, e, f)
    } catch (x: AssertionError) {
      val smallestA = shrink(a, gena) { context.fn(it, b, c, d, e, f) }
      val smallestB = shrink(b, genb) { context.fn(smallestA, it, c, d, e, f) }
      val smallestC = shrink(c, genc) { context.fn(smallestA, smallestB, it, d, e, f) }
      val smallestD = shrink(d, gend) { context.fn(smallestA, smallestB, smallestC, it, e, f) }
      val smallestE = shrink(e, gene) { context.fn(smallestA, smallestB, smallestC, smallestD, it, f) }
      val smallestF = shrink(f, genf) { context.fn(smallestA, smallestB, smallestC, smallestD, smallestE, it) }
      val inputs = listOf(
          PropertyFailureInput<A>(a, smallestA),
          PropertyFailureInput<B>(b, smallestB),
          PropertyFailureInput<C>(c, smallestC),
          PropertyFailureInput<D>(d, smallestD),
          PropertyFailureInput<E>(e, smallestE),
          PropertyFailureInput<F>(f, smallestF)
      )
      throw propertyAssertionError(x, context.attempts(), inputs)
    }
  }

  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        for (d in gend.constants()) {
          for (e in gene.constants()) {
            for (f in genf.constants()) {
              context.addValue(Tuple6(a, b, c, d, e, f))
              test(a, b, c, d, e, f)
            }
          }
        }
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  val dvalues = gend.random().iterator()
  val evalues = gene.random().iterator()
  val fvalues = genf.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    val d = dvalues.next()
    val e = evalues.next()
    val f = fvalues.next()
    context.addValue(Tuple6(a, b, c, d, e, f))
    test(a, b, c, d, e, f)
  }
  outputValues(context)
  outputClassifications(context)
}
