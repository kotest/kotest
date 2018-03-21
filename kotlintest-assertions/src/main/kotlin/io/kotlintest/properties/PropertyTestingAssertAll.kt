package io.kotlintest.properties

inline fun <reified A> assertAll(noinline fn: (a: A) -> Unit) {
  assertAll(Gen.default(), fn)
}

fun <A> assertAll(gena: Gen<A>, fn: (a: A) -> Unit) {
  for (a in gena.generate(100)) {
    fn(a)
  }
}

inline fun <reified A, reified B> assertAll(noinline fn: (a: A, b: B) -> Unit) {
  assertAll(Gen.default(), Gen.default(), fn)
}

fun <A, B> assertAll(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Unit) {
  for (a in gena.generate(30)) {
    for (b in genb.generate(30)) {
      fn(a, b)
    }
  }
}

inline fun <reified A, reified B, reified C> assertAll(noinline fn: (a: A, b: B, c: C) -> Unit) {
  assertAll(Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Unit) {
  for (a in gena.generate(10)) {
    for (b in genb.generate(10)) {
      for (c in genc.generate(10)) {
        fn(a, b, c)
      }
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> assertAll(noinline fn: (a: A, b: B, c: C, D) -> Unit) {
  assertAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Unit) {
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

inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
  assertAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Unit) {
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

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  assertAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
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


