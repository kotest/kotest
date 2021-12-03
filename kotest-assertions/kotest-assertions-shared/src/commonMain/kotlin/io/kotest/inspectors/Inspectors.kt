package io.kotest.inspectors

import io.kotest.assertions.failure

inline fun <T> Sequence<T>.forAll(fn: (T) -> Unit): Sequence<T> = apply { toList().forAll(fn) }
inline fun <T> Array<T>.forAll(fn: (T) -> Unit): Array<T> = apply { asList().forAll(fn) }
inline fun <T, C : Collection<T>> C.forAll(fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < this.size) {
      val msg = "${passed.size} elements passed but expected ${this.size}"
      buildAssertionError(msg, results)
   }
}

inline fun <T> Sequence<T>.forOne(fn: (T) -> Unit): Sequence<T> = apply { toList().forOne(fn) }
inline fun <T> Array<T>.forOne(fn: (T) -> Unit): Array<T> = apply { asList().forOne(fn) }
inline fun <T, C : Collection<T>> C.forOne(fn: (T) -> Unit): C = forExactly(1, fn)

inline fun <T> Sequence<T>.forExactly(k: Int, fn: (T) -> Unit): Sequence<T> = apply { toList().forExactly(k, fn) }
inline fun <T> Array<T>.forExactly(k: Int, fn: (T) -> Unit): Array<T> = apply { toList().forExactly(k, fn) }
inline fun <T, C : Collection<T>> C.forExactly(k: Int, fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size != k) {
      val msg = "${passed.size} elements passed but expected $k"
      buildAssertionError(msg, results)
   }
}

inline fun <T> Sequence<T>.forSome(fn: (T) -> Unit): Sequence<T> = apply { toList().forSome(fn) }
inline fun <T> Array<T>.forSome(fn: (T) -> Unit): Array<T> = apply { toList().forSome(fn) }
inline fun <T, C : Collection<T>> C.forSome(fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isEmpty()) {
      buildAssertionError("No elements passed but expected at least one", results)
   } else if (passed.size == size) {
      buildAssertionError("All elements passed but expected < $size", results)
   }
}

inline fun <T> Sequence<T>.forAny(fn: (T) -> Unit): Sequence<T> = apply { toList().forAny(fn) }
inline fun <T> Array<T>.forAny(fn: (T) -> Unit): Array<T> = apply { toList().forAny(fn) }
inline fun <T, C : Collection<T>> C.forAny(fn: (T) -> Unit): C = apply { forAtLeastOne(fn) }

inline fun <T> Sequence<T>.forAtLeastOne(fn: (T) -> Unit): Sequence<T> = apply { toList().forAtLeastOne(fn) }
inline fun <T> Array<T>.forAtLeastOne(fn: (T) -> Unit): Array<T> = apply { toList().forAtLeastOne(fn) }
inline fun <T, C : Collection<T>> C.forAtLeastOne(f: (T) -> Unit) = forAtLeast(1, f)

inline fun <T> Sequence<T>.forAtLeast(k: Int, fn: (T) -> Unit): Sequence<T> = apply { toList().forAtLeast(k, fn) }
inline fun <T> Array<T>.forAtLeast(k: Int, fn: (T) -> Unit): Array<T> = apply { toList().forAtLeast(k, fn) }
inline fun <T, C : Collection<T>> C.forAtLeast(k: Int, fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < k) {
      val msg = "${passed.size} elements passed but expected at least $k"
      buildAssertionError(msg, results)
   }
}

inline fun <T> Sequence<T>.forAtMostOne(fn: (T) -> Unit): Sequence<T> = apply { toList().forAtMostOne(fn) }
inline fun <T> Array<T>.forAtMostOne(fn: (T) -> Unit): Array<T> = apply { toList().forAtMostOne(fn) }
inline fun <T, C : Collection<T>> C.forAtMostOne(fn: (T) -> Unit) = forAtMost(1, fn)

inline fun <T> Sequence<T>.forAtMost(k: Int, fn: (T) -> Unit): Sequence<T> = apply { toList().forAtMost(k, fn) }
inline fun <T> Array<T>.forAtMost(k: Int, fn: (T) -> Unit): Array<T> = apply { toList().forAtMost(k, fn) }
inline fun <T, C : Collection<T>> C.forAtMost(k: Int, fn: (T) -> Unit): C = apply {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size > k) {
      val msg = "${passed.size} elements passed but expected at most $k"
      buildAssertionError(msg, results)
   }
}

inline fun <T> Sequence<T>.forNone(fn: (T) -> Unit): Sequence<T> = apply { toList().forNone(fn) }
inline fun <T> Array<T>.forNone(fn: (T) -> Unit): Array<T> = apply { toList().forNone(fn) }
inline fun <T, C : Collection<T>> C.forNone(f: (T) -> Unit): C = apply {
   val results = runTests(this, f)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isNotEmpty()) {
      val msg = "${passed.size} elements passed but expected ${0}"
      buildAssertionError(msg, results)
   }
}

/**
 * Checks that [Sequence] consists of a single element, which passes the given assertion block [fn]
 * and returns the element
 * */
fun <T> Sequence<T>.forSingle(fn: (T) -> Unit): T = toList().forSingle(fn)

/**
 * Checks that [Array] consists of a single element, which passes the given assertion block [fn]
 * and returns the element
 * */
fun <T> Array<T>.forSingle(fn: (T) -> Unit): T = toList().forSingle(fn)

/**
 * Checks that [Collection] consists of a single element, which passes the given assertion block [fn]
 * and returns the element
 * */
fun <T, C : Collection<T>> C.forSingle(f: (T) -> Unit): T = run {
   val results = runTests(this, f)
   when (results.size) {
      1 -> when (results[0]) {
         is ElementPass<T> -> results[0].value()
         else -> buildAssertionError("Expected a single element to pass, but it failed.", results)
      }
      0 -> throw failure("Expected a single element in the collection, but it was empty.")
      else -> buildAssertionError("Expected a single element in the collection, but found ${results.size}.", results)
   }
}
