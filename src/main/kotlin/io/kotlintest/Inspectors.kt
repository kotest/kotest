package io.kotlintest

interface Inspectors {

  fun <T> forAll(col: Collection<T>, f: (T) -> Unit): Unit {
    val size = col.size
    val passed = count(col, f)
    if (size != passed)
      throw TestFailedException("$passed passed tests but expected $size")
  }

  fun <T> forOne(col: Collection<T>, f: (T) -> Unit): Unit = forExactly(1, col, f)

  fun <T> forExactly(k: Int, col: Collection<T>, f: (T) -> Unit): Unit {
    val passed = count(col, f)
    if (k != passed)
      throw TestFailedException("$passed passed tests but expected $k")
  }

  fun <T> forSome(col: Collection<T>, f: (T) -> Unit): Unit {
    val size = col.size
    val passed = count(col, f)
    if (passed == size)
      throw TestFailedException("All elements passed tests but expected < $size")
    else if (passed == 0)
      throw TestFailedException("No elements passed tests but expected > 0")
  }

  fun <T> forAny(col: Collection<T>, f: (T) -> Unit): Unit = forAtLeast(1, col, f)

  fun <T> forAtLeast(k: Int, col: Collection<T>, f: (T) -> Unit): Unit {
    val passed = count(col, f)
    if (passed < k)
      throw TestFailedException("$passed passed tests but expected at least $k")
  }

  fun <T> forAtMostOne(col: Collection<T>, f: (T) -> Unit): Unit = forAtMost(1, col, f)

  fun <T> forAtMost(k: Int, col: Collection<T>, f: (T) -> Unit): Unit {
    val passed = count(col, f)
    if (passed > k)
      throw TestFailedException("$passed passed tests but expected at most $k")
  }

  fun <T> forNone(k: Int, col: Collection<T>, f: (T) -> Unit): Unit {
    val passed = count(col, f)
    if (passed > 0)
      throw TestFailedException("$passed passed tests but expected 0")
  }

  fun <T> count(col: Collection<T>, f: (T) -> Unit): Int {
    return col.map { t ->
      try {
        f(t)
        1
      } catch (e: Exception) {
        0
      }
    }.sum()
  }
}
