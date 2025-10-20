package io.kotest.datatest

import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withThens(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   ts: Sequence<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   ts: Sequence<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withThens(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   ts: Iterable<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   ts: Iterable<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withThens({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withThens(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withThens(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   ts.forEach { t -> and(nameFn(t)) { this.test(t) } }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   ts.forEach { t -> then(nameFn(t)) { this.test(t) } }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> BehaviorSpecWhenContainerScope.withData(
   data: Map<String, T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withAnds(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withAndsMap")
suspend fun <T> BehaviorSpecWhenContainerScope.withAnds(
   data: Map<String, T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) -> and(name) { this.test(t) } }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withThenssMap")
suspend fun <T> BehaviorSpecWhenContainerScope.withThens(
   data: Map<String, T>,
   test: suspend TestScope.(T) -> Unit
) {
   data.forEach { (name, t) -> then(name) { this.test(t) } }
}
