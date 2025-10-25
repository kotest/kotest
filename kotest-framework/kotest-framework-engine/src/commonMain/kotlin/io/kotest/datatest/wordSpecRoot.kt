package io.kotest.datatest

import io.kotest.core.spec.style.scopes.WordSpecRootScope
import io.kotest.core.spec.style.scopes.WordSpecShouldContainerScope
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
   withWhens(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withWhens(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withShoulds(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   withShoulds(listOf(first, second) + rest, test)
}

fun <T> WordSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(nameFn, listOf(first, second) + rest, test)
}

fun <T> WordSpecRootScope.withWhens(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(nameFn, listOf(first, second) + rest, test)
}

fun <T> WordSpecRootScope.withShoulds(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   withShoulds(nameFn, listOf(first, second) + rest, test)
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
   withWhens(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withWhens(
   ts: Sequence<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withShoulds(
   ts: Sequence<T>,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   withShoulds(ts.toList(), test)
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
   withWhens(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withWhens(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withShoulds(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   withShoulds(nameFn, ts.toList(), test)
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
   withWhens({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withWhens(
   ts: Iterable<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withWhens({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> WordSpecRootScope.withShoulds(
   ts: Iterable<T>,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   withShoulds({ StableIdents.getStableIdentifier(it) }, ts, test)
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
   withWhens(nameFn, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> WordSpecRootScope.withWhens(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      nameFn(t) `when` { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> WordSpecRootScope.withShoulds(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      nameFn(t) should { this.test(t) }
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
   withWhens(data, test)
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> WordSpecRootScope.withWhens(
   data: Map<String, T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      name `when` { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> WordSpecRootScope.withShoulds(
   data: Map<String, T>,
   test: suspend WordSpecShouldContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      name should { this.test(t) }
   }
}
