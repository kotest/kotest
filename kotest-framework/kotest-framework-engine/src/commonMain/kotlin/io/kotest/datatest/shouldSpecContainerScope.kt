package io.kotest.datatest

import io.kotest.core.spec.style.scopes.ShouldSpecContainerScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withContexts(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withShoulds(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withData(
   ts: Sequence<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withContexts(
   ts: Sequence<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withShoulds(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withData(
   ts: Iterable<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withContexts(
   ts: Iterable<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withShoulds({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withShoulds(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withShoulds(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withContexts(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      context(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   ts.forEach { t ->
      should(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> ShouldSpecContainerScope.withData(
   data: Map<String, T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   withContexts(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withContextsMap")
suspend fun <T> ShouldSpecContainerScope.withContexts(
   data: Map<String, T>,
   test: suspend ShouldSpecContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      context(name) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withShouldsMap")
suspend fun <T> ShouldSpecContainerScope.withShoulds(
   data: Map<String, T>,
   test: suspend TestScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      should(name) { this.test(t) }
   }
}
