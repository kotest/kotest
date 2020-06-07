package io.kotest.inspectors

fun <T> Sequence<T>.forAll(fn: (T) -> Unit) = toList().forAll(fn)
fun <T> Array<T>.forAll(fn: (T) -> Unit) = asList().forAll(fn)
fun <T> Collection<T>.forAll(fn: (T) -> Unit) {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < this.size) {
      val msg = "${passed.size} elements passed but expected ${this.size}"
      buildAssertionError(msg, results)
   }
}

fun <T> Sequence<T>.forOne(fn: (T) -> Unit) = toList().forOne(fn)
fun <T> Array<T>.forOne(fn: (T) -> Unit) = asList().forOne(fn)
fun <T> Collection<T>.forOne(fn: (T) -> Unit) = forExactly(1, fn)

fun <T> Sequence<T>.forExactly(k: Int, fn: (T) -> Unit) = toList().forExactly(k, fn)
fun <T> Array<T>.forExactly(k: Int, fn: (T) -> Unit) = toList().forExactly(k, fn)
fun <T> Collection<T>.forExactly(k: Int, fn: (T) -> Unit) {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size != k) {
    val msg = "${passed.size} elements passed but expected $k"
    buildAssertionError(msg, results)
  }
}

fun <T> Sequence<T>.forSome(fn: (T) -> Unit) = toList().forSome(fn)
fun <T> Array<T>.forSome(fn: (T) -> Unit) = toList().forSome(fn)
fun <T> Collection<T>.forSome(fn: (T) -> Unit) {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isEmpty()) {
      buildAssertionError("No elements passed but expected at least one", results)
   } else if (passed.size == size) {
      buildAssertionError("All elements passed but expected < $size", results)
   }
}

fun <T> Sequence<T>.forAny(fn: (T) -> Unit) = toList().forAny(fn)
fun <T> Array<T>.forAny(fn: (T) -> Unit) = toList().forAny(fn)
fun <T> Collection<T>.forAny(fn: (T) -> Unit) = forAtLeastOne(fn)

fun <T> Sequence<T>.forAtLeastOne(fn: (T) -> Unit) = toList().forAtLeastOne(fn)
fun <T> Array<T>.forAtLeastOne(fn: (T) -> Unit) = toList().forAtLeastOne(fn)
fun <T> Collection<T>.forAtLeastOne(f: (T) -> Unit) = forAtLeast(1, f)

fun <T> Sequence<T>.forAtLeast(k: Int, fn: (T) -> Unit) = toList().forAtLeast(k, fn)
fun <T> Array<T>.forAtLeast(k: Int, fn: (T) -> Unit) = toList().forAtLeast(k, fn)
fun <T> Collection<T>.forAtLeast(k: Int, fn: (T) -> Unit) {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < k) {
    val msg = "${passed.size} elements passed but expected at least $k"
    buildAssertionError(msg, results)
  }
}

fun <T> Sequence<T>.forAtMostOne(fn: (T) -> Unit) = toList().forAtMostOne(fn)
fun <T> Array<T>.forAtMostOne(fn: (T) -> Unit) = toList().forAtMostOne(fn)
fun <T> Collection<T>.forAtMostOne(fn: (T) -> Unit) = forAtMost(1, fn)

fun <T> Sequence<T>.forAtMost(k: Int, fn: (T) -> Unit) = toList().forAtMost(k, fn)
fun <T> Array<T>.forAtMost(k: Int, fn: (T) -> Unit) = toList().forAtMost(k, fn)
fun <T> Collection<T>.forAtMost(k: Int, fn: (T) -> Unit) {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size > k) {
      val msg = "${passed.size} elements passed but expected at most $k"
      buildAssertionError(msg, results)
   }
}

fun <T> Sequence<T>.forNone(fn: (T) -> Unit) = toList().forNone(fn)
fun <T> Array<T>.forNone(fn: (T) -> Unit) = toList().forNone(fn)
fun <T> Collection<T>.forNone(f: (T) -> Unit) {
   val results = runTests(this, f)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size != 0) {
    val msg = "${passed.size} elements passed but expected ${0}"
    buildAssertionError(msg, results)
  }
}
