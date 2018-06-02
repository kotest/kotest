package io.kotlintest.properties

import io.kotlintest.properties.shrinking.Shrinker
import outputClassifications
import shrink
import testAndShrink

inline fun <reified A> assertAll(noinline fn: PropertyContext.(a: A) -> Unit) = assertAll(1000, fn)
inline fun <reified A> assertAll(iterations: Int, noinline fn: PropertyContext.(a: A) -> Unit) {
  assertAll(iterations, Gen.default(), fn)
}

fun <A> assertAll(gena: Gen<A>, fn: PropertyContext.(a: A) -> Unit) = assertAll(1000, gena, fn)
fun <A> assertAll(iterations: Int, gena: Gen<A>, fn: PropertyContext.(a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  val values = gena.constants().asSequence() + gena.random()
  _assertAll(iterations, values, gena.shrinker(), fn)
  outputClassifications(context)
}

fun <A> _assertAll(iterations: Int,
                   values: Sequence<A>,
                   shrinkera: Shrinker<A>?,
                   fn: PropertyContext.(a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  values.take(iterations).forEach { a ->
    testAndShrink(a, shrinkera, context, fn)
  }
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
    val (a, b) = it
    testAndShrink(a, b, shrinkera, shrinkerb, context, fn)
  }
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
    testAndShrink(a, b, c, gena, genb, genc, context, fn)
  }
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
    testAndShrink(avalues.next(), bvalues.next(), cvalues.next(), dvalues.next(), gena, genb, genc, gend, context, fn)
  }
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
    testAndShrink(a, b, c, d, e, gena, genb, genc, gend, gene, context, fn)
  }
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
      val smallestA = shrink(a, gena, { context.fn(it, b, c, d, e, f) })
      val smallestB = shrink(b, genb, { context.fn(smallestA, it, c, d, e, f) })
      val smallestC = shrink(c, genc, { context.fn(smallestA, smallestB, it, d, e, f) })
      val smallestD = shrink(d, gend, { context.fn(smallestA, smallestB, smallestC, it, e, f) })
      val smallestE = shrink(e, gene, { context.fn(smallestA, smallestB, smallestC, smallestD, it, f) })
      val smallestF = shrink(f, genf, { context.fn(smallestA, smallestB, smallestC, smallestD, smallestE, it) })
      val inputs = listOf(
          PropertyFailureInput<A>(a, smallestA),
          PropertyFailureInput<B>(b, smallestB),
          PropertyFailureInput<C>(c, smallestC),
          PropertyFailureInput<D>(d, smallestD),
          PropertyFailureInput<E>(e, smallestE),
          PropertyFailureInput<F>(f, smallestF)
      )
      throw PropertyAssertionError(x, context.attempts(), inputs)
    }
  }

  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        for (d in gend.constants()) {
          for (e in gene.constants()) {
            for (f in genf.constants()) {
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
    test(a, b, c, d, e, f)
  }
  outputClassifications(context)
}