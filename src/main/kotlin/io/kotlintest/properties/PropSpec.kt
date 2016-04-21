package io.kotlintest.properties

import io.kotlintest.TestBase
import io.kotlintest.TestCase
import io.kotlintest.TestFailedException
import io.kotlintest.TestSuite

abstract class PropSpec : TestBase() {

  var current = root
  var name = ""

  fun property(name: String, init: () -> Unit): Unit {
    this.name = name
    val suite = TestSuite.empty(name)
    current.suites.add(suite)
    val temp = current
    current = suite
    init()
    current = temp
  }

  fun <A> forAll(gena: Generator<A>, fn: (a: A) -> Boolean): Unit {
    current.cases.add(TestCase("forAll", {
      for (k in 0..1000) {
        val a = gena.generate()
        val passed = fn(a)
        if (!passed) {
          throw TestFailedException("Property $name failed for ($a)")
        }
      }
    }))
  }

  fun <A, B> forAll(gena: Generator<A>, genb: Generator<B>, fn: (a: A, b: B) -> Boolean): Unit {
    current.cases.add(TestCase("forAll", {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val passed = fn(a, b)
        if (!passed) {
          throw TestFailedException("Property $name failed for ($a, $b)")
        }
      }
    }))
  }

  fun <A, B, C> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, fn: (a: A, b: B, c: C) -> Boolean): Unit {
    current.cases.add(TestCase("forAll", {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val passed = fn(a, b, c)
        if (!passed) {
          throw TestFailedException("Property $name failed for ($a, $b, $c)")
        }
      }
    }))
  }

  fun <A, B, C, D> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, gend: Generator<D>, fn: (a: A, b: B, c: C, d: D) -> Boolean): Unit {
    current.cases.add(TestCase("forAll", {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val passed = fn(a, b, c, d)
        if (!passed) {
          throw TestFailedException("Property $name failed for ($a, $b, $c, $d)")
        }
      }
    }))
  }

  fun <A, B, C, D, E> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, gend: Generator<D>, gene: Generator<E>, fn: (a: A, b: B, c: C, d: D, e: E) -> Boolean): Unit {
    current.cases.add(TestCase("forAll", {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val e = gene.generate()
        val passed = fn(a, b, c, d, e)
        if (!passed) {
          throw TestFailedException("Property $name failed for ($a, $b, $c, $d, $e)")
        }
      }
    }))
  }

  fun <A, B, C, D, E, F> forAll(gena: Generator<A>, genb: Generator<B>, genc: Generator<C>, gend: Generator<D>, gene: Generator<E>, genf: Generator<F>, fn: (a: A, b: B, c: C, d: D, e: E, f: F) -> Boolean): Unit {
    current.cases.add(TestCase("forAll", {
      for (k in 0..1000) {
        val a = gena.generate()
        val b = genb.generate()
        val c = genc.generate()
        val d = gend.generate()
        val e = gene.generate()
        val f = genf.generate()
        val passed = fn(a, b, c, d, e, f)
        if (!passed) {
          throw TestFailedException("Property $name failed for ($a, $b, $c, $d, $e, $f)")
        }
      }
    }))
  }
}