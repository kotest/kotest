package io.kotlintest

import io.kotlintest.inspectors.ElementPass
import io.kotlintest.inspectors.buildAssertionError
import io.kotlintest.inspectors.runTests

fun <T> forAll(array: Array<T>, fn: (T) -> Unit) = forAll(array.asList(), fn)

fun <T> forAll(col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size < col.size) {
    val msg = "${passed.size} elements passed but expected ${col.size}"
    buildAssertionError(msg, results)
  }
}

fun <T> forOne(array: Array<T>, fn: (T) -> Unit) = forOne(array.asList(), fn)

fun <T> forOne(col: Collection<T>, f: (T) -> Unit) = forExactly(1, col, f)

fun <T> forExactly(k: Int, array: Array<T>, f: (T) -> Unit) = forExactly(k, array.asList(), f)

fun <T> forExactly(k: Int, col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size != k) {
    val msg = "${passed.size} elements passed but expected $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forSome(array: Array<T>, f: (T) -> Unit) = forSome(array.asList(), f)

fun <T> forSome(col: Collection<T>, fn: (T) -> Unit) {
  val size = col.size
  val results = runTests(col, fn)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.isEmpty()) {
    buildAssertionError("No elements passed but expected at least one", results)
  } else if (passed.size == size) {
    buildAssertionError("All elements passed but expected < $size", results)
  }
}

fun <T> forAny(array: Array<T>, f: (T) -> Unit) = forAny(array.asList(), f)

fun <T> forAny(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

fun <T> forAtLeastOne(array: Array<T>, f: (T) -> Unit) = forAtLeastOne(array.asList(), f)

fun <T> forAtLeastOne(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

fun <T> forAtLeast(k: Int, array: Array<T>, f: (T) -> Unit) = forAtLeast(k, array.asList(), f)

fun <T> forAtLeast(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size < k) {
    val msg = "${passed.size} elements passed but expected at least $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forAtMostOne(array: Array<T>, f: (T) -> Unit) = forAtMost(1, array.asList(), f)

fun <T> forAtMostOne(col: Collection<T>, f: (T) -> Unit) = forAtMost(1, col, f)

fun <T> forAtMost(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size > k) {
    val msg = "${passed.size} elements passed but expected at most $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forNone(array: Array<T>, testFn: (T) -> Unit) = forNone(array.asList(), testFn)
fun <T> forNone(col: Collection<T>, testFn: (T) -> Unit) = forExactly(0, col, testFn)