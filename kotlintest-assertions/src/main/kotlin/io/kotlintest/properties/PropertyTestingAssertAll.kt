package io.kotlintest.properties

inline fun <reified A> assertAll(noinline fn: (a: A) -> Unit) = assertAll(1000, fn)
inline fun <reified A> assertAll(iterations: Int, noinline fn: (a: A) -> Unit) {
  assertAll(iterations, Gen.default(), fn)
}

fun <A> assertAll(gena: Gen<A>, fn: (a: A) -> Unit) = assertAll(1000, gena, fn)
fun <A> assertAll(iterations: Int, gena: Gen<A>, fn: (a: A) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A) {
    attempts++
    fn(a)
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

inline fun <reified A, reified B> assertAll(noinline fn: (a: A, b: B) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B> assertAll(iterations: Int, noinline fn: (a: A, b: B) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> assertAll(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Unit) = assertAll(1000, gena, genb, fn)
fun <A, B> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A, b: B) {
    attempts++
    fn(a, b)
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

inline fun <reified A, reified B, reified C> assertAll(noinline fn: (a: A, b: B, c: C) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B, reified C> assertAll(iterations: Int, noinline fn: (a: A, b: B, c: C) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Unit) =
    assertAll(1000, gena, genb, genc, fn)

fun <A, B, C> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  for (a in gena.always()) {
    for (b in genb.always()) {
      for (c in genc.always()) {
        attempts++
        fn(a, b, c)
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
    fn(a, b, c)
  }
}

inline fun <reified A, reified B, reified C, reified D> assertAll(noinline fn: (a: A, b: B, c: C, D) -> Unit) {
  assertAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> assertAll(iterations: Int, noinline fn: (a: A, b: B, c: C, D) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Unit) =
    assertAll(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  var attempts = 0
  fun test(a: A, b: B, c: C, d: D) {
    attempts++
    fn(a, b, c, d)
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

inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(iterations: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) =
    assertAll(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  var attempts = 0
  fun test(a: A, b: B, c: C, d: D, e: E) {
    attempts++
    fn(a, b, c, d, e)
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

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(iterations: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) =
    assertAll(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> assertAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  var attempts = 0

  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    attempts++
    fn(a, b, c, d, e, f)
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