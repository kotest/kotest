package io.kotest.datatest

import io.kotest.core.spec.style.scopes.FeatureSpecContainerScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents
import kotlin.jvm.JvmName

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withData(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   first: T,
   second: T, // we need second to help the compiler disambiguate between this and the sequence version
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withScenarios(listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withData(
   ts: Sequence<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   ts: Sequence<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withScenarios(ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withData(
   ts: Iterable<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   ts: Iterable<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdents].
 */
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withScenarios({ StableIdents.getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   nameFn: (T) -> String,
   ts: Sequence<T>,
   test: suspend TestScope.(T) -> Unit
) {
   withScenarios(nameFn, ts.toList(), test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withData(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given test context for each element.
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   nameFn: (T) -> String,
   first: T,
   second: T,
   vararg rest: T,
   test: suspend TestScope.(T) -> Unit
) {
   withScenarios(nameFn, listOf(first, second) + rest, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withData(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(nameFn, ts, test)
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   ts.forEach { t ->
      feature(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given [T] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   nameFn: (T) -> String,
   ts: Iterable<T>,
   test: suspend TestScope.(T) -> Unit
) {
   ts.forEach { t ->
      scenario(nameFn(t)) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
suspend fun <T> FeatureSpecContainerScope.withData(
   data: Map<String, T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   withFeatures(data, test)
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withFeaturesMap")
suspend fun <T> FeatureSpecContainerScope.withFeatures(
   data: Map<String, T>,
   test: suspend FeatureSpecContainerScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      feature(name) { this.test(t) }
   }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withScenariosMap")
suspend fun <T> FeatureSpecContainerScope.withScenarios(
   data: Map<String, T>,
   test: suspend TestScope.(T) -> Unit
) {
   data.forEach { (name, t) ->
      scenario(name) { this.test(t) }
   }
}
