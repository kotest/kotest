package io.kotlintest.properties

inline fun <reified A> assertNone(noinline fn: (a: A) -> Unit) {
  assertNone(Gen.default(), fn)
}

fun <A> assertNone(gena: Gen<A>, fn: (a: A) -> Unit) {
  for (a in gena.generate(100)) {
    val passed = try {
      fn(a)
      true
    } catch (e: AssertionError) {
      false
    }
    if (passed)
      throw AssertionError("Property passed for \n$a")
  }
}

inline fun <reified A, reified B> assertNone(noinline fn: (a: A, b: B) -> Unit) {
  assertNone(Gen.default(), Gen.default(), fn)
}

fun <A, B> assertNone(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Unit) {
  for (a in gena.generate(30)) {
    for (b in genb.generate(30)) {
      val passed = try {
        fn(a, b)
        true
      } catch (e: AssertionError) {
        false
      }
      if (passed)
        throw AssertionError("Property passed for \n$a\n$b")
    }
  }
}

inline fun <reified A, reified B, reified C> assertNone(noinline fn: (a: A, b: B, c: C) -> Unit) {
  assertNone(Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Unit) {
  for (a in gena.generate(10)) {
    for (b in genb.generate(10)) {
      for (c in genc.generate(10)) {
        fn(a, b, c)
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> assertNone(noinline fn: (a: A, b: B, c: C, D) -> Unit) {
  assertNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Unit) {
  for (a in gena.generate(5)) {
    for (b in genb.generate(5)) {
      for (c in genc.generate(5)) {
        for (d in gend.generate(5)) {
          fn(a, b, c, d)
        }
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> assertNone(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  for (a in gena.generate(5)) {
    for (b in genb.generate(5)) {
      for (c in genc.generate(5)) {
        for (d in gend.generate(5)) {
          for (e in gene.generate(5)) {
            fn(a, b, c, d, e)
          }
        }
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertNone(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertNone(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> assertNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  for (a in gena.generate(5)) {
    for (b in genb.generate(5)) {
      for (c in genc.generate(5)) {
        for (d in gend.generate(5)) {
          for (e in gene.generate(5)) {
            for (f in genf.generate(5)) {
              fn(a, b, c, d, e, f)
            }
          }
        }
      }
    }
  }
}


