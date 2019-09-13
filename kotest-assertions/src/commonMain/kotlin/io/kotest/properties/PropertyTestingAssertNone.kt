package io.kotest.properties

import io.kotest.assertions.Failures

inline fun <reified A> assertNone(noinline fn: PropertyContext.(a: A) -> Unit) = assertNone(1000, fn)
inline fun <reified A> assertNone(iterations: Int, noinline fn: PropertyContext.(a: A) -> Unit) {
  assertNone(iterations, Gen.default(), fn)
}

fun <A> assertNone(gena: Gen<A>, fn: PropertyContext.(a: A) -> Unit) = assertNone(1000, gena, fn)
fun <A> Gen<A>.assertNone(iterations: Int = 1000, fn: PropertyContext.(a0: A) -> Unit) = assertNone(iterations, this, fn)
fun <A> Gen<A>.assertNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A) -> Unit) = assertNone(iterations, this, this, fn)
fun <A> Gen<A>.assertNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A) -> Unit) = assertNone(iterations, this, this, this, fn)
fun <A> Gen<A>.assertNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A) -> Unit) = assertNone(iterations, this, this, this, this, fn)
fun <A> Gen<A>.assertNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A) -> Unit) = assertNone(iterations, this, this, this, this, this, fn)
fun <A> Gen<A>.assertNone(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A, a5: A) -> Unit) = assertNone(iterations, this, this, this, this, this, this, fn)
fun <A> assertNone(iterations: Int, gena: Gen<A>, fn: PropertyContext.(a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A) {
    context.inc()
    val passed = try {
      context.fn(a)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw Failures.failure("Property passed for\n$a\nafter ${context.attempts()} attempts")
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

inline fun <reified A, reified B> assertNone(noinline fn: PropertyContext.(a: A, b: B) -> Unit) {
  assertNone(Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B> assertNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> assertNone(gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Unit) = assertNone(1000, gena, genb, fn)

fun <A, B> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Unit) {
  val context = PropertyContext()
  fun test(a: A, b: B) {
    context.inc()
    val passed = try {
      context.fn(a, b)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw Failures.failure("Property passed for\n$a\n$b\nafter ${context.attempts()} attempts")
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

inline fun <reified A, reified B, reified C> assertNone(noinline fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  assertNone(1000, fn)
}

inline fun <reified A, reified B, reified C> assertNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) =
    assertNone(1000, gena, genb, genc, fn)

fun <A, B, C> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B, c: C) {
    context.inc()
    val passed = try {
      context.fn(a, b, c)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw Failures.failure("Property passed for\n$a\n$b\n$c\nafter ${context.attempts()} attempts")
  }
  for (a in gena.constants()) {
    for (b in genb.constants()) {
      for (c in genc.constants()) {
        test(a, b, c)
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
    test(a, b, c)
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D> assertNone(noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) {
  assertNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> assertNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit) =
    assertNone(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit) {
  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D) {
    context.inc()
    val passed = try {
      context.fn(a, b, c, d)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw Failures.failure("Property passed for\n$a\n$b\n$c\n$d\nafter ${context.attempts()} attempts")
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

inline fun <reified A, reified B, reified C, reified D, reified E> assertNone(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) =
    assertNone(1000, fn)

inline fun <reified A, reified B, reified C, reified D, reified E> assertNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) =
    assertNone(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D, e: E) {
    context.inc()
    val passed = try {
      context.fn(a, b, c, d, e)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw Failures.failure("Property passed for\n$a\n$b\n$c\n$d\n$e\nafter ${context.attempts()} attempts")
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

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertNone(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertNone(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) =
    assertNone(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    context.inc()
    val passed = try {
      context.fn(a, b, c, d, e, f)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw Failures.failure("Property passed for\n$a\n$b\n$c\n$d\n$e\n$f\nafter ${context.attempts()} attempts")
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
