package io.kotest.datatest

import io.kotest.core.spec.style.scopes.WordSpecRootScope
import io.kotest.core.spec.style.scopes.WordSpecWhenContainerScope
import io.kotest.engine.stable.StableIdents

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(listOf(first, second) + rest, test)
}

fun <T> WordSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withData(
   ts: Sequence<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withData(
   ts: Iterable<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> WordSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      nameFn(t) `when` { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> WordSpecRootScope.withData(
   data: Map<String, T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      name `when` { this.test(t) }
   }
}
