package io.kotest.datatest

import io.kotest.core.spec.style.scopes.BehaviorSpecContextContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecRootScope
import io.kotest.engine.stable.StableIdents

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withContexts(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withGivens(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withGivens(listOf(first, second) + rest, test)
}

fun <T> BehaviorSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

fun <T> BehaviorSpecRootScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

fun <T> BehaviorSpecRootScope.withGivens(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withGivens(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withData(
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withContexts(
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withGivens(
   ts: Sequence<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withGivens(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withGivens(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withGivens(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withData(
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withContexts(
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> BehaviorSpecRootScope.withGivens(
   ts: Iterable<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withGivens({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> BehaviorSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> BehaviorSpecRootScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      context(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> BehaviorSpecRootScope.withGivens(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      given(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> BehaviorSpecRootScope.withData(
   data: Map<String, T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   withContexts(data, test)
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> BehaviorSpecRootScope.withContexts(
   data: Map<String, T>,
   test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      context(name) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> BehaviorSpecRootScope.withGivens(
   data: Map<String, T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      given(name) { this.test(t) }
   }
}
