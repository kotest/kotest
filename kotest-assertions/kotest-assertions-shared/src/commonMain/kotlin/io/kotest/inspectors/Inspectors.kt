package io.kotest.inspectors

fun <T> Sequence<T>.forAll(fn: (T) -> Unit): Sequence<T> {
   toList().forAll(fn)
   return this
}

fun <T> Array<T>.forAll(fn: (T) -> Unit): Array<T> {
   asList().forAll(fn)
   return this
}

fun <T, C : Collection<T>> C.forAll(fn: (T) -> Unit): C {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < this.size) {
      val msg = "${passed.size} elements passed but expected ${this.size}"
      buildAssertionError(msg, results)
   }
   return this
}

fun <T> Sequence<T>.forOne(fn: (T) -> Unit): Sequence<T> {
   toList().forOne(fn)
   return this
}

fun <T> Array<T>.forOne(fn: (T) -> Unit): Array<T> {
   asList().forOne(fn)
   return this
}

fun <T, C : Collection<T>> C.forOne(fn: (T) -> Unit): C = forExactly(1, fn)

fun <T> Sequence<T>.forExactly(k: Int, fn: (T) -> Unit): Sequence<T> {
   toList().forExactly(k, fn)
   return this
}

fun <T> Array<T>.forExactly(k: Int, fn: (T) -> Unit): Array<T> {
   toList().forExactly(k, fn)
   return this
}

fun <T, C : Collection<T>> C.forExactly(k: Int, fn: (T) -> Unit): C {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size != k) {
      val msg = "${passed.size} elements passed but expected $k"
      buildAssertionError(msg, results)
   }
   return this
}

fun <T> Sequence<T>.forSome(fn: (T) -> Unit): Sequence<T> {
   toList().forSome(fn)
   return this
}

fun <T> Array<T>.forSome(fn: (T) -> Unit): Array<T> {
   toList().forSome(fn)
   return this
}

fun <T, C : Collection<T>> C.forSome(fn: (T) -> Unit): C {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isEmpty()) {
      buildAssertionError("No elements passed but expected at least one", results)
   } else if (passed.size == size) {
      buildAssertionError("All elements passed but expected < $size", results)
   }
   return this
}

fun <T> Sequence<T>.forAny(fn: (T) -> Unit): Sequence<T> {
   toList().forAny(fn)
   return this
}

fun <T> Array<T>.forAny(fn: (T) -> Unit): Array<T> {
   toList().forAny(fn)
   return this
}

fun <T, C : Collection<T>> C.forAny(fn: (T) -> Unit): C {
   forAtLeastOne(fn)
   return this
}

fun <T> Sequence<T>.forAtLeastOne(fn: (T) -> Unit): Sequence<T> {
   toList().forAtLeastOne(fn)
   return this
}

fun <T> Array<T>.forAtLeastOne(fn: (T) -> Unit): Array<T> {
   toList().forAtLeastOne(fn)
   return this
}

fun <T, C : Collection<T>> C.forAtLeastOne(f: (T) -> Unit) = forAtLeast(1, f)

fun <T> Sequence<T>.forAtLeast(k: Int, fn: (T) -> Unit): Sequence<T> {
   toList().forAtLeast(k, fn)
   return this
}

fun <T> Array<T>.forAtLeast(k: Int, fn: (T) -> Unit): Array<T> {
   toList().forAtLeast(k, fn)
   return this
}

fun <T, C : Collection<T>> C.forAtLeast(k: Int, fn: (T) -> Unit): C {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size < k) {
      val msg = "${passed.size} elements passed but expected at least $k"
      buildAssertionError(msg, results)
   }
   return this
}

fun <T> Sequence<T>.forAtMostOne(fn: (T) -> Unit): Sequence<T> {
   toList().forAtMostOne(fn)
   return this
}

fun <T> Array<T>.forAtMostOne(fn: (T) -> Unit): Array<T> {
   toList().forAtMostOne(fn)
   return this
}

fun <T, C : Collection<T>> C.forAtMostOne(fn: (T) -> Unit) = forAtMost(1, fn)

fun <T> Sequence<T>.forAtMost(k: Int, fn: (T) -> Unit): Sequence<T> {
   toList().forAtMost(k, fn)
   return this
}

fun <T> Array<T>.forAtMost(k: Int, fn: (T) -> Unit): Array<T> {
   toList().forAtMost(k, fn)
   return this
}

fun <T, C : Collection<T>> C.forAtMost(k: Int, fn: (T) -> Unit): C {
   val results = runTests(this, fn)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.size > k) {
      val msg = "${passed.size} elements passed but expected at most $k"
      buildAssertionError(msg, results)
   }
   return this
}

fun <T> Sequence<T>.forNone(fn: (T) -> Unit): Sequence<T> {
   toList().forNone(fn)
   return this
}

fun <T> Array<T>.forNone(fn: (T) -> Unit): Array<T> {
   toList().forNone(fn)
   return this
}

fun <T, C : Collection<T>> C.forNone(f: (T) -> Unit): C {
   val results = runTests(this, f)
   val passed = results.filterIsInstance<ElementPass<T>>()
   if (passed.isNotEmpty()) {
      val msg = "${passed.size} elements passed but expected ${0}"
      buildAssertionError(msg, results)
   }
   return this
}
