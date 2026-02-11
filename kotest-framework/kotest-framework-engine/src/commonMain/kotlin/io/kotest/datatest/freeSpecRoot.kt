package io.kotest.datatest

import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.core.spec.style.scopes.FreeSpecRootScope
import io.kotest.core.spec.style.scopes.FreeSpecTerminalScope
import io.kotest.engine.stable.StableIdents

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withData(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withContexts(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withTests(
   first: T,
   second: T, // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(listOf(first, second) + rest, test)
}

fun <T> FreeSpecRootScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

fun <T> FreeSpecRootScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

fun <T> FreeSpecRootScope.withTests(
   nameFn: (T) -> String,
   first: T,
   second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
   vararg rest: T,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withData(
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withContexts(
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withTests(
   ts: Sequence<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withTests(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(nameFn, ts.toList(), test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withData(
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withContexts(
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
fun <T> FreeSpecRootScope.withTests(
   ts: Iterable<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> FreeSpecRootScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> FreeSpecRootScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   ts.forEach { t ->
      nameFn(t).config().minus { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each element of [ts].
 *
 * The test name will be generated from the given [nameFn] function.
 */
fun <T> FreeSpecRootScope.withTests(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   ts.forEach { t ->
      nameFn(t).config() { FreeSpecTerminalScope(this).test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> FreeSpecRootScope.withData(
   data: Map<String, T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(data, test)
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> FreeSpecRootScope.withContexts(
   data: Map<String, T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   data.forEach { (name, t) ->
      name.config().minus { this.test(t) }
   }
}

/**
 * Registers tests at the root level for each tuple of [data], with the first value of the tuple
 * used as the test name, and the second value passed to the test.
 */
fun <T> FreeSpecRootScope.withTests(
   data: Map<String, T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   data.forEach { (name, t) ->
      name.config() { FreeSpecTerminalScope(this).test(t) }
   }
}
