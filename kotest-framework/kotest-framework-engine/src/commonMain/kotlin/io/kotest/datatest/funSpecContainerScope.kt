package io.kotest.datatest

import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withContexts(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withTests(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withTests(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withData(
   ts: Sequence<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withContexts(
   ts: Sequence<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withTests(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withTests(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withData(
   ts: Iterable<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withContexts(
   ts: Iterable<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FunSpecContainerScope.withTests(
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withTests({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withTests(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withTests(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withTests(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withTests(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      context(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FunSpecContainerScope.withTests(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   ts.forEach { t ->
      test(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> FunSpecContainerScope.withData(data: Map<String, T>, test: suspend FunSpecContainerScope.(T) -> Unit) {
   withContexts(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withContextsMap")
suspend fun <T> FunSpecContainerScope.withContexts(
   data: Map<String, T>,
   test: suspend FunSpecContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      context(name) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withTestsMap")
suspend fun <T> FunSpecContainerScope.withTests(data: Map<String, T>, test: suspend TestScope.(T) -> Unit) {
   data.forEach { (name, t) ->
      test(name) { this.test(t) }
   }
}
