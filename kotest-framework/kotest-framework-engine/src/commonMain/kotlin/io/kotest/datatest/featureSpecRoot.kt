package io.kotest.datatest

import io.kotest.core.spec.style.scopes.FeatureSpecContainerScope
import io.kotest.core.spec.style.scopes.FeatureSpecRootScope
import io.kotest.engine.stable.StableIdents

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FeatureSpecRootScope.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withData(listOf(first, second) + rest, test)
}

fun <T> FeatureSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withData(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FeatureSpecRootScope.withData(
   ts: Sequence<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withData(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FeatureSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FeatureSpecRootScope.withData(
   ts: Iterable<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withData({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> FeatureSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      feature(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> FeatureSpecRootScope.withData(data: Map<String, T>, test: suspend FeatureSpecContainerScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      feature(name) { this.test(t) }
   }
}
