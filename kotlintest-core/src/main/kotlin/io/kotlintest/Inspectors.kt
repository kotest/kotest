package io.kotlintest

fun <T> forAll(array: Array<T>, fn: (T) -> Unit) = forAll(array.asList(), fn)

fun <T> forAll(col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filter { it.second == null }
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
  val passed = results.filter { it.second == null }
  if (passed.size != k) {
    val msg = "${passed.size} elements passed but expected $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forSome(array: Array<T>, f: (T) -> Unit) = forSome(array.asList(), f)

fun <T> forSome(col: Collection<T>, fn: (T) -> Unit) {
  val size = col.size
  val results = runTests(col, fn)
  val passed = results.filter { it.second == null }
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
  val passed = results.filter { it.second == null }
  if (passed.size < k) {
    val msg = "${passed.size} elements passed but expected at least $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forAtMostOne(array: Array<T>, f: (T) -> Unit) = forAtMost(1, array.asList(), f)

fun <T> forAtMostOne(col: Collection<T>, f: (T) -> Unit) = forAtMost(1, col, f)

fun <T> forAtMost(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filter { it.second == null }
  if (passed.size > k) {
    val msg = "${passed.size} elements passed but expected at most $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forNone(array: Array<T>, f: (T) -> Unit) = forNone(array.asList(), f)

fun <T> forNone(col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filter { it.second == null }
  if (passed.isNotEmpty()) {
    val msg = "${passed.size} elements passed but expected 0"
    buildAssertionError(msg, results)
  }
}

private fun <T> buildAssertionError(msg: String, results: List<Pair<T, String?>>): String {

  val passed = results.filter { it.second == null }
  val failed = results.filter { it.second != null }

  val builder = StringBuilder(msg)
  builder.append("\n\nThe following elements passed:\n")
  if (passed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(passed.map { it.first }.joinToString("\n"))
  }
  builder.append("\n\nThe following elements failed:\n")
  if (failed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(failed.map { it.first.toString() + " => " + it.second }.joinToString("\n"))
  }
  throw AssertionError(builder.toString())
}

private fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<Pair<T, String?>> {
  return col.map { t ->
    try {
      f(t)
      Pair(t, null)
    } catch (e: Throwable) {
      Pair(t, e.message)
    }
  }
}
