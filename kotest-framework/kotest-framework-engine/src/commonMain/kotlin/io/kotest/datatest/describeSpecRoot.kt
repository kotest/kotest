package io.kotest.datatest

import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import io.kotest.core.spec.style.scopes.DescribeSpecRootScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withContexts(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withDescribes(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withDescribes(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withIts(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withIts(listOf(first, second) + rest, test)
}

fun <T> DescribeSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

fun <T> DescribeSpecRootScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

fun <T> DescribeSpecRootScope.withDescribes(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withDescribes(nameFn, listOf(first, second) + rest, test)
}

fun <T> DescribeSpecRootScope.withIts(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withIts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withData(
   ts: Sequence<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withContexts(
   ts: Sequence<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withDescribes(
   ts: Sequence<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withDescribes(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withIts(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withIts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withDescribes(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withDescribes(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withIts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withIts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withData(ts: Iterable<T>, test: suspend DescribeSpecContainerScope.(T) -> Unit) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withContexts(ts: Iterable<T>, test: suspend DescribeSpecContainerScope.(T) -> Unit) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withDescribes(ts: Iterable<T>, test: suspend DescribeSpecContainerScope.(T) -> Unit) {
   withDescribes({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> DescribeSpecRootScope.withIts(ts: Iterable<T>, test: suspend TestScope.(T) -> Unit) {
   withIts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> DescribeSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> DescribeSpecRootScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
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
fun <T> DescribeSpecRootScope.withDescribes(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      describe(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> DescribeSpecRootScope.withIts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   ts.forEach { t ->
      it(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> DescribeSpecRootScope.withData(data: Map<String, T>, test: suspend DescribeSpecContainerScope.(T) -> Unit) {
   withContexts(data, test)
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> DescribeSpecRootScope.withContexts(data: Map<String, T>, test: suspend DescribeSpecContainerScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      context(name) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> DescribeSpecRootScope.withDescribes(
   data: Map<String, T>,
   test: suspend DescribeSpecContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      describe(name) { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> DescribeSpecRootScope.withIts(data: Map<String, T>, test: suspend TestScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      it(name) { this.test(t) }
   }
}
