package io.kotlintest

fun <T> forAll(array: Array<T>, fn: (T) -> Unit) = forAll(array.asList(), fn)

fun <T> forAll(col: Collection<T>, fn: (T) -> Unit) {
  val size = col.size
  val passed = count(col, fn)
  if (size != passed)
    throw AssertionError("$passed passed tests but expected $size")
}

fun <T> forOne(array: Array<T>, fn: (T) -> Unit) = forOne(array.asList(), fn)

fun <T> forOne(col: Collection<T>, f: (T) -> Unit) = forExactly(1, col, f)

fun <T> forExactly(k: Int, array: Array<T>, f: (T) -> Unit) = forExactly(k, array.asList(), f)

fun <T> forExactly(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val passed = count(col, f)
  if (k != passed)
    throw AssertionError("$passed passed tests but expected $k")
}

fun <T> forSome(array: Array<T>, f: (T) -> Unit) = forSome(array.asList(), f)

fun <T> forSome(col: Collection<T>, f: (T) -> Unit) {
  val size = col.size
  val passed = count(col, f)
  if (passed == size)
    throw AssertionError("All elements passed tests but expected < $size")
  else if (passed == 0)
    throw AssertionError("No elements passed tests but expected > 0")
}

fun <T> forAny(array: Array<T>, f: (T) -> Unit) = forAny(array.asList(), f)

fun <T> forAny(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

fun <T> forAtLeastOne(array: Array<T>, f: (T) -> Unit) = forAtLeastOne(array.asList(), f)

fun <T> forAtLeastOne(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

fun <T> forAtLeast(k: Int, array: Array<T>, f: (T) -> Unit) = forAtLeast(k, array.asList(), f)

fun <T> forAtLeast(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val passed = count(col, f)
  if (passed < k)
    throw AssertionError("$passed passed tests but expected at least $k")
}

fun <T> forAtMostOne(array: Array<T>, f: (T) -> Unit) = forAtMost(1, array.asList(), f)

fun <T> forAtMostOne(col: Collection<T>, f: (T) -> Unit) = forAtMost(1, col, f)

fun <T> forAtMost(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val passed = count(col, f)
  if (passed > k)
    throw AssertionError("$passed passed tests but expected at most $k")
}

fun <T> forNone(array: Array<T>, f: (T) -> Unit) = forNone(array.asList(), f)

fun <T> forNone(col: Collection<T>, f: (T) -> Unit) {
  val passed = count(col, f)
  if (passed > 0)
    throw AssertionError("$passed passed tests but expected 0")
}

fun <T> count(col: Collection<T>, f: (T) -> Unit): Int {
  return col.map { t ->
    try {
      f(t)
      1
    } catch (e: Throwable) {
      0
    }
  }.sum()
}
