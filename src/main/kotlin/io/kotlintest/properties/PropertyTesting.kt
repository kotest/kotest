package io.kotlintest.properties

inline fun <reified A> forAll(noinline fn: (a: A) -> Boolean): Unit {
  forAll(Gen.default<A>(), fn)
}

fun <A> forAll(gena: Gen<A>, fn: (a: A) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val passed = fn(a)
    if (!passed) {
      throw AssertionError("Property failed for\n$a")
    }
  }
}

inline fun <reified A, reified B> forAll(noinline fn: (a: A, b: B) -> Boolean): Unit {
  forAll(Gen.default<A>(), Gen.default<B>(), fn)
}

fun <A, B> forAll(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val passed = fn(a, b)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b)")
    }
  }
}

inline fun <reified A, reified B, reified C> forAll(noinline fn: (a: A, b: B, c: C) -> Boolean): Unit {
  forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), fn)
}

fun <A, B, C> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val passed = fn(a, b, c)
    if (!passed) {
      throw AssertionError("Property failed for\n$a\n$b\n$c)")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> forAll(noinline fn: (a: A, b: B, c: C, D) -> Boolean): Unit {
  forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), fn)
}

fun <A, B, C, D> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val passed = fn(a, b, c, d)
    if (!passed) {
      throw AssertionError("Property failed for \n$a\n$b\n$c\n$d)")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
  forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), Gen.default<E>(), fn)
}

fun <A, B, C, D, E> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val passed = fn(a, b, c, d, e)
    if (!passed) {
      throw AssertionError("Property failed for \n$a\n$b\n$c\n$d\n$e")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
  forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), Gen.default<E>(), Gen.default<F>(), fn)
}

fun <A, B, C, D, E, F> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val f = genf.generate()
    val passed = fn(a, b, c, d, e, f)
    if (!passed) {
      throw AssertionError("Property failed for \n$a\n$b\n$c\n$d\n$e\n$f")
    }
  }
}

inline fun <reified A> forNone(noinline fn: (a: A) -> Boolean): Unit {
  forNone(Gen.default<A>(), fn)
}

fun <A> forNone(gena: Gen<A>, fn: (a: A) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val passed = fn(a)
    if (passed) {
      throw AssertionError("Property passed for\n$a")
    }
  }
}

inline fun <reified A, reified B> forNone(noinline fn: (a: A, b: B) -> Boolean): Unit {
  forNone(Gen.default<A>(), Gen.default<B>(), fn)
}

fun <A, B> forNone(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val passed = fn(a, b)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b)")
    }
  }
}

inline fun <reified A, reified B, reified C> forNone(noinline fn: (a: A, b: B, c: C) -> Boolean): Unit {
  forNone(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), fn)
}

fun <A, B, C> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val passed = fn(a, b, c)
    if (passed) {
      throw AssertionError("Property passed for\n$a\n$b\n$c)")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D> forNone(noinline fn: (a: A, b: B, c: C, D) -> Boolean): Unit {
  forNone(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), fn)
}

fun <A, B, C, D> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val passed = fn(a, b, c, d)
    if (passed) {
      throw AssertionError("Property passed for \n$a\n$b\n$c\n$d)")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> forNone(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
  forNone(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), Gen.default<E>(), fn)
}

fun <A, B, C, D, E> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val passed = fn(a, b, c, d, e)
    if (passed) {
      throw AssertionError("Property passed for \n$a\n$b\n$c\n$d\n$e")
    }
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
  forNone(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), Gen.default<E>(), Gen.default<F>(), fn)
}

fun <A, B, C, D, E, F> forNone(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
  for (k in 0..1000) {
    val a = gena.generate()
    val b = genb.generate()
    val c = genc.generate()
    val d = gend.generate()
    val e = gene.generate()
    val f = genf.generate()
    val passed = fn(a, b, c, d, e, f)
    if (passed) {
      throw AssertionError("Property passed for \n$a\n$b\n$c\n$d\n$e\n$f")
    }
  }
}