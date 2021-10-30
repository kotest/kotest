package io.kotest.inspectors

fun <T> Sequence<T>.forAll(fn: (T) -> Unit): Sequence<T> = apply { toList().forAll(fn) }
fun <T> Array<T>.forAll(fn: (T) -> Unit): Array<T> = apply { asList().forAll(fn) }
fun <T, C : Collection<T>> C.forAll(fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < this.size) {
      val msg = "${passed.size} elements passed but expected ${this.size}"
      buildAssertionError(msg, results)
   }
}

fun <T> Sequence<T>.forOne(fn: (T) -> Unit): Sequence<T> = apply { toList().forOne(fn) }
fun <T> Array<T>.forOne(fn: (T) -> Unit): Array<T> = apply { asList().forOne(fn) }
fun <T, C : Collection<T>> C.forOne(fn: (T) -> Unit): C = forExactly(1, fn)

fun <T> Sequence<T>.forExactly(k: Int, fn: (T) -> Unit): Sequence<T> = apply { toList().forExactly(k, fn) }
fun <T> Array<T>.forExactly(k: Int, fn: (T) -> Unit): Array<T> = apply { toList().forExactly(k, fn) }
fun <T, C : Collection<T>> C.forExactly(k: Int, fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size != k) {
      val msg = "${passed.size} elements passed but expected $k"
      buildAssertionError(msg, results)
   }
}

fun <T> Sequence<T>.forSome(fn: (T) -> Unit): Sequence<T> = apply { toList().forSome(fn) }
fun <T> Array<T>.forSome(fn: (T) -> Unit): Array<T> = apply { toList().forSome(fn) }
fun <T, C : Collection<T>> C.forSome(fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isEmpty()) {
      buildAssertionError("No elements passed but expected at least one", results)
   } else if (passed.size == size) {
      buildAssertionError("All elements passed but expected < $size", results)
   }
}

fun <T> Sequence<T>.forAny(fn: (T) -> Unit): Sequence<T> = apply { toList().forAny(fn) }
fun <T> Array<T>.forAny(fn: (T) -> Unit): Array<T> = apply { toList().forAny(fn) }
fun <T, C : Collection<T>> C.forAny(fn: (T) -> Unit): C = apply { forAtLeastOne(fn) }

fun <T> Sequence<T>.forAtLeastOne(fn: (T) -> Unit): Sequence<T> = apply { toList().forAtLeastOne(fn) }
fun <T> Array<T>.forAtLeastOne(fn: (T) -> Unit): Array<T> = apply { toList().forAtLeastOne(fn) }
fun <T, C : Collection<T>> C.forAtLeastOne(f: (T) -> Unit) = forAtLeast(1, f)

fun <T> Sequence<T>.forAtLeast(k: Int, fn: (T) -> Unit): Sequence<T> = apply { toList().forAtLeast(k, fn) }
fun <T> Array<T>.forAtLeast(k: Int, fn: (T) -> Unit): Array<T> = apply { toList().forAtLeast(k, fn) }
fun <T, C : Collection<T>> C.forAtLeast(k: Int, fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < k) {
      val msg = "${passed.size} elements passed but expected at least $k"
      buildAssertionError(msg, results)
   }
}

fun <T> Sequence<T>.forAtMostOne(fn: (T) -> Unit): Sequence<T> = apply { toList().forAtMostOne(fn) }
fun <T> Array<T>.forAtMostOne(fn: (T) -> Unit): Array<T> = apply { toList().forAtMostOne(fn) }
fun <T, C : Collection<T>> C.forAtMostOne(fn: (T) -> Unit) = forAtMost(1, fn)

fun <T> Sequence<T>.forAtMost(k: Int, fn: (T) -> Unit): Sequence<T> = apply { toList().forAtMost(k, fn) }
fun <T> Array<T>.forAtMost(k: Int, fn: (T) -> Unit): Array<T> = apply { toList().forAtMost(k, fn) }
fun <T, C : Collection<T>> C.forAtMost(k: Int, fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size > k) {
      val msg = "${passed.size} elements passed but expected at most $k"
      buildAssertionError(msg, results)
   }
}

fun <T> Sequence<T>.forNone(fn: (T) -> Unit): Sequence<T> = apply { toList().forNone(fn) }
fun <T> Array<T>.forNone(fn: (T) -> Unit): Array<T> = apply { toList().forNone(fn) }
fun <T, C : Collection<T>> C.forNone(f: (T) -> Unit): C = apply {
   val results = runTests(this, f)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isNotEmpty()) {
      val msg = "${passed.size} elements passed but expected ${0}"
      buildAssertionError(msg, results)
   }
}
