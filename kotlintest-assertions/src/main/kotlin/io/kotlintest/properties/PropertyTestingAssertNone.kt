package io.kotlintest.properties

inline fun <reified A> assertNone(noinline fn: (a: A) -> Unit) = assertNone(1000, fn)
inline fun <reified A> assertNone(iterations: Int, noinline fn: (a: A) -> Unit) {
  assertNone(iterations, Gen.default(), fn)
}

fun <A> assertNone(gena: Gen<A>, fn: (a: A) -> Unit) = assertNone(1000, gena, fn)
fun <A> assertNone(iterations: Int, gena: Gen<A>, fn: (a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A) {
    attempts++
    val passed = try {
      fn(a)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw AssertionError("Property passed for\n$a\nafter $attempts attempts")
  }
  for (a in gena.always()) {
    test(a)
  }
  val avalues = gena.random().iterator()
  while (attempts < iterations) {
    val a = avalues.next()
    test(a)
  }
}

inline fun <reified A, reified B> assertNone(noinline fn: (a: A, b: B) -> Unit) {
  assertNone(Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B> assertNone(iterations: Int, noinline fn: (a: A, b: B) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> assertNone(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Unit) = assertNone(1000, gena, genb, fn)

fun <A, B> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Unit) {
  var attempts = 0
  fun test(a: A, b: B) {
    attempts++
    val passed = try {
      fn(a, b)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw AssertionError("Property passed for\n$a\n$b\nafter $attempts attempts")
  }
  for (a in gena.always()) {
    for (b in genb.always()) {
      test(a, b)
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  while (attempts < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    test(a, b)
  }
}

inline fun <reified A, reified B, reified C> assertNone(noinline fn: (a: A, b: B, c: C) -> Unit) {
  assertNone(1000, fn)
}

inline fun <reified A, reified B, reified C> assertNone(iterations: Int, noinline fn: (a: A, b: B, c: C) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Unit) =
    assertNone(1000, gena, genb, genc, fn)

fun <A, B, C> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A, b: B, c: C) {
    attempts++
    val passed = try {
      fn(a, b, c)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw AssertionError("Property passed for\n$a\n$b\n$c\nafter $attempts attempts")
  }
  for (a in gena.always()) {
    for (b in genb.always()) {
      for (c in genc.always()) {
        test(a, b, c)
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  while (attempts < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    test(a, b, c)
  }
}

inline fun <reified A, reified B, reified C, reified D> assertNone(noinline fn: (a: A, b: B, c: C, D) -> Unit) {
  assertNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> assertNone(iterations: Int, noinline fn: (a: A, b: B, c: C, D) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Unit) =
    assertNone(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Unit) {
  var attempts = 0
  fun test(a: A, b: B, c: C, d: D) {
    attempts++
    val passed = try {
      fn(a, b, c, d)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw AssertionError("Property passed for\n$a\n$b\n$c\n$d\nafter $attempts attempts")
  }
  for (a in gena.always()) {
    for (b in genb.always()) {
      for (c in genc.always()) {
        for (d in gend.always()) {
          test(a, b, c, d)
        }
      }
    }
  }
  val avalues = gena.random().iterator()
  val bvalues = genb.random().iterator()
  val cvalues = genc.random().iterator()
  val dvalues = gend.random().iterator()
  while (attempts < iterations) {
    test(avalues.next(), bvalues.next(), cvalues.next(), dvalues.next())
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> assertNone(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) =
    assertNone(1000, fn)

inline fun <reified A, reified B, reified C, reified D, reified E> assertNone(iterations: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) =
    assertNone(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A, b: B, c: C, d: D, e: E) {
    attempts++
    val passed = try {
      fn(a, b, c, d, e)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw AssertionError("Property passed for\n$a\n$b\n$c\n$d\n$e\nafter $attempts attempts")
  }
  for (a in gena.always()) {
    for (b in genb.always()) {
      for (c in genc.always()) {
        for (d in gend.always()) {
          for (e in gene.always()) {
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
  while (attempts < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    val d = dvalues.next()
    val e = evalues.next()
    test(a, b, c, d, e)
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertNone(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertNone(iterations: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) =
    assertNone(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> assertNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    attempts++
    val passed = try {
      fn(a, b, c, d, e, f)
      true
    } catch (e: AssertionError) {
      false
    } catch (e: Exception) {
      throw e
    }
    if (passed)
      throw AssertionError("Property passed for\n$a\n$b\n$c\n$d\n$e\n$f\nafter $attempts attempts")
  }

  for (a in gena.always()) {
    for (b in genb.always()) {
      for (c in genc.always()) {
        for (d in gend.always()) {
          for (e in gene.always()) {
            for (f in genf.always()) {
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
  while (attempts < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    val d = dvalues.next()
    val e = evalues.next()
    val f = fvalues.next()
    test(a, b, c, d, e, f)
  }
}