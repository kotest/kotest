package io.kotest.properties

import io.kotest.assertions.Failures

inline fun <reified A> forNone(noinline fn: PropertyContext.(a: A) -> Boolean) = forNone(1000, fn)
inline fun <reified A> forNone(iterations: Int, noinline fn: PropertyContext.(a: A) -> Boolean) {
  forNone(iterations, Gen.default(), fn)
}

fun <A> forNone(gena: Gen<A>, fn: PropertyContext.(a: A) -> Boolean) = forNone(1000, gena, fn)
fun <A> Gen<A>.forNone(iterations: Int = 1000, fn: PropertyContext.(a0: A) -> Boolean) = forNone(iterations, this, fn)
fun <A> Gen<A>.forNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A) -> Boolean) = forNone(iterations, this, this, fn)
fun <A> Gen<A>.forNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A) -> Boolean) = forNone(iterations, this, this, this, fn)
fun <A> Gen<A>.forNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A) -> Boolean) = forNone(iterations, this, this, this, this, fn)
fun <A> Gen<A>.forNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A) -> Boolean) = forNone(iterations, this, this, this, this, this, fn)
fun <A> Gen<A>.forNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A, a5: A) -> Boolean) = forNone(iterations, this, this, this, this, this, this, fn)
fun <A> forNone(iterations: Int, gena: Gen<A>, fn: PropertyContext.(a: A) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A) {
    context.inc()
    val passed = context.fn(a)
    if (passed) {
      throw Failures.failure("Property passed for\n$a\nafter ${context.attempts()} attempts")
    }
  }
  for (a in gena.constants()) {
    test(a)
  }
  val avalues = gena.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    test(a)
  }
  outputClassifications(context)
}

inline fun <reified A, reified B> forNone(noinline fn: PropertyContext.(a: A, b: B) -> Boolean) {
  forNone(Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B> forNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> forNone(gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Boolean) = forNone(1000, gena, genb, fn)

fun <A, B> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Boolean) {
  val context = PropertyContext()
  fun test(a: A, b: B) {
    context.inc()
    val passed = context.fn(a, b)
    if (passed) {
      throw Failures.failure("Property passed for\n$a\n$b\nafter ${context.attempts()} attempts")
    }
  }
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      test(a, b)
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    test(a, b)
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C> forNone(noinline fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  forNone(1000, fn)
}

inline fun <reified A, reified B, reified C> forNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) =
  forNone(1000, gena, genb, genc, fn)

fun <A, B, C> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        context.inc()
        val passed = context.fn(a, b, c)
        if (passed) {
          throw Failures.failure("Property passed for\n$a\n$b\n$c\nafter ${context.attempts()} attempts")
        }
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
    context.inc()
    val passed = context.fn(a, b, c)
    if (passed) {
      throw Failures.failure("Property passed for\n$a\n$b\n$c\nafter ${context.attempts()} attempts")
    }
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D> forNone(noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Boolean) {
  forNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> forNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Boolean) =
  forNone(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Boolean) {
  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D) {
    context.inc()
    val passed = context.fn(a, b, c, d)
    if (passed) {
      throw Failures.failure("Property passed for\n$a\n$b\n$c\n$d\nafter ${context.attempts()} attempts")
    }
  }
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        for (d in gend.constants()) {
          test(a, b, c, d)
        }
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  val dvalues = gend.random().iterator()
  while (context.attempts() < iterations) {
    test(avalues.next(), bvalues.next(), cvalues.next(), dvalues.next())
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) =
  forNone(1000, fn)

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) =
  forNone(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D, e: E) {
    context.inc()
    val passed = context.fn(a, b, c, d, e)
    if (passed) {
      throw Failures.failure("Property passed for\n$a\n$b\n$c\n$d\n$e\nafter ${context.attempts()} attempts")
    }
  }
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        for (d in gend.constants()) {
          for (e in gene.constants()) {
            test(a, b, c, d, e)
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
    test(a, b, c, d, e)
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) =
  forNone(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  val context = PropertyContext()

  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    context.inc()
    val passed = context.fn(a, b, c, d, e, f)
    if (passed) {
      throw Failures.failure("Property passed for\n$a\n$b\n$c\n$d\n$e\n$f\nafter ${context.attempts()} attempts")
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
