package io.kotlintest.properties

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestFailedException
import io.kotlintest.TestSuite

abstract class PropSpec : TestBase() {

  fun property(name: String): PropertyTestBuilder {
    return PropertyTestBuilder(root, name)
  }

  class PropertyTestBuilder(val suite: TestSuite, val name: String) {

    inline fun <reified A> forAll(noinline fn: (a: A) -> Boolean): Unit {
      forAll(Gen.default<A>(), fn)
    }

    fun <A> forAll(gena: Gen<A>, fn: (a: A) -> Boolean): Unit {
      suite.cases.add(TestCase(name, {
        for (k in 0..1000) {
          val a = gena.generate()
          val passed = fn(a)
          if (!passed) {
            throw TestFailedException("Property '$name' failed for\n$a")
          }
        }
      }))
    }

    inline fun <reified A, reified B> forAll(noinline fn: (a: A, b: B) -> Boolean): Unit {
      forAll(Gen.default<A>(), Gen.default<B>(), fn)
    }

    fun <A, B> forAll(gena: Gen<A>, genb: Gen<B>, fn: (a: A, b: B) -> Boolean): Unit {
      suite.cases.add(TestCase(name, {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val passed = fn(a, b)
        if (!passed) {
          throw TestFailedException("Property '$name' failed for\n$a\n$b)")
        }
      }
      }))
    }

    inline fun <reified A, reified B, reified C> forAll(noinline fn: (a: A, b: B, c: C) -> Boolean): Unit {
      forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), fn)
    }

    fun <A, B, C> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
      suite.cases.add(TestCase(name, {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val passed = fn(a, b, c)
        if (!passed) {
          throw TestFailedException("Property '$name' failed for\n$a\n$b\n$c)")
        }
      }
      }))
    }

    inline fun <reified A, reified B, reified C, reified D> forAll(noinline fn: (a: A, b: B, c: C, D) -> Boolean): Unit {
      forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), fn)
    }

    fun <A, B, C, D> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
      suite.cases.add(TestCase(name, {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val passed = fn(a, b, c, d)
        if (!passed) {
          throw TestFailedException("Property '$name' failed for \n$a\n$b\n$c\n$d)")
        }
      }
      }))
    }

    inline fun <reified A, reified B, reified C, reified D, reified E> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
      forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), Gen.default<E>(), fn)
    }

    fun <A, B, C, D, E> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
      suite.cases.add(TestCase(name, {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val e = gene.generate()
        val passed = fn(a, b, c, d, e)
        if (!passed) {
          throw TestFailedException("Property '$name' failed for \n$a\n$b\n$c\n$d\n$e")
        }
      }
      }))
    }

    inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(noinline fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
      forAll(Gen.default<A>(), Gen.default<B>(), Gen.default<C>(), Gen.default<D>(), Gen.default<E>(), Gen.default<F>(), fn)
    }

    fun <A, B, C, D, E, F> forAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, gend: Gen<D>, gene: Gen<E>, genf: Gen<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
      suite.cases.add(TestCase(name, {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val e = gene.generate()
        val f = genf.generate()
        val passed = fn(a, b, c, d, e, f)
        if (!passed) {
          throw TestFailedException("Property '$name' failed for \n$a\n$b\n$c\n$d\n$e\n$f")
        }
      }
      }))
    }
  }
}