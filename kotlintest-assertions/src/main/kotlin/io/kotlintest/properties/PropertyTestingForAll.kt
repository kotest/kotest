package io.kotlintest.properties

fun outputClassifications(context: PropertyContext) {
  context.classificationCounts().entries.sortedByDescending { it.value }.forEach {
    val percentage = (it.value / context.attempts().toDouble() * 100)
    println("${String.format("%.2f", percentage)}% ${it.key}")
  }
}

inline fun <reified A> forAll(noinline fn: PropertyContext.(a: A) -> Boolean) = forAll(1000, fn)
inline fun <reified A> forAll(iterations: Int, noinline fn: PropertyContext.(a: A) -> Boolean) {
  forAll(iterations, Gen.default(), fn)
}

fun <A> forAll(gena: Gen<A>, fn: PropertyContext.(a: A) -> Boolean) = forAll(1000, gena, fn)
fun <A> forAll(iterations: Int, gena: Gen<A>, fn: PropertyContext.(a: A) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A) {
    context.inc()
    val passed = context.fn(a)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\nafter ${context.attempts()} attempts")
    }
  }
  for (a in gena.always()) {
    test(a)
  }
  val avalues = gena.random().iterator()
  while (context.attempts() < iterations) {
    val a = avalues.next()
    test(a)
  }
  outputClassifications(context)
}

inline fun <reified A, reified B> forAll(noinline fn: PropertyContext.(a: A, b: B) -> Boolean) = forAll(1000, fn)
inline fun <reified A, reified B> forAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> forAll(gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Boolean) = forAll(1000, gena, genb, fn)
fun <A, B> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B) {
    context.inc()
    val passed = context.fn(a, b)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\nafter ${context.attempts()} attempts")
    }
  }
  for (a in gena.always()) {
    for (b in genb.always()) {
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

inline fun <reified A, reified B, reified C> forAll(noinline fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) = forAll(1000, fn)
inline fun <reified A, reified B, reified C> forAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) =
    forAll(1000, gena, genb, genc, fn)

fun <A, B, C> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B, c: C) {
    context.inc()
    val passed = context.fn(a, b, c)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\n$c\nafter ${context.attempts()} attempts")
    }
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
  while (context.attempts() < iterations) {
    val a = avalues.next()
    val b = bvalues.next()
    val c = cvalues.next()
    test(a, b, c)
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D> forAll(noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Boolean) {
  forAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> forAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Boolean) =
    forAll(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D) {
    context.inc()
    val passed = context.fn(a, b, c, d)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\nafter ${context.attempts()} attempts")
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
  while (context.attempts() < iterations) {
    test(avalues.next(), bvalues.next(), cvalues.next(), dvalues.next())
  }
  outputClassifications(context)
}

inline fun <reified A, reified B, reified C, reified D, reified E> forAll(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E> forAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) =
    forAll(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D, e: E) {
    context.inc()
    val passed = context.fn(a, b, c, d, e)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\n$e\nafter ${context.attempts()} attempts")
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

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) =
    forAll(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")

  val context = PropertyContext()
  fun test(a: A, b: B, c: C, d: D, e: E, f: F) {
    context.inc()
    val passed = context.fn(a, b, c, d, e, f)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\n$e\n$f\nafter ${context.attempts()} attempts")
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