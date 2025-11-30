package io.kotest.datatest

import io.kotest.core.spec.style.scopes.ExpectSpecContainerScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withContexts(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withExpects(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withExpects(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withData(
   ts: Sequence<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withContexts(
   ts: Sequence<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withExpects(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withExpects(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withData(
   ts: Iterable<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withContexts(
   ts: Iterable<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ExpectSpecContainerScope.withExpects(
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withExpects({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withExpects(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withExpects(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withExpects(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withExpects(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      context(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ExpectSpecContainerScope.withExpects(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   ts.forEach { t ->
      expect(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> ExpectSpecContainerScope.withData(
   data: Map<String, T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   withContexts(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withContextsMap")
suspend fun <T> ExpectSpecContainerScope.withContexts(
   data: Map<String, T>,
   test: suspend ExpectSpecContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      context(name) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withExpectsMap")
suspend fun <T> ExpectSpecContainerScope.withExpects(
   data: Map<String, T>,
   test: suspend TestScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      expect(name) { this.test(t) }
   }
}
