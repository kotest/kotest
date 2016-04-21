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
      forAll(Generator.default<A>(), fn)
    }

    fun <A> forAll(gena: Generator<A>, fn: (a: A) -> Boolean): Unit {
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
      forAll(Generator.default<A>(), Generator.default<B>(), fn)
    }

    fun <A, B> forAll(gena: Generator<A>, genb: Generator<B>, fn: (a: A, b: B) -> Boolean): Unit {
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
      forAll(Generator.default<A>(), Generator.default<B>(), Generator.default<C>(), fn)
    }

    fun <A, B, C> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
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
      forAll(Generator.default<A>(), Generator.default<B>(), Generator.default<C>(), Generator.default<D>(), fn)
    }

    fun <A, B, C, D> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, gend: Generator<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
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
      forAll(Generator.default<A>(), Generator.default<B>(), Generator.default<C>(), Generator.default<D>(), Generator.default<E>(), fn)
    }

    fun <A, B, C, D, E> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, gend: Generator<D>, gene: Generator<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
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
      forAll(Generator.default<A>(), Generator.default<B>(), Generator.default<C>(), Generator.default<D>(), Generator.default<E>(), Generator.default<F>(), fn)
    }

    fun <A, B, C, D, E, F> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, gend: Generator<D>, gene: Generator<E>, genf: Generator<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
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