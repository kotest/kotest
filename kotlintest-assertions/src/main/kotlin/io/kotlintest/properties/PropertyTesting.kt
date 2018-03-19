package io.kotlintest.properties

inline fun <reified A> forAll(noinline fn: (a: A) -> Boolean) {
  forAll(Gen.default(), fn)
}

inline fun <reified A> forAll(attempts: Int, noinline fn: (a: A) -> Boolean) {
  forAll(attempts, Gen.default(), fn)
}

fun <A> forAll(gena: Gen<A>, fn: (a: A) -> Boolean) {
  forAll(1000, gena, fn)
}

fun <A> forAll(attempts: Int, gena: Gen<A>, fn: (a: A) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val passed = fn(a)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B> forAll(noinline fn: (a: A, b: B) -> Boolean) {
  forAll(Gen.default(), Gen.default(), fn)
}


inline fun <reified A, reified B> forAll(attempts: Int, noinline fn: (a: A, b: B) -> Boolean) {
  forAll(attempts, Gen.default(), Gen.default(), fn)
}

fun <A, B> forAll(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  forAll(1000, gena, genb, fn)
}

fun <A, B> forAll(attempts: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  if (attempts <= 0)
    throw IllegalArgumentException("The number of attempts should be a positive number")

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val passed = fn(a, b)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C> forAll(attempts: Int, noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forAll(attempts, Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C> forAll(noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) {
  forAll(1000, gena, genb, genc, fn)
}

fun <A, B, C> forAll(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val passed = fn(a, b, c)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\n$c\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> forAll(noinline fn: (a: A, b: B, c: C, D) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D> forAll(attempts: Int, noinline fn: (a: A, b: B, c: C, D) -> Boolean) {
  forAll(attempts, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) {
  forAll(1000, gena, genb, genc, gend, fn)
}

fun <A, B, C, D> forAll(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val passed = fn(a, b, c, d)
    if (!passed) {
      throw AssertionError("Property failed for \n$a\n$b\n$c\n$d\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E> forAll(attempts: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forAll(attempts, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forAll(1000, gena, genb, genc, gend, gene, fn)
}

fun <A, B, C, D, E> forAll(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val passed = fn(a, b, c, d, e)
    if (!passed) {
      throw AssertionError("Property failed for \n$a\n$b\n$c\n$d\n$e\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(attempts: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forAll(attempts, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forAll(1000, gena, genb, genc, gend, gene, genf, fn)
}

fun <A, B, C, D, E, F> forAll(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val f = genf.generate()
    val passed = fn(a, b, c, d, e, f)
    if (!passed) {
      throw AssertionError("Property failed for \n$a\n$b\n$c\n$d\n$e\n$f\nafter $k attempts")
    }
  }
}

private fun checkNumberOfAttemptsIsAPositiveNumber(attempts: Int) {
  if (attempts <= 0)
    throw IllegalArgumentException("Attempts should be a positive number")
}

inline fun <reified A> forNone(noinline fn: (a: A) -> Boolean) {
  forNone(Gen.default(), fn)
}

inline fun <reified A> forNone(attempts: Int, noinline fn: (a: A) -> Boolean) {
  forNone(attempts, Gen.default(), fn)
}

fun <A> forNone(gena: Gen<A>, fn: (a: A) -> Boolean) {
  forNone(1000, gena, fn)
}

fun <A> forNone(attempts: Int, gena: Gen<A>, fn: (a: A) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val passed = fn(a)
    if (passed) {
      throw AssertionError("Property passed for\n$a\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B> forNone(noinline fn: (a: A, b: B) -> Boolean) {
  forNone(Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B> forNone(attempts: Int, noinline fn: (a: A, b: B) -> Boolean) {
  forNone(attempts, Gen.default(), Gen.default(), fn)
}

fun <A, B> forNone(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  forNone(1000, gena, genb, fn)
}

fun <A, B> forNone(attempts: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val passed = fn(a, b)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C> forNone(noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forNone(Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C> forNone(attempts: Int, noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forNone(attempts, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) {
  forNone(1000, gena, genb, genc, fn)
}

fun <A, B, C> forNone(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val passed = fn(a, b, c)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\n$c\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> forNone(attempts: Int, noinline fn: (a: A, b: B, c: C, D) -> Boolean) {
  forNone(attempts, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D> forNone(noinline fn: (a: A, b: B, c: C, D) -> Boolean) {
  forNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) {
  forNone(1000, gena, genb, genc, gend, fn)
}

fun <A, B, C, D> forNone(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val passed = fn(a, b, c, d)
    if (passed) {
      throw AssertionError("Property passed for \n$a\n$b\n$c\n$d\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(attempts: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forNone(attempts, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forNone(1000, gena, genb, genc, gend, gene, fn)
}

fun <A, B, C, D, E> forNone(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val passed = fn(a, b, c, d, e)
    if (passed) {
      throw AssertionError("Property passed for \n$a\n$b\n$c\n$d\n$e\nafter $k attempts")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(attempts: Int, noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(attempts, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forNone(1000, gena, genb, genc, gend, gene, genf, fn)
}

fun <A, B, C, D, E, F> forNone(attempts: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  checkNumberOfAttemptsIsAPositiveNumber(attempts)

  for (k in 1..attempts) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val f = genf.generate()
    val passed = fn(a, b, c, d, e, f)
    if (passed) {
      throw AssertionError("Property passed for \n$a\n$b\n$c\n$d\n$e\n$f\nafter $k attempts")
    }
  }
}