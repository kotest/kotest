package io.kotest.datatest

import io.kotest.core.spec.style.scopes.WordSpecWhenContainerScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> WordSpecWhenContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> WordSpecWhenContainerScope.withData(
   ts: Sequence<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> WordSpecWhenContainerScope.withData(
   ts: Iterable<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> WordSpecWhenContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> WordSpecWhenContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   withData(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> WordSpecWhenContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      nameFn(t) `when` { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> WordSpecWhenContainerScope.withData(
   data: Map<String, T>,
   test: suspend WordSpecWhenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      name `when` { this.test(t) }
   }
}
