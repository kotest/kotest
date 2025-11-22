package io.kotest.datatest

import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
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
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   ts: Sequence<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   ts: Sequence<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   ts: Sequence<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withThens(ts.toList(), test)
}


/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   ts: Iterable<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   ts: Iterable<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   ts: Iterable<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withWhens({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withThens({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
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
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   withWhens(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
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
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   ts.forEach { t -> and(nameFn(t)) { this.test(t) } }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   ts.forEach { t -> `when`(nameFn(t)) { this.test(t) } }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
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
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withData(
   data: Map<String, T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   withAnds(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withAndsMap")
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withAnds(
   data: Map<String, T>,
   test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) -> and(name) { this.test(t) } }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withWhensMap")
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withWhens(
   data: Map<String, T>,
   test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) -> `when`(name) { this.test(t) } }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withThensMap")
@KotestTestScope
suspend fun <T> BehaviorSpecGivenContainerScope.withThens(
   data: Map<String, T>,
   test: suspend TestScope.(T) -> Unit
) {
   data.forEach { (name, t) -> then(name) { this.test(t) } }
}
