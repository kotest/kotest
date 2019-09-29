package io.kotest

import io.kotest.inspectors.ElementPass
import io.kotest.inspectors.buildAssertionError
import io.kotest.inspectors.runTests

@Deprecated("use the extension function version of this", ReplaceWith("array.forAll(fn)"))
fun <T> forAll(array: Array<T>, fn: (T) -> Unit) = forAll(array.asList(), fn)

@Deprecated("use the extension function version of this", ReplaceWith("col.forAll(fn)"))
fun <T> forAll(col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size < col.size) {
    val msg = "${passed.size} elements passed but expected ${col.size}"
    buildAssertionError(msg, results)
  }
}

@Deprecated("use the extension function version of this", ReplaceWith("array.forOne(fn)"))
fun <T> forOne(array: Array<T>, fn: (T) -> Unit) = forOne(array.asList(), fn)

@Deprecated("use the extension function version of this", ReplaceWith("col.forOne(fn)"))
fun <T> forOne(col: Collection<T>, f: (T) -> Unit) = forExactly(1, col, f)

@Deprecated("use the extension function version of this", ReplaceWith("array.forExactly(fn)"))
fun <T> forExactly(k: Int, array: Array<T>, f: (T) -> Unit) = forExactly(k, array.asList(), f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forExactly(fn)"))
fun <T> forExactly(k: Int, col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size != k) {
    val msg = "${passed.size} elements passed but expected $k"
    buildAssertionError(msg, results)
  }
}

@Deprecated("use the extension function version of this", ReplaceWith("array.forSome(fn)"))
fun <T> forSome(array: Array<T>, f: (T) -> Unit) = forSome(array.asList(), f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forSome(fn)"))
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

@Deprecated("use the extension function version of this", ReplaceWith("array.forAny(fn)"))
fun <T> forAny(array: Array<T>, f: (T) -> Unit) = forAny(array.asList(), f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forAny(fn)"))
fun <T> forAny(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

@Deprecated("use the extension function version of this", ReplaceWith("array.forAtLeastOne(fn)"))
fun <T> forAtLeastOne(array: Array<T>, f: (T) -> Unit) = forAtLeastOne(array.asList(), f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forAtLeastOne(fn)"))
fun <T> forAtLeastOne(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

@Deprecated("use the extension function version of this", ReplaceWith("array.forAtLeast(fn)"))
fun <T> forAtLeast(k: Int, array: Array<T>, f: (T) -> Unit) = forAtLeast(k, array.asList(), f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forAtLeast(fn)"))
fun <T> forAtLeast(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size < k) {
    val msg = "${passed.size} elements passed but expected at least $k"
    buildAssertionError(msg, results)
  }
}

@Deprecated("use the extension function version of this", ReplaceWith("array.forAtMostOne(fn)"))
fun <T> forAtMostOne(array: Array<T>, f: (T) -> Unit) = forAtMost(1, array.asList(), f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forAtMostOne(fn)"))
fun <T> forAtMostOne(col: Collection<T>, f: (T) -> Unit) = forAtMost(1, col, f)

@Deprecated("use the extension function version of this", ReplaceWith("col.forAtMost(fn)"))
fun <T> forAtMost(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filterIsInstance<ElementPass<T>>()
  if (passed.size > k) {
    val msg = "${passed.size} elements passed but expected at most $k"
    buildAssertionError(msg, results)
  }
}

@Deprecated("use the extension function version of this", ReplaceWith("array.forNone(fn)"))
fun <T> forNone(array: Array<T>, testFn: (T) -> Unit) = forNone(array.asList(), testFn)

@Deprecated("use the extension function version of this", ReplaceWith("col.forNone(fn)"))
fun <T> forNone(col: Collection<T>, testFn: (T) -> Unit) = forExactly(0, col, testFn)
