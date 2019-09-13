package io.kotest.properties

import io.kotest.shouldBe

inline fun <reified A> verifyAll(noinline fn: PropertyContext.(a: A) -> Boolean) = verifyAll(1000, fn)
inline fun <reified A> verifyAll(iterations: Int, noinline fn: PropertyContext.(a: A) -> Boolean) {
  verifyAll(iterations, Gen.default(), fn)
}

fun <A> verifyAll(gena: Gen<A>, fn: PropertyContext.(a: A) -> Boolean) = verifyAll(1000, gena, fn)
fun <A> Gen<A>.verifyAll(iterations: Int = 1000, fn: PropertyContext.(a0: A) -> Boolean) = verifyAll(iterations, this, fn)
fun <A> Gen<A>.verifyAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A) -> Boolean) = verifyAll(iterations, this, this, this, fn)
fun <A> Gen<A>.verifyAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A) -> Boolean) = verifyAll(iterations, this, this, this, this, fn)
fun <A> Gen<A>.verifyAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A) -> Boolean) = verifyAll(iterations, this, this, this, this, this, fn)
fun <A> Gen<A>.verifyAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A, a5: A) -> Boolean) = verifyAll(iterations, this, this, this, this, this, this, fn)
fun <A> verifyAll(iterations: Int, gena: Gen<A>, fn: PropertyContext.(a: A) -> Boolean) {
  assertAll(iterations, gena) { a ->
    fn(a) shouldBe true
  }
}

inline fun <reified A, reified B> verifyAll(noinline fn: PropertyContext.(a: A, b: B) -> Boolean) = verifyAll(1000, fn)
inline fun <reified A, reified B> verifyAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B) -> Boolean) {
  verifyAll(iterations, Gen.default(), Gen.default(), fn)
}

fun <A, B> verifyAll(gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Boolean) = verifyAll(1000, gena, genb, fn)
fun <A, B> verifyAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Boolean) {
  assertAll(iterations, gena, genb) { a, b ->
    fn(a, b) shouldBe true
  }
}

inline fun <reified A, reified B, reified C> verifyAll(noinline fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) = verifyAll(1000, fn)
inline fun <reified A, reified B, reified C> verifyAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  verifyAll(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C> verifyAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) =
    verifyAll(1000, gena, genb, genc, fn)

fun <A, B, C> verifyAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Boolean) {
  assertAll(iterations, gena, genb, genc) { a, b, c ->
    fn(a, b, c) shouldBe true
  }
}

inline fun <reified A, reified B, reified C, reified D> verifyAll(noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Boolean) {
  verifyAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D> verifyAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, D) -> Boolean) {
  verifyAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D> verifyAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Boolean) =
    verifyAll(1000, gena, genb, genc, gend, fn)

fun <A, B, C, D> verifyAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Boolean) {
  assertAll(iterations, gena, genb, genc, gend) { a, b, c, d ->
    fn(a, b, c, d) shouldBe true
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> verifyAll(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  verifyAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E> verifyAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  verifyAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E> verifyAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) =
    verifyAll(1000, gena, genb, genc, gend, gene, fn)

fun <A, B, C, D, E> verifyAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Boolean) {
  assertAll(iterations, gena, genb, genc, gend, gene) { a, b, c, d, e ->
    fn(a, b, c, d, e) shouldBe true
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> verifyAll(noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  verifyAll(1000, fn)
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> verifyAll(iterations: Int, noinline fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  verifyAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
}

fun <A, B, C, D, E, F> verifyAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>,
                              fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) =
    verifyAll(1000, gena, genb, genc, gend, gene, genf, fn)

fun <A, B, C, D, E, F> verifyAll(iterations: Int, gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>,
                              fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean) {
  assertAll(iterations, gena, genb, genc, gend, gene, genf) { a, b, c, d, e, f ->
    fn(a, b, c, d, e, f) shouldBe true
  }
}
