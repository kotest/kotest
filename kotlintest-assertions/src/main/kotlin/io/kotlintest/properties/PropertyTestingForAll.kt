package io.kotlintest.properties

inline fun <reified A> forAll(noinline fn: (a: A) -> Boolean) = forAll(1000, fn)
inline fun <reified A> forAll(iterations: Int, noinline fn: (a: A) -> Boolean) {
  forAll(iterations, Gen.default(), fn)
}

fun <A> forAll(gena: Gen<A>, fn: (a: A) -> Boolean) = forAll(1000, gena, fn)
fun <A> forAll(iterations: Int, gena: Gen<A>, fn: (a: A) -> Boolean) {
  assert(iterations > 0, { "Iterations should be a positive number" })

  var attempts = 0
  for (a in gena.generate(iterations)) {
    attempts++
    val passed = fn(a)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\nafter $attempts attempts")
    }
  }
}

inline fun <reified A, reified B> forAll(noinline fn: (a: A, b: B) -> Boolean) = forAll(30, fn)
inline fun <reified A, reified B> forAll(iterations: Int, noinline fn: (a: A, b: B) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> forAll(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) = forAll(30, gena, genb, fn)
fun <A, B> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean) {
  assert(iterations > 0, { "Iterations should be a positive number" })

  var attempts = 0
  for (a in gena.generate(iterations)) {
    for (b in genb.generate(iterations)) {
      attempts++
      val passed = fn(a, b)
      if (!passed) {
        throw AssertionError("Property failed for\n$a\n$b\nafter $attempts attempts")
      }
    }
  }
}

inline fun <reified A, reified B, reified C> forAll(noinline fn: (a: A, b: B, c: C) -> Boolean) = forAll(10, fn)
inline fun <reified A, reified B, reified C> forAll(iterations: Int, noinline fn: (a: A, b: B, c: C) -> Boolean) {
  forAll(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) =
    forAll(10, gena, genb, genc, fn)

fun <A, B, C> forAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean) {
  assert(iterations > 0, { "Iterations should be a positive number" })

  var attempts = 0
  for (a in gena.generate(iterations)) {
    for (b in genb.generate(iterations)) {
      for (c in genc.generate(iterations)) {
        attempts++
        val passed = fn(a, b, c)
        if (!passed) {
          throw AssertionError("Property failed for\n$a\n$b\n$c\nafter $attempts attempts")
        }
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> forAll(noinline fn: (a: A, b: B, c: C, D) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean) {
  var attempts = 0
  for (a in gena.generate(5)) {
    for (b in genb.generate(5)) {
      for (c in genc.generate(5)) {
        for (d in gend.generate(5)) {
          attempts++
          val passed = fn(a, b, c, d)
          if (!passed) {
            throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\nafter $attempts attempts")
          }
        }
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  var attempts = 0
  for (a in gena.generate(5)) {
    for (b in genb.generate(5)) {
      for (c in genc.generate(5)) {
        for (d in gend.generate(5)) {
          for (e in gene.generate(5)) {
            attempts++
            val passed = fn(a, b, c, d, e)
            if (!passed) {
              throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\n$e\nafter $attempts attempts")
            }
          }
        }
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  forAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  var attempts = 0
  for (a in gena.generate(5)) {
    for (b in genb.generate(5)) {
      for (c in genc.generate(5)) {
        for (d in gend.generate(5)) {
          for (e in gene.generate(5)) {
            for (f in genf.generate(5)) {
              attempts++
              val passed = fn(a, b, c, d, e, f)
              if (!passed) {
                throw AssertionError("Property failed for\n$a\n$b\n$c\n$d\n$e\n$f\nafter $attempts attempts")
              }
            }
          }
        }
      }
    }
  }
}