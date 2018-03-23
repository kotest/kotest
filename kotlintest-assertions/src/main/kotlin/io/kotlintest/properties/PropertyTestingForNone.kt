package io.kotlintest.properties

inline fun <reified A> forNone(noinline fn: (a: A) -> Boolean) = forNone(1000, fn)
inline fun <reified A> forNone(iterations: Int, noinline fn: (a: A) -> Boolean) {
  forNone(iterations, Gen.default(), fn)
}

fun <A> forNone(gena: Gen<A>, fn: (a: A) -> Boolean) = forNone(1000, gena, fn)
fun <A> forNone(iterations: Int, gena: Gen<A>, fn: (a: A) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  gena.values().take(iterations).withIndex().forEach { (attempt, a) ->
    val passed = fn(a)
    if (passed) {
      throw AssertionError("Property passed for\n$a\nafter $attempt attempts")
    }
  }
}

inline fun <reified A, reified B> forNone(noinline fn: (a: A, b: B) -> Boolean) {
  forNone(Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B> forNone(iterations: Int, noinline fn: (a: A, b: B) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> forNone(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) = forNone(1000, gena, genb, fn)

fun <A, B> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  var attempts = 0
  fun test(a: A, b: B) {
    attempts++
    val passed = fn(a, b)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\nafter $attempts attempts")
    }
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

inline fun <reified A, reified B, reified C> forNone(noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forNone(1000, fn)
}

inline fun <reified A, reified B, reified C> forNone(iterations: Int, noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) =
    forNone(1000, gena, genb, genc, fn)

fun <A, B, C> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  for (a in gena.always()) {
    for (b in genb.always()) {
      for (c in genc.always()) {
        attempts++
        val passed = fn(a, b, c)
        if (passed) {
          throw AssertionError("Property passed for\n$a\n$b\n$c\nafter $attempts attempts")
        }
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
    attempts++
    val passed = fn(a, b, c)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\n$c\nafter $attempts attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> forNone(noinline fn: (a: A, b: B, c: C, D) -> Boolean) {
  forNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) =
    forNone(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) {
  var attempts = 0
  fun test(a: A, b: B, c: C, d: D) {
    attempts++
    val passed = fn(a, b, c, d)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\n$c\n$d\nafter $attempts attempts")
    }
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

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) =
    forNone(1000, fn)

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(iterations: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) =
    forNone(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A, b: B, c: C, d: D, e: E) {
    attempts++
    val passed = fn(a, b, c, d, e)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\n$c\n$d\n$e\nafter $attempts attempts")
    }
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

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(iterations: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) =
    forNone(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> forNone(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  var attempts = 0

  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    attempts++
    val passed = fn(a, b, c, d, e, f)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\n$c\n$d\n$e\nafter $attempts attempts")
    }
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