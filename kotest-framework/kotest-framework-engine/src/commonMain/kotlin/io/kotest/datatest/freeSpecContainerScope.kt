package io.kotest.datatest

import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.core.spec.style.scopes.FreeSpecTerminalScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withContexts(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withTests(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withData(
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withContexts(
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withTests(
   ts: Sequence<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withData(
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withContexts(
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FreeSpecContainerScope.withTests(
   ts: Iterable<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withTests(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withTests(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   withTests(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   ts.forEach { t ->
      nameFn(t).config(dataTestTagConfig).minus { test(t) }
   }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FreeSpecContainerScope.withTests(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   ts.forEach { t ->
      nameFn(t).config(dataTestTagConfig) { FreeSpecTerminalScope(this).test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> FreeSpecContainerScope.withData(
   data: Map<String, T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   withContexts(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withContextsMap")
suspend fun <T> FreeSpecContainerScope.withContexts(
   data: Map<String, T>,
   test: suspend FreeSpecContainerScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   data.forEach { (name, t) ->
      name.config(dataTestTagConfig).minus { test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withTestsMap")
suspend fun <T> FreeSpecContainerScope.withTests(
   data: Map<String, T>,
   test: suspend FreeSpecTerminalScope.(T) -> Unit
) {
   val dataTestTagConfig = getDataTestTagConfig()
   data.forEach { (name, t) ->
      name.config(dataTestTagConfig) { FreeSpecTerminalScope(this).test(t) }
   }
}
